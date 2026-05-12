# 💰 Expense Tracker API: FastAPI vs Spring Boot

A comparative implementation of a RESTful transaction management API to evaluate performance, developer experience, and maintainability across two popular backend stacks.

> Built as a portfolio project demonstrating cross-stack proficiency and empirical technology evaluation.

## 🏗️ Architecture

Both implementations expose identical functionality using the same PostgreSQL database schema.

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /transactions | Create a transaction |
| GET | /transactions | List all transactions |
| GET | /transactions/{id} | Get transaction by ID |
| PUT | /transactions/{id} | Update transaction |
| DELETE | /transactions/{id} | Delete transaction |
| GET | /transactions/summary | Get income/expenses/balance summary |
| GET | /transactions/expenses | List expenses with optional date/category filters |
| GET | /transactions/income | List income with optional date/category filters |

## 🔬 Tech Stack Comparison

| Aspect | FastAPI | Spring Boot |
|--------|---------|-------------|
| Language | Python 3.12 | Java 21 |
| Framework | FastAPI 0.135 | Spring Boot 3.5 |
| ORM | SQLAlchemy 2.0 | Spring Data JPA (Hibernate) |
| Validation | Pydantic 2.13 | Jakarta Validation |
| Server | Uvicorn 0.44 | Embedded Tomcat |
| Database | PostgreSQL | PostgreSQL |
| Migrations | Manual | Flyway (configured, disabled) |
| Testing | None yet | JUnit 5 + Spring Boot Test |
| Code Files | 7 (.py) | 15 (.java) |
| Lines of Code | 255 | 391 |

## 📊 Quick LOC Comparison

FastAPI (Python): 255 lines across 7 files
Spring Boot (Java): 391 lines across 15 files (382 main + 9 test)

Python requires ~35% less code for identical functionality.

## 🚀 Running the Projects

### Prerequisites
- Docker & Docker Compose
- Python 3.12+ (for FastAPI)
- Java 21 + Maven (for Spring Boot)
- PostgreSQL 15

### Start Database
```bash
docker-compose up -d postgres
```

### FastAPI
```
cd python-fastapi
python -m venv venv && source venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8080
```
### Springboot
```
cd java-spring
./mvnw spring-boot:run
```


## Performance Benchmarks
Load testing performed with k6. Each stack tested with identical workloads.

| Metric | FastAPI | Spring Boot |
|--------|---------|-------------|
| Requests/sec( GET summary) | TBD | TBD |
| Requests/sec (POST create) | TBD | TBD |
| P95 Response Time (GET) | TBD | TBD |
| P95 Response Time (POST) | TBD | TBD |
| Memory Usage (idle) | TBD | TBD |
| Memory Usage (load) | TBD | TBD |
| Startup Time | TBD | TBD |

---
| Metric | FastAPI(Python) | Spring Boot(Java) |
|--------|---------|-------------|
| Success Rate | 100% | 100% |
| Avg Response Time | 1.09s | 55ms |
| P95 Response Time (GET) | 2.25s | 193ms |
| P95 Response Time (POST) | TBD | TBD |
| Requests/sec | 18.6 | 44.2 |
| Summary Avg | 1125ms | 48ms |
| Expenses Avg | 1098ms | 71ms |
| Create Avg | 1062ms | 47ms |

## Key Takeaways

When to Use Which
### Choose FastAPI when:
- Rapid prototyping and iteration speed matter
- Team is Python-heavy
- Project scope is small to medium
- Data science/ML integration is needed

### Choose Spring Boot when:
- Enterprise-grade reliability is required
- Large team, long-lived codebase
- Compile-time type safety is non-negotiable
- Extensive Spring ecosystem needed (Security, Cloud, Batch)

## 📂 Project Structure

Each implementation lives in its own directory with its own README containing detailed structure and setup instructions.

- [`python-fastapi/`](python-fastapi/) — FastAPI + SQLAlchemy implementation
- [`java-spring/`](java-spring/) — Spring Boot + JPA implementation
- [`benchmarks/`](benchmarks/) — Load test scripts and results

---

## 📝 License
MIT — feel free to use this as a reference or template.
