import enum
from sqlalchemy import Column, Integer, String, Float, Date, Enum
from .database import Base

class TransactionType(str, enum.Enum):
    income = "income"
    expense = "expense"

class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, nullable=False)
    amount = Column(Float, nullable=False)
    type = Column(Enum(TransactionType), nullable=False)
    category  = Column(String, nullable=True)
    date = Column(Date, nullable=False)
