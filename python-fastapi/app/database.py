from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from .settings import settings

# Read DATABASE_URL from settings but do not force engine creation at import time.
DATABASE_URL = settings.DATABASE_URL

# Engine and SessionLocal will be initialized lazily via `init_engine`.
engine = None
SessionLocal = None

# SQLAlchemy Base class - models inherit from this
Base = declarative_base()


def init_engine(database_url: str):
    """Initialize the SQLAlchemy engine and sessionmaker.

    Safe to call multiple times; subsequent calls are no-ops.
    """
    global engine, SessionLocal
    if engine is None:
        engine = create_engine(database_url)
        SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    return engine


def get_db():
    if SessionLocal is None:
        raise RuntimeError("Database not configured. Call init_engine() with a valid DATABASE_URL.")
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()