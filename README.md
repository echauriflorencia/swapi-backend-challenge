# SWAPI Backend Challenge

Java Spring Boot backend that integrates with the Star Wars API (SWAPI) to list and filter Star Wars resources with authentication and pagination

Exposed resources: 
- `people`
- `films`
- `starships`
- `vehicles`

## Endpoints

All these endpoints require auth.

### People

- `GET /people`
	- Query params:
		- `name` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /people/{id}`

Example:

```bash
curl -u swapi:swapi123 "http://localhost:8080/people?name=luke&page=1&size=5"
```

### Films

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
curl -u swapi:swapi123 "http://localhost:8080/films/1"
```

### Starships

- `GET /starships`
	- Query params:
		- `name` (optional)
		- `id` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /starships/{id}`

Example:

```bash
curl -u swapi:swapi123 "http://localhost:8080/starships?id=9&page=1&size=10"
```

### Vehicles

- `GET /vehicles`
	- Query params:
		- `name` (optional)
		- `id` (optional)
		- `page` (default `1`, minimum `1`)
		- `size` (default `10`, range `1..50`)
- `GET /vehicles/{id}`

Example:

```bash
curl -u swapi:swapi123 "http://localhost:8080/vehicles?name=sand&page=1&size=10"
```


## API Documentation (OpenAPI)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
