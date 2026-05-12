from fastapi import FastAPI
import time
from sqlalchemy import text

from .database import Base, init_engine
from .routes.transactions import router as transaction_router
from .routes.summary import router as summary_router
from .settings import settings

app = FastAPI(title="Expense Tracker API")


@app.on_event("startup")
async def startup():
    # Ensure DATABASE_URL is present and initialize engine with retries.
    if not settings.DATABASE_URL:
        raise RuntimeError("DATABASE_URL not set. Create .env or set the env var before starting the app.")

    engine = init_engine(settings.DATABASE_URL)

    # Simple retry/backoff for initial DB connection
    for attempt in range(5):
        try:
            with engine.connect() as conn:
                conn.execute(text("SELECT 1"))
            break
        except Exception:
            if attempt == 4:
                raise
            time.sleep(2 ** attempt)

    Base.metadata.create_all(bind=engine)


@app.get("/")
def root():
    return {"message": "Expense Tracker API is running"}


app.include_router(transaction_router)
app.include_router(summary_router)