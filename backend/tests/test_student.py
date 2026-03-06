from tests.conftest import auth_header


def test_get_student_profile(client, student_token):
    resp = client.get("/api/student/profile", headers=auth_header(student_token))
    assert resp.status_code == 200
    data = resp.get_json()
    assert data["email"] == "student@test.com"
    assert data["name"] == "Test Student"


def test_update_student_profile(client, student_token):
    resp = client.put(
        "/api/student/profile",
        json={"name": "Updated Name", "description": "My description"},
        headers=auth_header(student_token),
    )
    assert resp.status_code == 200
    data = resp.get_json()
    assert data["name"] == "Updated Name"
    assert data["description"] == "My description"


def test_admin_cannot_access_student_endpoints(client, admin_token):
    resp = client.get(
        "/api/student/profile", headers=auth_header(admin_token)
    )
    assert resp.status_code == 403


def test_unauthenticated_access_denied(client):
    resp = client.get("/api/student/profile")
    assert resp.status_code == 401
