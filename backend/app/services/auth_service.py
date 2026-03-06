import bcrypt
from flask_jwt_extended import create_access_token

from app.models import Admin, Student


def verify_password(plain_password, hashed_password):
    return bcrypt.checkpw(
        plain_password.encode("utf-8"), hashed_password.encode("utf-8")
    )


def hash_password(password):
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def login(email, password):
    admin = Admin.query.filter_by(email=email).first()
    if admin and verify_password(password, admin.password):
        token = create_access_token(
            identity=admin.email,
            additional_claims={"id": admin.id, "role": "ADMIN"},
        )
        return {
            "token": token,
            "role": "ADMIN",
            "id": admin.id,
            "email": admin.email,
            "name": admin.name,
        }

    student = Student.query.filter_by(email=email).first()
    if student and verify_password(password, student.password):
        token = create_access_token(
            identity=student.email,
            additional_claims={"id": student.id, "role": "STUDENT"},
        )
        return {
            "token": token,
            "role": "STUDENT",
            "id": student.id,
            "email": student.email,
            "name": student.name,
        }

    raise ValueError("Invalid email or password")
