# Sistema Inteligente de Gestão de Capacidade - SUS (MVP)

> **Hackathon Pós-Tech - Arquitetura e Desenvolvimento Java**  
> **Tema:** Inovação para otimização de atendimento no SUS (Combate ao Absenteísmo)

## 📋 Visão Geral do Projeto

Este projeto é um **Middleware de Orquestração** desenvolvido para modernizar a gestão de agendas do Sistema Único de Saúde (SUS). O objetivo principal é reduzir o absenteísmo (pacientes que faltam e não avisam) e otimizar a ocupação dos médicos através de uma abordagem ativa e reativa via WhatsApp.

A solução atua como uma camada inteligente acoplada aos sistemas legados, não substituindo o prontuário eletrônico, mas enriquecendo a experiência do paciente e a eficiência operacional.

### 🚀 Diferenciais Técnicos
*   **Arquitetura Orientada a Eventos (EDA):** Alta performance e desacoplamento.
*   **Gestão de Concorrência (Redis Lock):** Sistema de "Repescagem" segura (primeiro a chegar leva a vaga).
*   **Resiliência:** Comunicação assíncrona para integração com WhatsApp.
*   **Trava Social (Inclusão):** Lógica que protege pacientes sem acesso digital.

---

## 🏗️ Arquitetura da Solução

O sistema foi desenhado utilizando o padrão de **Microsserviços** em um **Monorepo**, facilitando a gestão do MVP enquanto demonstra separação clara de responsabilidades (CQRS-like).

### Diagrama de Componentes

```mermaid
graph TD
    subgraph "Mundo Externo"
        Legado[Sistema Legado SUS]
        Zap[WhatsApp (Twilio)]
        User[Paciente]
    end

    subgraph "Infraestrutura Local (Docker)"
        RabbitMQ((RabbitMQ))
        Postgres[(PostgreSQL)]
        Redis[(Redis)]
    end

    subgraph "Microsserviços (Java 21)"
        MS_Ingestao[ms-ingestao]
        MS_Processamento[ms-processamento]
        MS_Notificacao[ms-notificacao]
    end

    %% Fluxos
    Legado -->|HTTP POST| MS_Ingestao
    User -->|WhatsApp| Zap
    Zap -->|Webhook| MS_Ingestao
    
    MS_Ingestao -->|Pub Evento| RabbitMQ
    
    RabbitMQ -->|Sub Input| MS_Processamento
    MS_Processamento -->|Persistência| Postgres
    MS_Processamento -->|Lock Distribuído| Redis
    MS_Processamento -->|Pub Notificação| RabbitMQ
    
    RabbitMQ -->|Sub Notificação| MS_Notificacao
    MS_Notificacao -->|API Call| Zap
```

---

## 🧩 Detalhamento dos Microsserviços

### 1. `ms-ingestao` (Gatekeeper)
Porta de entrada do sistema. Serviço *stateless* focado em alta disponibilidade de escrita.
*   **Responsabilidade:** Receber cargas de agendamento do legado e webhooks do WhatsApp.
*   **Tecnologia:** Spring Web, Spring AMQP.
*   **Input:** REST API.
*   **Output:** Filas RabbitMQ (`sus.input.carga-agendamento`, `sus.input.resposta-usuario`).

### 2. `ms-processamento` (Core Domain)
O cérebro da operação. Contém toda a regra de negócio e gestão de estado.
*   **Responsabilidade:** Máquina de estados do agendamento, Jobs (Schedulers) e Lógica de Repescagem.
*   **Tecnologia:** Spring Data JPA, Spring Data Redis, Spring Scheduler.
*   **Input:** Filas RabbitMQ.
*   **Output:** Persistência (Postgres), Lock (Redis) e Eventos de Notificação.

### 3. `ms-notificacao` (Worker)
Serviço de I/O responsável pela entrega da mensagem.
*   **Responsabilidade:** Integração com Twilio Sandbox e tratativa de retries.
*   **Tecnologia:** Spring WebFlux (WebClient), Spring AMQP.
*   **Input:** Fila `sus.core.notificacao`.
*   **Output:** Chamada HTTP para API Externa.

---

## 🛠️ Stack Tecnológica

*   **Linguagem:** Java 21 (Records, Virtual Threads, Pattern Matching).
*   **Framework:** Spring Boot 3.2+.
*   **Mensageria:** RabbitMQ.
*   **Banco Relacional:** PostgreSQL 16.
*   **Cache & Lock:** Redis 7.
*   **Containerização:** Docker & Docker Compose.
*   **Integração Externa:** Twilio Sandbox for WhatsApp.
*   **Túnel Local:** Ngrok (para expor o webhook localmente).

---

## ⚙️ Regras de Negócio Implementadas

### 📅 1. Confirmação Ativa (D-7)
Sete dias antes da consulta, o sistema busca agendamentos pendentes e envia solicitação de confirmação via WhatsApp.
*   *Opções:* 1-Confirmar, 2-Reagendar, 3-Cancelar.

### 🛡️ 2. Trava de Segurança Social (D-2)
Faltando 48h para a consulta, se o paciente **não respondeu**, o sistema assume **Confirmação Automática**.
*   *Justificativa:* Proteção a idosos e excluídos digitais. O "silêncio" não pode cancelar o atendimento.

### ⚡ 3. Repescagem Inteligente (Concorrência)
Quando um paciente cancela (Opção 3), o sistema dispara um algoritmo de realocação:
1.  Busca os 3 primeiros pacientes na `Fila de Espera`.
2.  Envia oferta: *"Surgiu uma vaga para amanhã. Digite SIM."*
3.  **Race Condition:** Se múltiplos pacientes respondem "SIM", o **Redis Atomic Lock (`SETNX`)** garante que apenas o primeiro obtenha a vaga. Os demais recebem uma mensagem de "Vaga já preenchida".

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
*   Java 21 JDK
*   Maven 3.8+
*   Docker & Docker Compose
*   Conta na Twilio (Sandbox Gratuita)
*   Ngrok (instalado)

### Passo 1: Infraestrutura
Suba os containers de banco de dados e mensageria:
```bash
docker-compose up -d
```

### Passo 2: Configuração de Túnel (Ngrok)
Para receber respostas do WhatsApp no seu ambiente local:
```bash
ngrok http 8080
# Copie a URL gerada (ex: https://abcde.ngrok-free.app)
# Cole no painel da Twilio em "Sandbox Settings -> When a message comes in"
# Adicione o sufixo: /api/v1/webhook/twilio
```

### Passo 3: Execução dos Serviços
Como é um monorepo, você pode rodar via IDE ou terminal em abas separadas:

**Terminal 1 (Ingestão):**
```bash
cd ms-ingestao && mvn spring-boot:run
```
**Terminal 2 (Processamento):**
```bash
cd ms-processamento && mvn spring-boot:run
```
**Terminal 3 (Notificação):**
```bash
cd ms-notificacao && mvn spring-boot:run
```

---

## 📡 Documentação da API (Ingestão)

### 1. Carga de Agendamento (Simulando Legado SUS)
**POST** `/api/v1/integracao/agendamentos`
```json
{
  "idExterno": "SUS-100200",
  "paciente": {
    "nome": "João da Silva",
    "telefone": "5511999998888",
    "possuiWhatsapp": true
  },
  "consulta": {
    "dataHora": "2025-10-20T14:00:00",
    "medico": "Dr. House",
    "especialidade": "CLINICA_GERAL",
    "unidadeId": "UBS-VILA-MARIANA"
  }
}
```

### 2. Webhook Twilio (Callback)
**POST** `/api/v1/webhook/twilio`
*Content-Type: application/x-www-form-urlencoded*
*   Recebe os parâmetros padrão da Twilio (`From`, `Body`) e encaminha para a fila de processamento.

---

## 🧪 Roteiro de Teste (MVP Video)

1.  **Cenário Feliz:** Inserir agendamento para D+7. Verificar recebimento do WhatsApp. Responder "1". Verificar status `CONFIRMADO_PACIENTE` no banco.
2.  **Cenário Trava Social:** Inserir agendamento para D+2. Aguardar execução do Job. Verificar status `CONFIRMADO_AUTOMATICO`.
3.  **Cenário Repescagem:**
    *   Popular `FilaEspera` no banco.
    *   Enviar "3" (Cancelar) em um agendamento existente.
    *   Verificar envio de ofertas para a fila.
    *   Simular resposta "SIM" de dois números diferentes rapidamente.
    *   Validar logs de bloqueio do Redis (um sucesso, um falha).

---

## Créditos

Projeto desenvolvido para o **Hackton FIAP** como parte da entrega da fase 5.

Autores:
- [@FMTSL - Felipe Matos](https://github.com/FMTSL)
- [@gustavoleite - Gustavo Leite](https://github.com/gustavoleite)
- [@JefHerc - Jeferson Matos](https://github.com/JefHerc)
- [@kellycps - Kelly](https://github.com/kellycps)
- [@MichaelPBarroso - Michael Barroso](https://github.com/MichaelPBarroso)