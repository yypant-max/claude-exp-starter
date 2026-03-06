from tests.conftest import auth_header


def test_get_admin_profile(client, admin_token):
    resp = client.get("/api/admin/profile", headers=auth_header(admin_token))
    assert resp.status_code == 200
    data = resp.get_json()
    assert data["email"] == "admin@sms.com"
    assert data["name"] == "Default Admin"


def test_update_admin_profile(client, admin_token):
    resp = client.put(
        "/api/admin/profile",
        json={"name": "Updated Admin", "description": "Updated desc"},
        headers=auth_header(admin_token),
    )
    assert resp.status_code == 200
    data = resp.get_json()
    assert data["name"] == "Updated Admin"
    assert data["description"] == "Updated desc"


def test_crud_students(client, admin_token):
    headers = auth_header(admin_token)

    # Create student
    resp = client.post(
        "/api/admin/students",
        json={"email": "new@student.com", "password": "pass1234", "name": "New Student"},
        headers=headers,
    )
    assert resp.status_code == 200
    student_id = resp.get_json()["id"]

    # List students
    resp = client.get("/api/admin/students", headers=headers)
    assert resp.status_code == 200
    assert len(resp.get_json()) >= 1

    # Update student
    resp = client.put(
        f"/api/admin/students/{student_id}",
        json={"email": "updated@student.com"},
        headers=headers,
    )
    assert resp.status_code == 200
    assert resp.get_json()["email"] == "updated@student.com"

    # Delete student
    resp = client.delete(f"/api/admin/students/{student_id}", headers=headers)
    assert resp.status_code == 204


def test_add_student_duplicate_email(client, admin_token):
    headers = auth_header(admin_token)
    client.post(
        "/api/admin/students",
        json={"email": "dup@test.com", "password": "pass1234"},
        headers=headers,
    )
    resp = client.post(
        "/api/admin/students",
        json={"email": "dup@test.com", "password": "pass1234"},
        headers=headers,
    )
    assert resp.status_code == 400


def test_crud_admins(client, admin_token):
    headers = auth_header(admin_token)

    # Create admin
    resp = client.post(
        "/api/admin/admins",
        json={"email": "new@admin.com", "password": "pass1234", "name": "New Admin"},
        headers=headers,
    )
    assert resp.status_code == 200
    admin_id = resp.get_json()["id"]

    # List admins
    resp = client.get("/api/admin/admins", headers=headers)
    assert resp.status_code == 200
    assert len(resp.get_json()) >= 2

    # Delete admin
    resp = client.delete(f"/api/admin/admins/{admin_id}", headers=headers)
    assert resp.status_code == 204


def test_admin_cannot_delete_self(client, admin_token):
    headers = auth_header(admin_token)
    # Get own admin id
    resp = client.get("/api/admin/profile", headers=headers)
    admin_id = resp.get_json()["id"]

    resp = client.delete(f"/api/admin/admins/{admin_id}", headers=headers)
    assert resp.status_code == 400


def test_student_cannot_access_admin_endpoints(client, student_token):
    headers = auth_header(student_token)
    resp = client.get("/api/admin/students", headers=headers)
    assert resp.status_code == 403
