from flask import Blueprint, jsonify, request
from flask_jwt_extended import get_jwt, get_jwt_identity, jwt_required

from app.services import admin_service

admin_bp = Blueprint("admin", __name__, url_prefix="/api/admin")


def require_admin():
    claims = get_jwt()
    if claims.get("role") != "ADMIN":
        return False
    return True


@admin_bp.route("/profile", methods=["GET"])
@jwt_required()
def get_profile():
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    email = get_jwt_identity()
    profile = admin_service.get_profile(email)
    return jsonify(profile), 200


@admin_bp.route("/profile", methods=["PUT"])
@jwt_required()
def update_profile():
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    email = get_jwt_identity()
    data = request.get_json() or {}
    profile = admin_service.update_profile(email, data)
    return jsonify(profile), 200


@admin_bp.route("/students", methods=["GET"])
@jwt_required()
def get_students():
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    students = admin_service.get_all_students()
    return jsonify(students), 200


@admin_bp.route("/students", methods=["POST"])
@jwt_required()
def add_student():
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    data = request.get_json() or {}
    student = admin_service.add_student(data)
    return jsonify(student), 200


@admin_bp.route("/students/<int:student_id>", methods=["PUT"])
@jwt_required()
def update_student(student_id):
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    data = request.get_json() or {}
    student = admin_service.update_student(student_id, data)
    return jsonify(student), 200


@admin_bp.route("/students/<int:student_id>", methods=["DELETE"])
@jwt_required()
def remove_student(student_id):
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    admin_service.remove_student(student_id)
    return "", 204


@admin_bp.route("/admins", methods=["GET"])
@jwt_required()
def get_admins():
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    admins = admin_service.get_all_admins()
    return jsonify(admins), 200


@admin_bp.route("/admins", methods=["POST"])
@jwt_required()
def add_admin():
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    data = request.get_json() or {}
    admin = admin_service.add_admin(data)
    return jsonify(admin), 200


@admin_bp.route("/admins/<int:admin_id>", methods=["DELETE"])
@jwt_required()
def remove_admin(admin_id):
    if not require_admin():
        return jsonify({"error": "Access denied"}), 403
    email = get_jwt_identity()
    admin_service.remove_admin(admin_id, email)
    return "", 204
