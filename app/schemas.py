from datetime import date
from typing import Optional
from pydantic import BaseModel, Field
from decimal import Decimal

from .models import TransactionType

class TransactionCreate(BaseModel):
    title: str = Field(..., min_length=1, max_length=100)
    amount: Decimal = Field(..., gt=0)
    type: TransactionType
    category: Optional[str] = Field(None, max_length=50)
    date: date

class TransactionOut(BaseModel):
    id: int
    title: str
    amount: Decimal
    type: TransactionType
    category: Optional[str]
    date: date

    class Config:
        from_attributes = True

class SummaryOut(BaseModel):
    income: Decimal
    expenses: Decimal
    balance: Decimal