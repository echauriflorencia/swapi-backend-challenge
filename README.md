# SWAPI Backend Challenge

Java Spring Boot backend that integrates with the Star Wars API (SWAPI) to list and filter Star Wars resources with authentication and pagination

Exposed resources: 
- `people`
- `films`
- `starships`
- `vehicles`

## 1. Requirements

- Java 21
- Internet access (the app queries SWAPI in real time)
- `curl` for manual testing

## 2. Quick Setup

### 2.1 Access credentials

The application uses in-memory credentials and JWT.

Set environment variables before starting:

```bash
export APP_SECURITY_USER_NAME=swapi
export APP_SECURITY_USER_PASSWORD=swapi123
export APP_SECURITY_JWT_SECRET=secret-jwt-key
```

If they are not set, default values from `application.properties` are used.

### 2.2 Run the app

```bash
./mvnw spring-boot:run
```

By default, it runs at `http://localhost:8080`.

## 3. Authentication Flow

Security is intentionally simple for this technical challenge:
- JWT Bearer authentication
- In-memory user
- Stateless sessions
- Spring Security default `401/403` handling (no custom security error handlers)

Get a token with `POST /auth/login`:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
	-H "Content-Type: application/json" \
	-d '{"username":"swapi","password":"swapi123"}' | jq -r '.token')
```

Use that token in protected endpoints:

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/people
```

### 3.1 Example request without credentials

```bash
curl -i http://localhost:8080/people
```

Expected response: `403 Forbidden` when no valid JWT is provided.

## 4. Endpoints

All these endpoints require a valid JWT token.

### 4.1 People

- `GET /people`
	- Query params:
		- `name` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /people/{id}`

Example:

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/people?name=luke&page=1&size=5"
```

### 4.2 Films

- `GET /films`
	- Query params:
		- `title` (optional)
		- `name` (optional, alias for `title`)
		- `id` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /films/{id}`

Detail example (stable):

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/films/1"
```

### 4.3 Starships

- `GET /starships`
	- Query params:
		- `name` (optional)
		- `id` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /starships/{id}`

Example:

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/starships?id=9&page=1&size=10"
```

### 4.4 Vehicles

- `GET /vehicles`
	- Query params:
		- `name` (optional)
		- `id` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /vehicles/{id}`

Example:

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/vehicles?name=sand&page=1&size=10"
```

## 5. Automated Tests

The project includes automated unit and integration tests to prevent regressions.

### 5.1 Test Scope

- Unit tests for services:
	- `PeopleService`
	- `FilmsService`
	- `StarshipsService`
	- `VehiclesService`
- Integration tests with `MockMvc` for:
	- Authentication required on protected endpoints
	- JWT login and authenticated calls
	- Pagination and filter handling
	- Error mapping: `400`, `404`, `503`

### 5.2 Test Files

- `src/test/java/com/challenge/swapi/service/PeopleServiceTest.java`
- `src/test/java/com/challenge/swapi/service/FilmsServiceTest.java`
- `src/test/java/com/challenge/swapi/service/StarshipsServiceTest.java`
- `src/test/java/com/challenge/swapi/service/VehiclesServiceTest.java`
- `src/test/java/com/challenge/swapi/integration/ApiIntegrationTest.java`
- `src/test/java/com/challenge/swapi/fixtures/TestFixtures.java`

### 5.3 Run Tests

Run full test suite:

```bash
./mvnw test
```

Run a single test class:

```bash
./mvnw -Dtest=ApiIntegrationTest test
```

### 5.4 Determinism

- Unit tests mock `SwapiClient`.
- Integration tests use mocked services (`@MockBean`) for controller/security behavior.
- No real SWAPI calls are required during test execution.

## 6. Setup From Scratch (Smoke Test)

1. Clone the repo.
2. Export credentials:

```bash
export APP_SECURITY_USER_NAME=swapi
export APP_SECURITY_USER_PASSWORD=swapi123
export APP_SECURITY_JWT_SECRET=changeit-changeit-changeit-changeit
```

3. Run the app:

```bash
./mvnw spring-boot:run
```

4. Get token:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
	-H "Content-Type: application/json" \
	-d '{"username":"swapi","password":"swapi123"}' | jq -r '.token')
```

5. Verify authenticated access:

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/people?page=1&size=2"
```

6. Verify access is blocked without auth:

```bash
curl -i http://localhost:8080/people
```

## 7. API Documentation (OpenAPI)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 8. Deployment

The application is deployed and available at:

https://swapi-backend-challenge.onrender.com/

Note: The service runs on Render's free tier and may take up to 30–50 seconds to respond on the first request after inactivity.