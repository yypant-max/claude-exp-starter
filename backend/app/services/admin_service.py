from app import db
from app.models import Admin, Student
from app.services.auth_service import hash_password


def get_profile(email):
    admin = Admin.query.filter_by(email=email).first()
    if not admin:
        raise ValueError("Admin not found")
    return admin.to_profile()


def update_profile(email, data):
    admin = Admin.query.filter_by(email=email).first()
    if not admin:
        raise ValueError("Admin not found")

    if "name" in data and data["name"] is not None:
        admin.name = data["name"]
    if "description" in data and data["description"] is not None:
        admin.description = data["description"]

    db.session.commit()
    return admin.to_profile()


def get_all_students():
    students = Student.query.all()
    return [s.to_profile() for s in students]


def add_student(data):
    email = data.get("email")
    password = data.get("password")
    name = data.get("name")

    if not email or not password:
        raise ValueError("Email and password are required")

    if Student.query.filter_by(email=email).first():
        raise ValueError("Email already exists")
    if Admin.query.filter_by(email=email).first():
        raise ValueError("Email already exists")

    student = Student(
        email=email,
        password=hash_password(password),
        name=name,
    )
    db.session.add(student)
    db.session.commit()
    return student.to_profile()


def update_student(student_id, data):
    student = db.session.get(Student, student_id)
    if not student:
        raise ValueError("Student not found")

    if "email" in data and data["email"] is not None:
        new_email = data["email"]
        if new_email != student.email:
            if Student.query.filter_by(email=new_email).first():
                raise ValueError("Email already exists")
            if Admin.query.filter_by(email=new_email).first():
                raise ValueError("Email already exists")
            student.email = new_email

    if "password" in data and data["password"] is not None:
        student.password = hash_password(data["password"])

    db.session.commit()
    return student.to_profile()


def remove_student(student_id):
    student = db.session.get(Student, student_id)
    if not student:
        raise ValueError("Student not found")
    db.session.delete(student)
    db.session.commit()


def get_all_admins():
    admins = Admin.query.all()
    return [a.to_profile() for a in admins]


def add_admin(data):
    email = data.get("email")
    password = data.get("password")
    name = data.get("name")

    if not email or not password:
        raise ValueError("Email and password are required")

    if Admin.query.filter_by(email=email).first():
        raise ValueError("Email already exists")
    if Student.query.filter_by(email=email).first():
        raise ValueError("Email already exists")

    admin = Admin(
        email=email,
        password=hash_password(password),
        name=name,
    )
    db.session.add(admin)
    db.session.commit()
    return admin.to_profile()


def remove_admin(admin_id, current_admin_email):
    admin = db.session.get(Admin, admin_id)
    if not admin:
        raise ValueError("Admin not found")
    if admin.email == current_admin_email:
        raise ValueError("Cannot delete yourself")
    db.session.delete(admin)
    db.session.commit()
