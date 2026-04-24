import enum
from sqlalchemy import Column, Integer, String, Date, Enum, Numeric
from .database import Base

class TransactionType(str, enum.Enum):
    income = "income"
    expense = "expense"

class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, nullable=False)
    amount = Column(Numeric(24, 2), nullable=False)
    type = Column(Enum(TransactionType), nullable=False)
    category  = Column(String(collation="NOCASE"), nullable=False, default="other", unique=True)
    date = Column(Date, nullable=False)
