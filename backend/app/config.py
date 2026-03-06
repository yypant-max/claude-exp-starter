import os
from datetime import timedelta


class Config:
    SQLALCHEMY_DATABASE_URI = os.getenv(
        "DATABASE_URL",
        "postgresql://sms_user:sms_password@localhost:5432/student_management",
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = os.getenv(
        "JWT_SECRET",
        "MySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong!",
    )
    JWT_ACCESS_TOKEN_EXPIRES = timedelta(hours=24)


class TestConfig(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = "sqlite:///:memory:"
