# Expense Tracker API

A RESTful Expense Tracker API built with Python, FastAPI, SQLAlchemy, and PostgreSQL. It allows for creating, updating, listing, and summary analysis of income and expense transactions. 

This is an expandable learning project designed with clean modularity using modern Python web development patterns.

## Features

- **Transaction Management**: Full CRUD (Create, Read, Update, Delete) for income and expenses.
- **Categorization**: Transactions require categories, which are normalized internally.
- **Filtering**: Query capabilities for filtering records by date ranges (`start_date`, `end_date`) and categories.
- **Financial Summaries**: Built-in endpoints providing calculated balances and totals.
- **Category Breakdowns**: Analyzes your expenses and returns detailed breakdowns, aggregating totals per category and representing them as percentages of your total income and total expenditures.
- **Robust Validation**: Uses Pydantic to ensure reliable schema checks and data integrity.

## Technology Stack

- **Framework**: [FastAPI](https://fastapi.tiangolo.com/)
- **Database**: PostgreSQL
- **ORM / Query Builder**: [SQLAlchemy](https://www.sqlalchemy.org/)
- **Data Validation & Serialization**: [Pydantic](https://docs.pydantic.dev/)
- **Server**: [Uvicorn](https://www.uvicorn.org/)

## Project Structure

```text
├── app/
│   ├── database.py       # Engine initialization and DB session management
│   ├── main.py           # FastAPI application initialization & routers
│   ├── models.py         # SQLAlchemy database models
│   ├── schemas.py        # Pydantic schemas for request/response serialization
│   ├── settings.py       # Pydantic-based configuration and environments
│   └── routes/
│       ├── summary.py       # Endpoints for financial aggregate computations
│       └── transactions.py  # Endpoints for CRUD operations on transactions
├── requirements.txt      # Project dependencies
├── Project.md            # Extended goals and context notes
└── .env                  # Environment variables (not tracked in source control)
```

## Getting Started

### Prerequisites
- Python 3.12+
- PostgreSQL database

### Installation

1. **Clone the repository.**
2. **Create and activate a virtual environment:**
   ```bash
   python -m venv .venv
   # On Windows:
   .venv\Scripts\activate
   # On macOS/Linux:
   source .venv/bin/activate
   ```
3. **Install the dependencies:**
   ```bash
   pip install -r requirements.txt
   ```
4. **Environment Setup:** Make sure you have a `.env` file in the root level of your project with your database URL configuration:
   ```env
   DATABASE_URL="postgresql+psycopg://username:password@localhost:5432/expense_tracker"
   ```

### Running the App

Start the application with Uvicorn:

```bash
uvicorn app.main:app --reload
```

Then, visit [`http://127.0.0.1:8000/docs`](http://127.0.0.1:8000/docs) to visually test the API endpoints using the interactive Swagger UI.

## Core API Endpoints

### Transactions
- `POST /transactions/` - Create a new transaction.
- `GET /transactions/` - List all transactions in descending order of dates.
- `PUT /transactions/{transaction_id}` - Update a specific transaction.
- `DELETE /transactions/{transaction_id}` - Delete a specific transaction.

### Summary & Analytics
- `GET /summary/` - Provides total income, total expenses, and the remaining balance.
- `GET /summary/expenses` - Provides filtered listings of your expenses directly.
- `GET /summary/income` - Provides filtered listings of your incoming transactions.
- `GET /summary/category` - Provides insights into expenditure broken down by grouped categories, including the respective aggregated amount and percentages.

## Configuration & Error Handling
- Invalid payloads or missing attributes are handled safely by FastAPI dependencies.
- SQLAlchemy session state wraps around endpoints safely utilizing the FastAPI `Depends` dependency injection.
- Date ranges requested by external clients are validated (e.g., `end_date` must not be before `start_date`).