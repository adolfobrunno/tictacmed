# Tá na hora!

Tá na hora! é um serviço de lembretes de medicamentos via WhatsApp. Ele permite:

- Cadastrar lembretes com RRULE
- Enviar lembretes e cobrar confirmação (Tomei/Esqueci)
- Consultar o próximo horário agendado
- Integrar com o WhatsApp Cloud API

## Tech stack

- Java 24
- Spring Boot 3 (Web, Data MongoDB, AMQP)
- MongoDB
- OpenAI (classificação de mensagens)
- lib-recur (RRULE)

## Começando

### Pré-requisitos

- JDK 24
- Docker (para MongoDB via Docker Compose)
- Gradle (Wrapper incluído)

### Subir MongoDB com Docker Compose

```bash
docker compose up -d
```

### Configuração

As variáveis principais estão em `src/main/resources/application.yml`. Alguns exemplos:

- `SPRING_DATA_MONGODB_URI` e `SPRING_DATA_MONGODB_DATABASE`
- `OPENAI_API_KEY` e `OPENAI_MODEL`
- `TANAHORA_WHATSAPP_ENABLED`, `TANAHORA_WHATSAPP_ACCESS_TOKEN`, `TANAHORA_WHATSAPP_VERIFY_TOKEN`
- `TANAHORA_SCHEDULER_ENABLED`

### Executar a aplicação

```bash
# Windows PowerShell
./gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```

O servidor sobe em http://localhost:8080.

### Webhook do WhatsApp

O endpoint de webhook está em `POST /webhooks/whatsapp`.

## Estrutura do projeto

- `domain`: modelos e regras de negócio
- `application`: casos de uso e serviços
- `infrastructure`: integração (webhook, scheduler, configs)

## Licença

Defina a licença conforme a necessidade do projeto.
