def test_login_admin_success(client):
    resp = client.post(
        "/api/auth/login",
        json={"email": "admin@sms.com", "password": "admin123"},
    )
    assert resp.status_code == 200
    data = resp.get_json()
    assert data["role"] == "ADMIN"
    assert data["email"] == "admin@sms.com"
    assert "token" in data


def test_login_invalid_credentials(client):
    resp = client.post(
        "/api/auth/login",
        json={"email": "admin@sms.com", "password": "wrong"},
    )
    assert resp.status_code == 400


def test_login_missing_fields(client):
    resp = client.post("/api/auth/login", json={"email": "admin@sms.com"})
    assert resp.status_code == 400


def test_login_student_success(client, admin_token):
    from tests.conftest import auth_header

    client.post(
        "/api/admin/students",
        json={"email": "s@test.com", "password": "pass1234"},
        headers=auth_header(admin_token),
    )
    resp = client.post(
        "/api/auth/login",
        json={"email": "s@test.com", "password": "pass1234"},
    )
    assert resp.status_code == 200
    assert resp.get_json()["role"] == "STUDENT"
