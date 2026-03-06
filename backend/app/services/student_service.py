from app import db
from app.models import Student


def get_profile(email):
    student = Student.query.filter_by(email=email).first()
    if not student:
        raise ValueError("Student not found")
    return student.to_profile()


def update_profile(email, data):
    student = Student.query.filter_by(email=email).first()
    if not student:
        raise ValueError("Student not found")

    if "name" in data and data["name"] is not None:
        student.name = data["name"]
    if "description" in data and data["description"] is not None:
        student.description = data["description"]

    db.session.commit()
    return student.to_profile()
