import pytest

from app import create_app, db as _db
from app.config import TestConfig
from app.services.auth_service import hash_password


@pytest.fixture
def app():
    app = create_app(TestConfig)
    yield app


@pytest.fixture
def db(app):
    with app.app_context():
        yield _db


@pytest.fixture
def client(app):
    return app.test_client()


@pytest.fixture
def admin_token(client, db):
    """Login as the seeded default admin and return the JWT token."""
    resp = client.post(
        "/api/auth/login",
        json={"email": "admin@sms.com", "password": "admin123"},
    )
    return resp.get_json()["token"]


@pytest.fixture
def student_token(client, db, admin_token):
    """Create a student and return its JWT token."""
    client.post(
        "/api/admin/students",
        json={"email": "student@test.com", "password": "pass1234", "name": "Test Student"},
        headers={"Authorization": f"Bearer {admin_token}"},
    )
    resp = client.post(
        "/api/auth/login",
        json={"email": "student@test.com", "password": "pass1234"},
    )
    return resp.get_json()["token"]


def auth_header(token):
    return {"Authorization": f"Bearer {token}"}
