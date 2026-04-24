from __future__ import annotations
from datetime import date
from typing import Optional
from pydantic import BaseModel, Field, field_validator
from decimal import Decimal

from .models import TransactionType

class TransactionCreate(BaseModel):
    title: str = Field(..., min_length=1, max_length=100)
    amount: Decimal = Field(..., gt=0)
    type: TransactionType
    category: str = Field("other", max_length=50)
    date: date

    @field_validator("category")
    @classmethod
    def normalize_category(cls, v: str) -> str:
        return v.strip().lower() or "other"

class TransactionUpdate(BaseModel):
    title: Optional[str] = Field(None, min_length=1, max_length=100)
    amount: Optional[Decimal] = Field(None, gt=0)
    type: Optional[TransactionType] = None
    category: Optional[str] = Field("other", max_length=50)
    date: Optional[date] = None

    @field_validator("category")
    @classmethod
    def normalize_category(cls, v: str) -> str:
        return v.strip().lower() or "other"

class TransactionOut(BaseModel):
    id: int
    title: str
    amount: Decimal
    type: TransactionType
    category: str
    date: date

    class Config:
        from_attributes = True

class Income_ExpenseOut(BaseModel):
    title: str
    amount: Decimal
    category: str
    date: date

class SummaryOut(BaseModel):
    income: Decimal
    expenses: Decimal
    balance: Decimal

class CategoryBreakdown(BaseModel):
    category: str
    category_total: Decimal
    percentage_of_income: Decimal
    percentage_of_expenditure: Decimal