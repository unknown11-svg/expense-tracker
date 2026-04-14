from fastapi import FastAPI

from .database import Base, engine
from .routes.transactions import router as transaction_router

Base.metadata.create_all(bind=engine)

app = FastAPI(title="Expense Tracker API")

@app.get("/")
def root():
    return {"message": "Expense Tracker API is runnning"}

app.include_router(transaction_router)