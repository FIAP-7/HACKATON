# ADR-001: Arquitetura do Microsserviço de Ingestão

Este documento formaliza as decisões arquiteturais tomadas para o microsserviço de **Ingestão (`ms-ingestao`)**.

**Status:** Aceito  
**Data:** 28/01/2026  
**Contexto:** MVP de Otimização de Agendamentos do SUS  
**Responsável:** Arquiteto de Software, Gustavo Leite

## 1. Contexto do Problema
O ecossistema do SUS possui sistemas legados que precisam enviar grandes cargas de dados de consultas agendadas. Simultaneamente, o sistema deve receber *callbacks* (Webhooks) do WhatsApp com respostas de pacientes em tempo real.

O desafio é garantir que a entrada de dados seja:
1.  **Alta Disponibilidade:** A API não pode rejeitar requisições de carga mesmo se o banco de dados principal estiver lento ou em manutenção.
2.  **Segura:** Diferenciar quem envia os dados (Sistema Legado vs Webhook Público).
3.  **Validada:** Impedir que dados corrompidos entrem no fluxo de processamento.
4.  **Desacoplada:** A resposta para o cliente HTTP deve ser imediata (Latência mínima).

## 2. Decisões Arquiteturais

### 2.1. Padrão de Arquitetura: Stateless Gatekeeper
**Decisão:** O `ms-ingestao` será um serviço puramente **Stateless** (sem banco de dados relacional acoplado).
*   **Justificativa:** Sua única função é receber, validar e enfileirar. Remover a dependência de banco de dados elimina o principal ponto de falha em operações de escrita (IO de disco) e permite escalar horizontalmente instâncias deste serviço sem complexidade.

### 2.2. Comunicação Assíncrona (Fire-and-Forget)
**Decisão:** Utilização do padrão *Producer* com **RabbitMQ**.
*   **Justificativa:** O sistema legado espera um `HTTP 202 Accepted` rápido. Não devemos processar regras de negócio (D-7, Trava Social) neste momento. O serviço apenas converte o JSON em um Evento e publica na *Exchange*. Se o consumidor (`ms-processamento`) cair, as mensagens ficam persistidas na fila, garantindo durabilidade.

### 2.3. Estratégia de Segurança Híbrida
**Decisão:** Implementação de dois mecanismos distintos de autenticação via **Spring Security**.
1.  **OAuth2 / OIDC (Keycloak):** Para os endpoints de integração com o Sistema Legado (`/api/v1/integracao/**`).
    *   *Justificativa:* Padrão de mercado para segurança corporativa, permite gestão de Roles (`ROLE_INTEGRADOR`) e auditoria centralizada.
2.  **Basic Auth:** Para os endpoints de Webhook (`/api/v1/webhook/**`).
    *   *Justificativa:* Webhooks de terceiros (Twilio/WhatsApp) geralmente operam com assinaturas digitais ou autenticação básica. É mais simples de configurar na Sandbox e mantém a segurança isolada do fluxo corporativo.

### 2.4. Validação de Contrato (Fail-Fast)
**Decisão:** Uso de **Jakarta Bean Validation** (`@Valid`, `@NotNull`, `@Pattern`) na entrada do Controller.
*   *Justificativa:* Evita "poluir" a fila de mensagens com dados inúteis. Se o CPF estiver errado ou data inválida, o erro é retornado imediatamente (`HTTP 400`) para a origem, sem onerar o processamento assíncrono.

### 2.5. Stack Tecnológica
**Decisão:** Java 21 + Spring Boot 3.2 (Web + Security + AMQP).
*   *Justificativa:* O Java 21 oferece **Virtual Threads** (Project Loom). Como este serviço é intensivo em I/O (recebe HTTP -> manda para RabbitMQ), as Virtual Threads aumentam drasticamente o *throughput* sem bloquear threads do sistema operacional, ideal para um gateway de alta volumetria.

---

## 3. Especificação Técnica dos Componentes

Ao solicitar o código, forneça estas especificações para a IA:

### 3.1. Entidades de Transferência (DTOs - Java Records)
Devem ser imutáveis.
*   **`AgendamentoInputDto`**: Mapeia o JSON do legado.
    *   Campos: `idExterno` (String), `paciente` (Objeto), `consulta` (Objeto).
    *   Validações: `@NotNull` em todos, `@PastOrPresent` proibido na data da consulta.
*   **`WebhookTwilioDto`**: Mapeia o `Map<String, String>` recebido do `application/x-www-form-urlencoded`.

### 3.2. Filas e Exchanges (RabbitMQ)
*   **Exchange:** `sus.direct.exchange` (Tipo Direct).
*   **Routing Key 1:** `rota.carga.agendamento` -> Fila: `sus.input.carga-agendamento`.
*   **Routing Key 2:** `rota.resposta.usuario` -> Fila: `sus.input.resposta-usuario`.
*   **Serialização:** Configurar `Jackson2JsonMessageConverter` para enviar JSON no corpo da mensagem AMQP, não bytes serializados Java.

### 3.3. Configuração de Segurança (SecurityFilterChain)
Configurar dois beans de filtro ou um filtro com `securityMatcher`:
1.  **Rota `/api/v1/integracao/**`**:
    *   Autenticação: `oauth2ResourceServer` (JWT).
    *   Autorização: Requer autoridade `SCOPE_INTEGRADOR` ou `ROLE_INTEGRADOR`.
2.  **Rota `/api/v1/webhook/**`**:
    *   Autenticação: `httpBasic`.
    *   Usuário em memória: `webhook-user` / `webhook-pass` (definidos via variáveis de ambiente).

### 3.4. Observabilidade (Opcional MVP mas recomendado)
*   Adicionar **Spring Actuator** exposto na porta de gestão (ex: 9090) para Health Check (`/actuator/health`) usado pelo Docker/K8s.

---

## 4. Diagrama de Sequência (Fluxo Interno)

Este fluxo descreve o comportamento esperado do código a ser gerado:

1.  **Requisição:** `POST /api/v1/integracao/agendamentos` (Header: `Authorization: Bearer <JWT>`).
2.  **Segurança:** Spring Security valida assinatura do JWT com Keycloak (cache das chaves públicas). Verifica Role.
3.  **Controller:** Recebe JSON. O framework executa `@Valid`.
    *   *Falha:* Retorna `400 Bad Request` com lista de campos inválidos.
4.  **Service:** Recebe o DTO validado. Adiciona metadados (timestamp de ingestão).
5.  **RabbitTemplate:** Envia para Exchange `sus.direct.exchange`.
6.  **Resposta:** Retorna `202 Accepted` (corpo vazio ou ID de correlação).

---

## 5. Variáveis de Ambiente Necessárias
O código deve ser agnóstico de ambiente, utilizando `application.yml` parametrizado:

*   `SPRING_RABBITMQ_HOST`: Host do Broker.
*   `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`: URL do Keycloak.
*   `WEBHOOK_BASIC_USER`: Usuário para o Twilio.
*   `WEBHOOK_BASIC_PASSWORD`: Senha para o Twilio.