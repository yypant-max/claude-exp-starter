from flask import Flask, jsonify
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()
jwt = JWTManager()


def create_app(config_class=None):
    app = Flask(__name__)

    if config_class:
        app.config.from_object(config_class)
    else:
        from app.config import Config
        app.config.from_object(Config)

    db.init_app(app)
    jwt.init_app(app)
    CORS(
        app,
        origins=["http://localhost:5173"],
        supports_credentials=True,
        allow_headers=["*"],
        methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    )

    from app.routes.auth import auth_bp
    from app.routes.admin import admin_bp
    from app.routes.student import student_bp

    app.register_blueprint(auth_bp)
    app.register_blueprint(admin_bp)
    app.register_blueprint(student_bp)

    @app.errorhandler(ValueError)
    def handle_value_error(e):
        return jsonify({"error": str(e)}), 400

    with app.app_context():
        db.create_all()
        _seed_default_admin()

    return app


def _seed_default_admin():
    from app.models import Admin
    from app.services.auth_service import hash_password

    if Admin.query.count() == 0:
        admin = Admin(
            email="admin@sms.com",
            password=hash_password("admin123"),
            name="Default Admin",
            description="System administrator",
        )
        db.session.add(admin)
        db.session.commit()
