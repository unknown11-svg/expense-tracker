import enum
from sqlalchemy import Column, Integer, String, Date, Enum, Numeric, CheckConstraint
from .database import Base

class TransactionType(str, enum.Enum):
    income = "income"
    expense = "expense"

class Transaction(Base):
    __tablename__ = "transactions"
    __table_args__ = (
        CheckConstraint("category = lower(category)", name="ck_category_lowercase"),
    )

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, nullable=False)
    amount = Column(Numeric(24, 2), nullable=False)
    type = Column(Enum(TransactionType), nullable=False)
    category  = Column(String, nullable=False, default="other")
    date = Column(Date, nullable=False)
