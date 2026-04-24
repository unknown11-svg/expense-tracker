from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy import func
from sqlalchemy.orm import Session
from decimal import Decimal
from datetime import date

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/summary", tags=["Summary"])

@router.get("/", response_model=schemas.SummaryOut)
def get_summary(db: Session = Depends(get_db)):
    income = round((
        db.query(func.coalesce(func.sum(models.Transaction.amount), 0.0))
        .filter(models.Transaction.type == models.TransactionType.income)
        .scalar()
    ), 2)

    expenses = round((
        db.query(func.coalesce(func.sum(models.Transaction.amount), 0.0))
        .filter(models.Transaction.type == models.TransactionType.expense)
        .scalar()
    ), 2)

    balance = round(income - expenses, 2)
    return schemas.SummaryOut(income=income, expenses=expenses, balance=balance)

@router.get("/expenses", response_model=list[schemas.Income_ExpenseOut])
def list_expenses(
    start_date: date | None = Query(None),
    end_date: date | None = Query(None),
    category: str | None = Query(None, max_length=50),
    db: Session = Depends(get_db)):

    if start_date and end_date and end_date < start_date:
        raise HTTPException(status_code=400, detail="End date must be later than start date")
    
    query = (db.query(models.Transaction)
             .filter(models.Transaction.type == models.TransactionType.expense)
             .order_by(models.Transaction.date.desc()))
    if start_date:
        query = query.filter(models.Transaction.date >= start_date)
    
    if end_date:
        query = query.filter(models.Transaction.date <= end_date)
    
    if category:
        query = (query.filter(models.Transaction.category == category))

    return query

@router.get("/income", response_model=list[schemas.Income_ExpenseOut])
def list_income(
    start_date: date | None = Query(None), 
    end_date: date | None = Query(None),
    db: Session = Depends(get_db)):

    if start_date and end_date and end_date < start_date:
        raise HTTPException(status_code=400, detail="End date must be later than start date")

    query = (db.query(models.Transaction)
    .filter(models.Transaction.type == models.TransactionType.income)
    .order_by(models.Transaction.date.desc())
    )

    if start_date:
        query = query.filter(models.Transaction.date >= start_date)
    if end_date:
        query = query.filter(models.Transaction.date <= end_date)
    
    return query