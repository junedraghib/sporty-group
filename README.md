# Sporty Group Assignment
This repository contains the implementation of the Sporty Group assignment, which involves building a messaging system to process sports betting messages from different providers and standardizing them for further processing. The system is designed to be extensible for future providers and market types.

different provider have different message types, the system is designed to process messages from different providers and different message types in a standardized way


### Alhpa Provider Message Format
#### ODDS_UPDATE message
```json
{
  "msg_type": "odds_update",
  "event_id": "12345",
  "values": {
    "1": 1.5,
    "X": 2.0,
    "2": 1.3
  }
}
```
#### SETTLEMENT message
```json
{
  "msg_type": "settlement",
  "event_id": "123454",
  "outcome": "1"
}
```


### Beta Provider Message Format
#### ODDS_UPDATE message
```json
{
  "type": "ODDS",
  "event_id": "67890",
  "odds": {
    "home": 1.8,
    "draw": 2.3,
    "away": 2.2
  }
}
```
#### SETTLEMENT message
```json
{
  "type": "SETTLEMENT",
  "event_id": "657890",
  "result": "away"
}
```

### Standard Message Format to support different market type and other future providers
#### ODDS_UPDATE message
```json
{
  "eventId" : "12345",
  "marketType" : "MATCH_RESULT",
  "timestamp" : "2025-06-27T18:11:35.607365",
  "provider" : "Alpha",
  "odds" : [ {
    "outcome" : "HOME_WIN",
    "value" : 1.5
  }, {
    "outcome" : "DRAW",
    "value" : 2.0
  }, {
    "outcome" : "AWAY_WIN",
    "value" : 1.3
  } ]
}
```
#### BET_SETTLEMENT message
```json
{
  "eventId" : "123454",
  "marketType" : "MATCH_RESULT",
  "timestamp" : "2025-06-27T18:12:08.874572",
  "provider" : "Alpha",
  "outcomes" : [ {
    "outcome" : "HOME_WIN",
    "result" : "WIN"
  } ]
}
```

## Low Level Implementation
Implemented a Spring Boot application that processes messages from different providers and different message types. The application uses a messaging queue to store the messages and process them asynchronously. The application is designed to be extensible for future providers and future market types.
### 1. Implemented Adaptor and Factory Design patterns 
Implemented Adaptor for processing message from different providers and different message types. Factory for selecting the relevant Adaptors is implemented
making sure the code is extensible for future provider and future market types
### 2. Implemented Singleton Design pattern
Implemented an inMemory messaging queue using Singleton Design pattern to ensure that there is only one instance of the queue in the application, ``ConcurrentLinkedQueue`` is used to make sure that the queue is thread-safe and can handle concurrent access from multiple threads
### 3. Implemented checksum validation for security and verification of provider
disabled by default for ease of testing, can be enabled by setting the environment variable `security.checksum.enabled` to `true`
when enabled, make sure to provide `X-Checksum` header in the request with the checksum value

an internal endpoint is exposed to calculate the checksum of the request body for ease
`/api/v1/checksum/calculate`

### 4. Implemented Rate Limiting for the providers endpoint to avoid abuse and ensure fair usage
disabled by default for ease of testing, can be enabled by setting the environment variable `security.ratelimit.enabled` to `true`
when enabled, on exceeding the limit, a `429 Too Many Requests` response will be returned

### 5. Implemented Idempotency 
for the providers endpoint to ensure that repeated requests with the same idempotency key will not result in duplicate processing
when publishing a message, the request must include `eventId` with a unique value otherwise message will not be processed

### 6. Implemented Swagger UI
swagger UI can be accessed at `http://localhost:8080/swagger-ui`

### 7. For Alpha and Beta providers, implemented a feed endpoint to receive messages

## Alpha Provider
```bash
curl --location 'http://localhost:8080/api/v1/provider-alpha/feed' \
--header 'Content-Type: application/json' \
--data '{
    "msg_type": "odds_update",
    "event_id": "12345",
    "values": {
        "1": 1.5,
        "X": 2.0,
        "2": 1.3
    }
}'
```

with checksum enabled, the request must include `X-Checksum` header with the checksum value
```bash
curl --location 'http://localhost:8080/api/v1/provider-alpha/feed' \
--header 'Content-Type: application/json' \
--header 'X-Checksum: 4FbI5AYeROHk5lS6AHDUAjQwjua4VcrBO2OYlC/1Oyw=' \
--data '{
    "msg_type": "odds_update",
    "event_id": "12345",
    "values": {
        "1": 1.5,
        "X": 2.0,
        "2": 1.3
    }
}'
```

```bash
curl --location 'http://localhost:8080/api/v1/provider-alpha/feed' \
--header 'Content-Type: application/json' \
--data '{
    "msg_type": "settlement",
    "event_id": "123454",
    "outcome": "1"
}'
```

success response will be
```json
{
  "status": "200",
  "message": "Message received and queued for processing",
  "error": null,
  "data": null
}
```

failure resposne
```json
{
  "status": 401,
  "error": "Invalid checksum",
  "message": "Unable to verify the provider, invalid checksum: alpha",
  "timestamp": "2025-06-27T12:08:08.487265Z"
}
```

```json
{
    "status": 429,
    "error": "Rate limit exceeded",
    "message": "Rate limit exceeded for provider: alpha please try after 30 seconds",
    "timestamp": "2025-06-27T12:08:45.710096Z"
}
```

checksum calculation endpoint ```/api/v1/internal/checksum/{provider}``` provider values : `alpha`, `beta`
```bash
curl --location 'http://localhost:8080/api/v1/internal/checksum/alpha' \
--header 'Content-Type: application/json' \
--data '{
    "msg_type": "odds_update",
    "event_id": "12345",
    "values": {
        "1": 1.5,
        "X": 2.0,
        "2": 1.3
    }
}'
```

## Beta Provider
```bash
curl --location 'http://localhost:8080/api/v1/provider-beta/feed' \
--header 'Content-Type: application/json' \
--data '{
    "type": "SETTLEMENT",
    "event_id": "657890",
    "result": "away"
}'
```

```bash
curl --location 'http://localhost:8080/api/v1/provider-beta/feed' \
--header 'Content-Type: application/json' \
--data '{
    "type": "ODDS",
    "event_id": "67890",
    "odds": {
        "home": 1.8,
        "draw": 2.3,
        "away": 2.2
    }
}'
```

