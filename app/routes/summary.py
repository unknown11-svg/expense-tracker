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

@router.get("/category", response_model=list[schemas.CategoryBreakdown])
def expense_category_breakdown(
    start_date: date | None = Query(None),
    end_date: date | None = Query(None),
    db: Session = Depends(get_db)
):
    if start_date and end_date and end_date < start_date:
        raise HTTPException(status_code=400, detail="End date must be later than start date")
    
    query = db.query(models.Transaction).filter(models.Transaction.type == models.TransactionType.expense)
    

    if start_date:
        query = query.filter(models.Transaction.date >= start_date)
    if end_date:
        query = query.filter(models.Transaction.date <= end_date)
        
    results = (
        db.query(
            models.Transaction.category.label("category"),
            func.coalesce(func.sum(models.Transaction.amount), 0.0).label("total")
        )
        .filter(models.Transaction.type == models.TransactionType.expense)
        .group_by(models.Transaction.category)
        .all()
    )
    # categories = db.query(models.Transaction.category).distinct().all()
    total_income = round((
        db.query(func.coalesce(func.sum(models.Transaction.amount), 0.0))
        .filter(models.Transaction.type == models.TransactionType.income)
        .scalar()
    ), 2)

    total_expenses = round((
        db.query(func.coalesce(func.sum(models.Transaction.amount), 0.0))
        .filter(models.Transaction.type == models.TransactionType.expense)
        .scalar()
    ), 2)

    # for (category,) in categories:
    #     category_total = round((
    #         db.query(func.coalesce(func.sum(models.Transaction.amount), 0.0))
    #         .filter(models.Transaction.type == models.TransactionType.expense)
    #         .filter(models.Transaction.category == category)
    #         .scalar()
    #     ), 2)

    #     percentage_of_income = round(((category_total / total_income) * 100), 2) if total_income > 0 else Decimal(0)
    #     percentage_of_expenditure = round(((category_total / total_expenses) * 100), 2) if total_expenses > 0 else Decimal(0)

    #     results.append(schemas.CategoryBreakdown(
    #         category= category,
    #         category_total=category_total,
    #         percentage_of_income=percentage_of_income,
    #         percentage_of_expenditure=percentage_of_expenditure
    #     ))
    return [
        schemas.CategoryBreakdown(
            category=category,
            category_total=total,
            percentage_of_income=round((total / total_income) * 100, 2) if total_income > 0 else Decimal(0),
            percentage_of_expenditure=round((total / total_expenses) * 100, 2) if total_expenses > 0 else Decimal(0)
        )
        for category, total in results
    ]

