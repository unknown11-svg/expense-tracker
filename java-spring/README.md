# Expense Tracker Spring

A Spring Boot REST API for tracking personal finances. The application stores transactions in PostgreSQL and provides endpoints for creating, reading, updating, deleting, and filtering income and expense records, plus a summary endpoint for totals and balance.

## Features

- Create, view, update, and delete transactions
- Track both income and expenses
- Filter transactions by date range and category
- Get income, expense, and balance summaries
- Global handling for invalid date range requests
- Bean validation on request payloads

## Tech Stack

- Java 21
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Flyway dependency included, but disabled in the current configuration
- Lombok
- JUnit 5 / Spring Boot Test

## Project Structure

- `src/main/java/com/expensetracker/expensetracker/`
  - `controller/` - REST endpoints
  - `service/` - business logic
  - `repository/` - JPA queries
  - `entity/` - database entities and enums
  - `dto/` - request and response models
  - `exceptions/` - custom exception and global handler
- `src/main/resources/application.properties` - application configuration
- `src/test/java/.../ExpenseTrackerSpringApplicationTests.java` - Spring context test

## Data Model

### Transaction

A transaction contains:

- `id` - generated primary key
- `title` - transaction description
- `amount` - positive monetary value
- `type` - `income` or `expense`
- `category` - category label
- `date` - transaction date

### Transaction Types

The application uses the following enum values:

- `income`
- `expense`

## API Endpoints

Base path: `/transactions`

### Create a transaction

`POST /transactions`

Request body:
```json
{
  "title": "Salary",
  "amount": 5000.00,
  "type": "income",
  "category": "work",
  "date": "2026-05-07"
}
```

Validation rules:

- `title` is required
- `amount` must be present and positive
- `type` is required
- `date` is required and cannot be in the future
- `category` is optional

### Get all transactions

`GET /transactions`

Returns all transactions as a list.

### Get a transaction by id

`GET /transactions/{id}`

Returns a single transaction or an error if the id does not exist.

### Update a transaction

`PUT /transactions/{id}`

Request body:
```json
{
  "title": "Updated title",
  "amount": 250.00,
  "type": "expense",
  "category": "food",
  "date": "2026-05-07"
}
```

Update behavior:

- Fields are optional
- Only non-null fields are applied
- `category` is normalized before saving
- The transaction must already exist

### Delete a transaction

`DELETE /transactions/{id}`

Returns `204 No Content` on success.

### Get summary

`GET /transactions/summary`

Returns:

- total income
- total expenses
- balance

### Get expenses with filters

`GET /transactions/expenses`

Optional query parameters:

- `startDate` - ISO date, for example `2026-01-01`
- `endDate` - ISO date, for example `2026-12-31`
- `category` - category name

Example:

`/transactions/expenses?startDate=2026-01-01&endDate=2026-05-31&category=food`

### Get income with filters

`GET /transactions/income`

Optional query parameters:

- `startDate`
- `endDate`
- `category`

Example:

`/transactions/income?startDate=2026-01-01&endDate=2026-05-31`

## Filtering Rules

- If both `startDate` and `endDate` are provided and `endDate` is before `startDate`, the API throws `InvalidDateRangeException`
- The global exception handler maps this to `400 Bad Request`
- Filtering is case-insensitive only in the sense that categories are normalized before save; stored categories are lowercased and trimmed

## Configuration

The application reads database credentials from environment variables through `.env[.properties]` support.

### Required environment variables

```properties
DATASOURCE_URL=jdbc:postgresql://localhost:5432/expense_tracker
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=your_password
```

### Application properties

Important runtime settings:

- `server.port=8081`
- `spring.jpa.hibernate.ddl-auto=none`
- `spring.flyway.enabled=false`
- `spring.jpa.show-sql=true`

Notes:

- Flyway is on the classpath, but migrations are disabled in the current setup
- The project expects an existing PostgreSQL schema rather than generating tables automatically

## Request and Response DTOs

### `TransactionRequest`
Used for create requests.

### `TransactionUpdateRequest`
Used for partial update requests.

### `TransactionResponse`
Returned for single transaction and list endpoints.

### `Income_ExpensesResponse`
Returned by income and expense filter endpoints.

### `SummaryResponse`
Returned by the summary endpoint.

## Error Handling

The application includes a global exception handler for invalid date ranges.

Expected error status examples:

- `400 Bad Request` - invalid date range
- `404`-style runtime errors are currently thrown as generic `RuntimeException` messages for missing transactions

## Running the Application

### Prerequisites

- Java 21
- Maven
- PostgreSQL running and reachable from the app

### Start the app

```bash
mvn spring-boot:run
```

The API will start on port `8081`.

### Run tests

```bash
mvn test
```

## Example cURL Requests

### Create an income transaction

```bash
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Freelance Work",
    "amount": 1200.00,
    "type": "income",
    "category": "work",
    "date": "2026-05-07"
  }'
```

### Get all transactions

```bash
curl http://localhost:8081/transactions
```

### Get summary

```bash
curl http://localhost:8081/transactions/summary
```

## Testing

The project currently includes a Spring context smoke test in `ExpenseTrackerSpringApplicationTests`. Additional tests that would fit well here include:

- controller tests for CRUD endpoints
- service tests for summary and filtering logic
- repository tests for JPQL queries
- exception handler tests for invalid date range requests

## Notes

- The application uses Lombok for boilerplate reduction
- Category values are normalized during writes
- The repository uses custom JPQL queries for summary and filtered listing
- There is currently no migration script under `src/main/resources/db/migration`
