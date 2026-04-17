from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import func
from sqlalchemy.orm import Session
from decimal import Decimal

from .. import models, schemas
from ..database import get_db

router = APIRouter(prefix="/transactions", tags=["Transactions"])

@router.post("/", response_model=schemas.TransactionOut, status_code=status.HTTP_201_CREATED)
def create_transaction(payload: schemas.TransactionCreate, db: Session = Depends(get_db)):
    transaction = models.Transaction(**payload.model_dump())
    db.add(transaction)
    db.commit()
    db.refresh(transaction)
    return transaction

@router.get("/", response_model=list[schemas.TransactionOut])
def list_transactions(db: Session = Depends(get_db)):
    transactions = db.query(models.Transaction).order_by(models.Transaction.date.desc()).all()
    return transactions

@router.delete("/{transaction_id}", status_code = status.HTTP_204_NO_CONTENT)
def delete_transaction( transaction_id : int, db: Session = Depends(get_db)):
    transaction = db.query(models.Transaction).filter(models.Transaction.id == transaction_id).first()

    if not transaction:
        raise HTTPException(status_code=404, detail="Transaction not found")

    db.delete(transaction)
    db.commit()
    return None

@router.get("/summary", response_model=schemas.SummaryOut)
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
