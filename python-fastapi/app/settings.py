from pydantic_settings import BaseSettings, SettingsConfigDict
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent


class Settings(BaseSettings):
    # Make DATABASE_URL optional at import time so creation of Settings
    # does not raise a ValidationError when .env is missing.
    DATABASE_URL: str | None = None

    model_config = SettingsConfigDict(
        env_file=BASE_DIR / ".env"
    )


settings = Settings()