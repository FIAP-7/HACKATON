# ms-ingestao — Microsserviço de Ingestão (Gatekeeper)

Este é o serviço de borda (*Edge Service*) do ecossistema de Otimização de Agendamentos do SUS. Ele atua como um **Gatekeeper Stateless**, responsável por receber cargas de dados do sistema legado e capturar interações dos usuários via E-mail, garantindo validação, segurança e desacoplamento através de mensageria.

---

## 📋 Sumário
- [Visão Geral](#-visão-geral)
- [Arquitetura e Stack Tecnológica](#-arquitetura-e-stack-tecnológica)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Segurança](#-segurança)
- [Endpoints (API Reference)](#-endpoints-api-reference)
- [Mensageria (Contrato de Eventos)](#-mensageria-contrato-de-eventos)
- [Como Executar](#-como-executar)
- [Observabilidade](#-observabilidade)

---

## 🔭 Visão Geral

O `ms-ingestao` tem como objetivos principais:
1.  **Recebimento de Cargas:** Endpoint seguro para ingestão de agendamentos vindos do sistema legado.
2.  **Interação do Paciente:** Endpoint público para processar cliques de confirmação/cancelamento em e-mails (Magic Links).
3.  **Validação Fail-Fast:** Garante que apenas dados íntegros entrem no fluxo de processamento.
4.  **Assincronismo:** Transforma requisições HTTP em eventos persistidos no RabbitMQ (Fire-and-forget).

---

## 🏗 Arquitetura e Stack Tecnológica

O projeto segue os princípios da **Clean Architecture** (Hexagonal Simplificada) e **Event-Driven Architecture**.

*   **Linguagem:** Java 21
*   **Framework:** Spring Boot 3.2+ (Web, AMQP, Security, Actuator)
*   **Performance:** Virtual Threads (Project Loom) habilitadas para alto throughput de I/O.
*   **Mensageria:** RabbitMQ (Serialização JSON).
*   **Build & Deploy:** Maven, Docker (Multi-stage build).

---

## 📂 Estrutura do Projeto

A organização de pacotes reflete a separação de responsabilidades da Clean Architecture:

```text
br.com.sus.ingestao
├── application             # Implementação dos casos de uso
├── core
│   ├── event               # Modelos de Evento (Domain Objects)
│   ├── port                # Interfaces de Saída (Output Ports)
│   └── usecase             # Interfaces de Entrada (Input Ports)
├── entrypoint              # Adaptadores de Entrada (Primary Adapters)
│   ├── controller          # API REST
│   ├── dto                 # Records de Transferência e Validação
│   └── handler             # Tratamento Global de Erros
└── infra                   # Adaptadores de Saída (Secondary Adapters)
    ├── config              # Configurações (RabbitMQ, Beans)
    ├── messaging           # Implementação dos Publishers RabbitMQ
    ├── openapi             # Configuração Swagger/OpenAPI
    └── security            # Filtros de API Key e SecurityConfig
```

---

## 🛡 Segurança

O serviço implementa uma estratégia de segurança híbrida baseada na natureza do consumidor:

1.  **Machine-to-Machine (Integração):**
    *   Protegido via **API Key**.
    *   O cliente deve enviar o header `X-API-KEY` validado contra a configuração da aplicação.
    *   Implementado via `OncePerRequestFilter`.

2.  **User-to-Machine (Magic Link):**
    *   Acesso público (`permitAll`).
    *   A segurança baseia-se na posse do **Token** único contido na URL (validado posteriormente pelo microsserviço de processamento).

---

## 🔌 Endpoints (API Reference)

A documentação interativa (Swagger UI) está disponível em:
`http://localhost:8080/swagger-ui/index.html`

### 1. Ingestão de Agendamentos
Recebe dados brutos do sistema legado.

*   **URL:** `POST /api/v1/integracao/agendamentos`
*   **Auth:** Header `X-API-KEY: <seu-segredo>`
*   **Body:** JSON

**Exemplo cURL:**
```bash
curl -X POST "http://localhost:8080/api/v1/integracao/agendamentos" \
  -H "X-API-KEY: hackathon-secret-key-123" \
  -H "Content-Type: application/json" \
  -d '{
    "idExterno": "REQ-001",
    "paciente": {
        "nome": "Maria Silva",
        "telefone": "5511999998888",
        "email": "maria@email.com"
    },
    "consulta": {
        "dataHora": "2026-12-25T14:00:00",
        "medico": "Dr. House",
        "especialidade": "CARDIOLOGIA",
        "unidadeId": "UBS-VILA-MARIANA"
    }
  }'
```
*   **Respostas:**
    *   `202 Accepted`: Recebido e enfileirado.
    *   `400 Bad Request`: Erro de validação nos campos.
    *   `403 Forbidden`: API Key inválida ou ausente.

### 2. Ação do Usuário (Magic Link)
Endpoint acessado pelo navegador quando o paciente clica no e-mail.

*   **URL:** `GET /api/v1/acao/responder`
*   **Auth:** Pública.
*   **Query Params:**
    *   `token`: Identificador único da transação.
    *   `acao`: `CONFIRMAR` ou `CANCELAR`.

**Exemplo (Navegador):**
`http://localhost:8080/api/v1/acao/responder?token=uuid-1234-5678&acao=CONFIRMAR`

*   **Resposta:**
    *   `200 OK` (Content-Type: `text/html`): Retorna uma página HTML renderizada informando o sucesso da operação.

---

## 📨 Mensageria (Contrato de Eventos)

O serviço publica mensagens em JSON na Exchange `sus.direct.exchange`.

| Fila | Routing Key | Descrição |
| :--- | :--- | :--- |
| `sus.input.carga-agendamento` | `rota.carga.agendamento` | Evento contendo os dados completos do agendamento validado. |
| `sus.input.resposta-usuario` | `rota.resposta.usuario` | Evento contendo a decisão do usuário (`identificador`, `resposta`, `canal=EMAIL`). |

---

## 🚀 Como Executar

### Pré-requisitos
*   Docker & Docker Compose
*   Java 21 (Apenas para execução local sem Docker)

### Via Docker Compose (Recomendado)
Sobe a aplicação juntamente com o RabbitMQ.

```bash
docker-compose up -d --build
```

### Execução Local (Maven)
Caso queira rodar a aplicação na IDE e a infraestrutura no Docker.

1.  Suba a infraestrutura:
    ```bash
    docker-compose up -d rabbitmq
    ```
2.  Execute a aplicação:
    ```bash
    ./mvnw spring-boot:run
    ```

---

## ⚙️ Variáveis de Ambiente

Configurações disponíveis no `application.yml`:

| Variável | Descrição | Default (Dev) |
| :--- | :--- | :--- |
| `SUS_SECURITY_API_TOKEN` | Chave secreta para autenticação M2M | `hackathon-secret` |
| `SPRING_RABBITMQ_HOST` | Host do RabbitMQ | `localhost` |
| `SPRING_RABBITMQ_PORT` | Porta do RabbitMQ | `5672` |

---

## 📊 Observabilidade

O serviço expõe endpoints do **Spring Actuator** para monitoramento de saúde e métricas.

*   **Health Check:** `GET /actuator/health`
    *   Retorna `{"status": "UP"}` se a aplicação e a conexão com o RabbitMQ estiverem saudáveis.

---

## 🧪 Postman

Arquivos disponibilizados em `postman/` na raiz do repositório:

- `postman/ms-ingestao.postman_collection.json`
- `postman/ms-ingestao.postman_environment.json`

A coleção cobre os cenários:
- POST /api/v1/integracao/agendamentos — Sucesso (202)
- POST /api/v1/integracao/agendamentos — Sem API Key (401)
- POST /api/v1/integracao/agendamentos — API Key Errada (401)
- POST /api/v1/integracao/agendamentos — Payload Inválido (400)
- GET /api/v1/acao/responder — Público (200, Content-Type text/html)
- GET /actuator/health — Healthcheck (200)

Variáveis do ambiente:
- `baseUrl` (ex.: http://localhost:8080)
- `apiKey` (default: hackathon-secret)
- `wrongApiKey` (default: wrong-secret)
- `contentType` (application/json)
- `token` (ex.: uuid-1234-5678)
- `acao` (CONFIRMAR)

Como usar:
1. Abra o Postman, clique em Import e selecione os dois arquivos acima.
2. Selecione o ambiente "MS Ingestao - Local" no canto superior direito.
3. Ajuste a variável `apiKey` se tiver alterado `SUS_SECURITY_API_TOKEN` no ambiente de execução.
4. Execute as requisições individualmente ou como uma Collection Run para validar todos os cenários.
