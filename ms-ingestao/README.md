### ms-ingestao — Microsserviço de Ingestão (Gatekeeper)

Serviço de borda responsável por receber cargas de agendamentos vindas do sistema legado do SUS e webhooks de respostas do WhatsApp (Twilio), validá-las e publicá-las em filas RabbitMQ para processamento assíncrono. Projetado para alta disponibilidade, baixo acoplamento e segurança híbrida.

---

### Sumário
- Visão geral e objetivos
- Arquitetura e padrões
- Componentes e pacotes
- Endpoints e segurança
- Mensageria (RabbitMQ)
- Documentação OpenAPI (Swagger)
- Execução (local e Docker)
- Variáveis de ambiente
- Decisões técnicas e justificativas
- Troubleshooting

---

### Visão geral e objetivos
- Receber cargas de agendamento via API REST e respostas de usuários via webhook.
- Validar contratos de entrada de forma fail-fast.
- Publicar eventos de forma assíncrona (fire-and-forget) em RabbitMQ.
- Manter o serviço estateless para facilitar escalabilidade horizontal.
- Proteger endpoints com segurança apropriada ao consumidor (JWT para integradores; Basic Auth para Twilio).

---

### Arquitetura e padrões
- Estilo: Clean Architecture (Hexagonal simplificada)
    - Core independente de frameworks (portas e modelos de evento/command)
    - Application (implementação de casos de uso/serviços)
    - Entrypoint (Controllers/DTOs)
    - Infra (adapters de mensageria, segurança e configuração)
- Comunicação assíncrona com RabbitMQ (EDA — Event-Driven Architecture)
- Segurança híbrida com múltiplas SecurityFilterChains
- Observabilidade básica com Spring Actuator
- Java 21 com Virtual Threads habilitadas para alto throughput de I/O

---

### Componentes e pacotes
- `br.com.sus.ingestao.Application` — bootstrap Spring Boot
- Entrypoint
    - `entrypoint.controller.AgendamentoController` — `POST /api/v1/integracao/agendamentos`
    - `entrypoint.controller.WebhookController` — `POST /api/v1/webhook/twilio`
    - `entrypoint.dto.*` — `AgendamentoRequest`, `PacienteDto`, `ConsultaDto` (Java Records + validação)
    - `entrypoint.handler.ApiExceptionHandler` — tratamento de validações 400
- Core
    - `core.usecase.IngestaoService` — porta de caso de uso
    - `core.usecase.model.AgendamentoCommand` — comando de entrada para o caso de uso (core)
    - `core.event.AgendamentoEvent` — evento de ingestão publicado
    - `core.event.EventoRespostaUsuario` — evento de resposta do usuário (webhook)
    - `core.port.AgendamentoPublisherPort` e `core.port.RespostaPublisherPort` — portas de saída
- Application
    - `application.service.IngestaoServiceImpl` — implementação do caso de uso (observação: a versão antiga no pacote `core.usecase.impl` está `@Deprecated` e sem anotações do Spring; a implementação ativa reside na camada de aplicação)
- Infra
    - `infra.config.RabbitMqConfig` — exchange, filas, bindings e conversor JSON
    - `infra.messaging.RabbitMqAgendamentoProducer` — adapter para publicar `AgendamentoEvent`
    - `infra.messaging.RabbitMqRespostaProducer` — adapter para publicar `EventoRespostaUsuario`
    - `infra.security.KeycloakJwtConverter` — mapeia roles do Keycloak em authorities Spring
    - `infra.security.SecurityConfig` — múltiplas cadeias de segurança
    - `infra.openapi.OpenApiConfig` — definição dos esquemas de segurança no Swagger

Observação: a implementação ativa de `IngestaoService` deve estar sob `br.com.sus.ingestao.application.service`. Caso esteja iniciando o projeto agora, mantenha a implementação neste pacote para preservar o isolamento do core.

---

### Endpoints e segurança
1) Integração de agendamentos (Sistema Legado)
- Método/URI: `POST /api/v1/integracao/agendamentos`
- Segurança: Bearer JWT (OAuth2 Resource Server com Keycloak)
- Autorização: requer role `ROLE_INTEGRADOR`
- Respostas:
    - 202 Accepted — payload aceito e enfileirado
    - 400 Bad Request — validações Bean Validation
    - 401 Unauthorized — sem JWT
    - 403 Forbidden — JWT sem role

Exemplo de requisição:
```bash
curl -X POST "http://localhost:8080/api/v1/integracao/agendamentos" \
  -H "Authorization: Bearer <TOKEN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "idExterno": "SUS-100200",
    "paciente": {"nome": "João da Silva", "telefone": "+5511999998888"},
    "consulta": {"dataHora": "2027-10-20T14:00:00", "medico": "Dr. House", "especialidade": "CLINICA_GERAL", "unidadeId": "UBS-01"}
  }'
```

2) Webhook Twilio (resposta do usuário)
- Método/URI: `POST /api/v1/webhook/twilio`
- Consumes: `application/x-www-form-urlencoded`
- Segurança: HTTP Basic (`sus.security.webhook.username/password`)
- Respostas:
    - 200 OK — recebido e publicado
    - 401 Unauthorized — credenciais ausentes/incorretas

Exemplo de requisição:
```bash
curl -X POST "http://localhost:8080/api/v1/webhook/twilio" \
  -H "Authorization: Basic $(printf "twilio:teste123" | base64)" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "From=whatsapp:+5511999998888" \
  --data-urlencode "Body=SIM"
```

Health check (Actuator)
- `GET /actuator/health` → `{"status":"UP"}`

---

### Mensageria (RabbitMQ)
- Exchange: `sus.direct.exchange` (direct, durável)
- Filas/Bindings:
    - `sus.input.carga-agendamento` ← routing key `rota.carga.agendamento`
    - `sus.input.resposta-usuario` ← routing key `rota.resposta.usuario`
- Conversão de mensagens: `Jackson2JsonMessageConverter` (JSON legível)
- Publicadores (adapters):
    - `RabbitMqAgendamentoProducer` → publica `AgendamentoEvent`
    - `RabbitMqRespostaProducer` → publica `EventoRespostaUsuario`

Fluxo de alto nível:
- `POST /integracao/agendamentos` → validação → monta `AgendamentoEvent` (com `dataIngestao=now`) → publica na exchange → 202
- `POST /webhook/twilio` → sanitiza `From` (remove prefixo `whatsapp:` e não dígitos) → cria `EventoRespostaUsuario` → publica → 200

---

### Documentação OpenAPI (Swagger)
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Esquemas de segurança configurados:
  - `bearerAuth` (HTTP Bearer, JWT)
  - `basicAuth` (HTTP Basic)
- Controllers anotados com `@SecurityRequirement` indicando o esquema adequado por endpoint.

### Autenticação via Client Credentials (Keycloak)
Além do fluxo com usuário/senha (password grant) para testes, o Keycloak foi configurado para suportar o fluxo Client Credentials para integrações de sistema a sistema.

- Client confidencial: `legado-app`
- Secret: `legado-secret-123` (pode ser alterado no arquivo keycloak/realm-sus-hackathon.json)
- Role atribuída ao service account: `INTEGRADOR` (o token emitido conterá `realm_access.roles` com `INTEGRADOR`)

Obter access token (no host, com Keycloak mapeado na porta 8081):
```bash
curl -X POST \
  "http://localhost:8081/realms/sus-hackathon/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=legado-app" \
  -d "client_secret=legado-secret-123"
```
Usar o token no endpoint protegido:
```bash
TOKEN="$(curl -s -X POST \
  "http://localhost:8081/realms/sus-hackathon/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=legado-app" \
  -d "client_secret=legado-secret-123" | jq -r .access_token)"

curl -X POST "http://localhost:8080/api/v1/integracao/agendamentos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idExterno": "SUS-100200",
    "paciente": {"nome": "João da Silva", "telefone": "+5511999998888"},
    "consulta": {"dataHora": "2027-10-20T14:00:00", "medico": "Dr. House", "especialidade": "CLINICA_GERAL", "unidadeId": "UBS-01"}
  }'
```

Observação: Dentro da rede Docker, utilize o host `keycloak:8080` no lugar de `localhost:8081`.

---

### Execução (local e Docker)
Pré-requisitos
- Java 21, Maven 3.8+
- Docker e Docker Compose

Executar via Maven (local)
```bash
# subir dependências (RabbitMQ/Keycloak) com Docker
docker-compose up -d rabbitmq keycloak

# rodar o ms-ingestao
cd ms-ingestao
mvn spring-boot:run
```

Executar tudo via Docker Compose
```bash
docker-compose up -d --build
# Acesse: http://localhost:8080/actuator/health e http://localhost:8080/swagger-ui/index.html
```

Imagem Docker (multi-stage)
- Arquivo: `ms-ingestao/Dockerfile`
- Runtime: `eclipse-temurin:21-jre-alpine`

---

### Variáveis de ambiente
Principais propriedades (veja `application.yml` e `docker-compose.yml`):
- `SPRING_RABBITMQ_HOST` (default `localhost` ou `rabbitmq` no Compose)
- `SPRING_RABBITMQ_USERNAME` / `SPRING_RABBITMQ_PASSWORD`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` (ex.: `http://localhost:8081/realms/sus-hackathon` no local; `http://keycloak:8080/realms/sus-hackathon` no Compose)
- `WEBHOOK_BASIC_USER` / `WEBHOOK_BASIC_PASSWORD` (mapeadas em `sus.security.webhook.username/password`)

Virtual Threads
- Habilitadas por `spring.threads.virtual.enabled=true`

Actuator
- `management.endpoints.web.exposure.include=health,info`

---

### Decisões técnicas e justificativas
- Stateless Gatekeeper
    - Evita dependência de banco de dados no caminho de escrita, reduzindo latência e pontos de falha; facilita o scale-out.
- Event-Driven + RabbitMQ
    - Fire-and-forget com durabilidade de mensagens: quando consumidores caem, as mensagens ficam armazenadas nas filas.
    - Desacoplamento entre borda (ingestão) e núcleo de negócio (processamento).
- Clean Architecture (Ports & Adapters)
    - Core independente das libs de infraestrutura. AMQP e Security ficam na borda, permitindo testes e evolução segura.
- Validação com Jakarta Bean Validation
    - Fail-fast para proteger as filas contra dados inválidos; melhora feedback para o integrador (400 detalhado).
- Segurança híbrida
    - JWT/Keycloak para integração corporativa (RBAC via `realm_access.roles` → `ROLE_*`), auditável e padrão de mercado.
    - Basic Auth para webhook de terceiros (Twilio), simples, compatível com Sandbox e isolado via filter chain dedicada.
- Java 21 + Virtual Threads
    - Carga de I/O (HTTP ↔ AMQP) se beneficia de milhares de threads leves, aumentando throughput sem custo de contexto de SO.
- Observabilidade e Operação
    - Actuator para healthcheck e integração com orquestradores (Compose/K8s). Health usado no `docker-compose.yml`.

---

### Troubleshooting
- 401/403 no `/integracao/agendamentos`
    - Verifique `issuer-uri` do Keycloak e se o token possui a role `integrador` no `realm_access.roles`.
- 401 no `/webhook/twilio`
    - Confirme usuário/senha via `WEBHOOK_BASIC_USER/WEBHOOK_BASIC_PASSWORD` e envio do header `Authorization: Basic` correto.
- Mensagens não aparecem nas filas
    - Cheque o management do RabbitMQ: http://localhost:15672 (user/pass conforme variáveis). Verifique exchange/queues.
- Healthcheck do container falhando
    - Veja logs do serviço (`docker logs ms-ingestao`) e conectividade com RabbitMQ/Keycloak definidos no Compose.