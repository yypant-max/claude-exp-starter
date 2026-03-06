from flask import Blueprint, jsonify, request
from flask_jwt_extended import get_jwt, get_jwt_identity, jwt_required

from app.services import student_service

student_bp = Blueprint("student", __name__, url_prefix="/api/student")


def require_student():
    claims = get_jwt()
    if claims.get("role") != "STUDENT":
        return False
    return True


@student_bp.route("/profile", methods=["GET"])
@jwt_required()
def get_profile():
    if not require_student():
        return jsonify({"error": "Access denied"}), 403
    email = get_jwt_identity()
    profile = student_service.get_profile(email)
    return jsonify(profile), 200


@student_bp.route("/profile", methods=["PUT"])
@jwt_required()
def update_profile():
    if not require_student():
        return jsonify({"error": "Access denied"}), 403
    email = get_jwt_identity()
    data = request.get_json() or {}
    profile = student_service.update_profile(email, data)
    return jsonify(profile), 200
