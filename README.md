# Barbearia API

## Tecnologias
- Java 21
- Spring Boot 3.4.0
- PostgreSQL
- Spring Security
- JWT
- JPA/Hibernate
- Swagger

## Executar
1. `docker compose up -d`
2. `mvn clean install`
3. `mvn spring-boot:run`

API: http://localhost:8080/api
Swagger: http://localhost:8080/api/swagger-ui/index.html

## Notificação de agendamento (WhatsApp ou SMS)
Ao criar um agendamento, o cliente recebe uma mensagem no telefone cadastrado (via Twilio).
Desabilitado por padrão (a mensagem aparece só no log). Para ativar, defina as variáveis de ambiente:

| Variável | Exemplo |
|---|---|
| `NOTIFICACAO_HABILITADO` | `true` |
| `NOTIFICACAO_CANAL` | `WHATSAPP` ou `SMS` |
| `TWILIO_ACCOUNT_SID` | `ACxxxxxxxx` |
| `TWILIO_AUTH_TOKEN` | `xxxxxxxx` |
| `TWILIO_FROM` | `+14155238886` (sandbox WhatsApp) ou seu número SMS |
| `TWILIO_TEMPLATE_SID` | opcional, `HXxxxxxxxx` (template aprovado do WhatsApp) |
