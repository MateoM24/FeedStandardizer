# Feed Standardizer

A Spring Boot microservice that acts as the **standardization layer** in a sports betting feed processing pipeline. It normalizes provider-specific feed messages (odds updates and bet settlements) into a unified internal format and forwards them to a message queue.

---

## Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters):

```
adapter/in/web/          ← Inbound adapters (REST controllers + mappers)
  alpha/                 ← ProviderAlpha-specific DTOs and mapper
  beta/                  ← ProviderBeta-specific DTOs and mapper
  exception/             ← Global error handler

domain/
  model/                 ← StandardFeedMessage, StandardOddsChange, StandardBetSettlement, Outcome
  port/in/               ← FeedProcessingUseCase (inbound port)
  port/out/              ← MessagePublisherPort (outbound port)

application/service/     ← FeedProcessingService (use case implementation)

adapter/out/messaging/   ← MockMessagePublisher (outbound adapter — logs to simulate queue)
```

The **domain has zero dependencies** on Spring or any infrastructure library. The queue adapter can be swapped from the mock to a real Kafka or RabbitMQ implementation without touching the domain or application layers.

---

## Prerequisites

| Tool   | Version |
|--------|---------|
| Java   | 26+     |
| Gradle | 9.4+    |

---

## Running the application

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The service starts on **port 8080**.

---

## Running the tests

```bash
./gradlew test        # Linux / macOS
gradlew.bat test      # Windows
```

---

## Endpoints

Both endpoints accept `Content-Type: application/json` and return `202 Accepted` on success.

### POST `/provider-alpha/feed`

Handles both message types from ProviderAlpha. The `msg_type` field is the discriminator.

**ODDS_CHANGE**
```json
{
  "msg_type": "odds_update",
  "event_id": "ev123",
  "values": {
    "1": 2.0,
    "X": 3.1,
    "2": 3.8
  }
}
```

**BET_SETTLEMENT**
```json
{
  "msg_type": "settlement",
  "event_id": "ev123",
  "outcome": "1"
}
```
> `outcome`: `"1"` = home win, `"X"` = draw, `"2"` = away win

---

### POST `/provider-beta/feed`

Handles both message types from ProviderBeta. The `type` field is the discriminator.

**ODDS_CHANGE**
```json
{
  "type": "ODDS",
  "event_id": "ev456",
  "odds": {
    "home": 1.95,
    "draw": 3.2,
    "away": 4.0
  }
}
```

**BET_SETTLEMENT**
```json
{
  "type": "SETTLEMENT",
  "event_id": "ev456",
  "result": "away"
}
```
> `result`: `"home"`, `"draw"`, or `"away"`

---

## Standardized internal format

Both providers' messages are normalized into one of two internal types before being published.

### ODDS_CHANGE
```json
{
  "messageType": "ODDS_CHANGE",
  "eventId": "ev123",
  "timestamp": "2024-01-15T10:30:00Z",
  "homeOdds": 2.0,
  "drawOdds": 3.1,
  "awayOdds": 3.8
}
```

### BET_SETTLEMENT
```json
{
  "messageType": "BET_SETTLEMENT",
  "eventId": "ev123",
  "timestamp": "2024-01-15T10:30:00Z",
  "outcome": "HOME"
}
```
> `outcome`: `HOME`, `DRAW`, or `AWAY`

---

## Mock message queue

The queue is simulated via structured logging. When a message is published, you will see a line like:

```
[MOCK QUEUE] Published message → {"messageType":"ODDS_CHANGE","eventId":"ev123","payload":{...}}
```

Replace `MockMessagePublisher` with a real adapter (Kafka, RabbitMQ, etc.) to connect to an actual queue — no other code needs to change.

---

## Error responses

All errors follow [RFC 7807 Problem Details](https://www.rfc-editor.org/rfc/rfc7807):

| Scenario | HTTP Status                 |
|---|-----------------------------|
| Unknown `msg_type` / `type` value | `422 Unprocessable Content` |
| Missing required fields or invalid values | `400 Bad Request`           |
| Bean validation failure | `400 Bad Request`           |

---

## cURL examples

```bash
# ProviderAlpha — ODDS_CHANGE
curl -X POST http://localhost:8080/provider-alpha/feed \
  -H "Content-Type: application/json" \
  -d '{"msg_type":"odds_update","event_id":"ev123","values":{"1":2.0,"X":3.1,"2":3.8}}'

# ProviderAlpha — BET_SETTLEMENT
curl -X POST http://localhost:8080/provider-alpha/feed \
  -H "Content-Type: application/json" \
  -d '{"msg_type":"settlement","event_id":"ev123","outcome":"1"}'

# ProviderBeta — ODDS_CHANGE
curl -X POST http://localhost:8080/provider-beta/feed \
  -H "Content-Type: application/json" \
  -d '{"type":"ODDS","event_id":"ev456","odds":{"home":1.95,"draw":3.2,"away":4.0}}'

# ProviderBeta — BET_SETTLEMENT
curl -X POST http://localhost:8080/provider-beta/feed \
  -H "Content-Type: application/json" \
  -d '{"type":"SETTLEMENT","event_id":"ev456","result":"away"}'
```

## PowerShell examples (Windows)

```powershell
# ProviderAlpha — ODDS_CHANGE
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/provider-alpha/feed" `
  -ContentType "application/json" `
  -Body '{"msg_type":"odds_update","event_id":"ev123","values":{"1":2.0,"X":3.1,"2":3.8}}'

# ProviderAlpha — BET_SETTLEMENT
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/provider-alpha/feed" `
  -ContentType "application/json" `
  -Body '{"msg_type":"settlement","event_id":"ev123","outcome":"1"}'

# ProviderBeta — ODDS_CHANGE
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/provider-beta/feed" `
  -ContentType "application/json" `
  -Body '{"type":"ODDS","event_id":"ev456","odds":{"home":1.95,"draw":3.2,"away":4.0}}'

# ProviderBeta — BET_SETTLEMENT
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/provider-beta/feed" `
  -ContentType "application/json" `
  -Body '{"type":"SETTLEMENT","event_id":"ev456","result":"away"}'
```

## Available endpoints

| URL | Description |
|-----|-------------|
| `POST /provider-alpha/feed` | ProviderAlpha feed ingestion |
| `POST /provider-beta/feed` | ProviderBeta feed ingestion |
| `GET /swagger-ui.html` | Interactive API documentation (Swagger UI) |
| `GET /api-docs` | Raw OpenAPI JSON spec |
| `GET /actuator/health` | Application health check |
| `GET /actuator/info` | Application info |

---

## Additional ideas

- **Kafka topic per event type** — rather than a single shared topic, publish `ODDS_CHANGE` and `BET_SETTLEMENT` to separate Kafka topics. Consumers can subscribe to only what they need.
- **Containerization** — Dockerize the service and deploy to Kubernetes for horizontal scaling and resilience.
- **Message ordering guarantee** — In live sports betting, odds update rapidly. If message #5 arrives after #6 due to network reordering, processing it would roll back to stale odds. Production solution: include a sequence number per event and discard late arrivals.
- **Idempotency / deduplication** — Providers retry on timeout. Processing the same `BET_SETTLEMENT` twice could cause downstream issues. Solution: track processed `eventId + messageType` pairs with a short TTL in Redis.
- **Dead Letter Queue (DLQ)** — Currently a failed message is logged and silently dropped. In production, failed messages should land in a DLQ for inspection and reprocessing without data loss.
- **Provider authentication** — The endpoints are currently open. Each provider should authenticate via an API key header or mTLS certificate to prevent unauthorized message injection.
- **Multi-market support** — The service currently handles only the 1X2 market. The hexagonal architecture makes it straightforward to extend the domain model to support Asian Handicap, Over/Under, and other market types without touching the provider adapters.