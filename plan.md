# Plan: Swap Java (Spring Boot) Backend to Python (Flask)

## Assessment

### Current Backend (Java / Spring Boot 3.2.4)
- **13 Java files** across controller, service, entity, dto, repository, security, and config packages
- **PostgreSQL** database with 2 tables: `students`, `admins`
- **JWT authentication** with role-based access (ADMIN/STUDENT)
- **BCrypt** password hashing
- **12 REST endpoints** across 3 controllers (Auth, Admin, Student)
- **Data seeder** creates default admin on startup
- **Global exception handler** for validation and runtime errors

### What Stays the Same
- **Frontend** — no changes needed (same API contract, same proxy config)
- **Database** — same PostgreSQL instance, same schema
- **docker-compose.yml** — unchanged (only defines PostgreSQL)
- **API contract** — identical endpoints, request/response shapes, status codes

### Key Mapping: Spring Boot → Flask

| Spring Boot | Flask Equivalent |
|-------------|-----------------|
| Spring Data JPA | SQLAlchemy + Flask-SQLAlchemy |
| Spring Security | Flask-JWT-Extended |
| BCryptPasswordEncoder | `werkzeug.security` (or `bcrypt` lib) |
| `@RestController` | Flask Blueprints |
| `@Valid` / Bean Validation | Marshmallow or manual validation |
| `application.yml` | Python config / `.env` |
| Maven (`pom.xml`) | pip (`requirements.txt`) |
| JJWT | Flask-JWT-Extended (PyJWT under the hood) |
| GlobalExceptionHandler | Flask `@app.errorhandler` |
| DataSeeder (CommandLineRunner) | Custom CLI command or app startup hook |

---

## Implementation Plan

### Step 1: Scaffold Flask project structure

Create the new `backend/` directory layout (after removing Java files):

```
backend/
├── app/
│   ├── __init__.py          # Flask app factory
│   ├── config.py            # Configuration (DB, JWT secret, etc.)
│   ├── models/
│   │   ├── __init__.py
│   │   ├── student.py       # Student SQLAlchemy model
│   │   └── admin.py         # Admin SQLAlchemy model
│   ├── routes/
│   │   ├── __init__.py
│   │   ├── auth.py          # POST /api/auth/login
│   │   ├── admin.py         # /api/admin/* endpoints
│   │   └── student.py       # /api/student/* endpoints
│   ├── services/
│   │   ├── __init__.py
│   │   ├── auth_service.py
│   │   ├── admin_service.py
│   │   └── student_service.py
│   └── seed.py              # Data seeder (default admin)
├── requirements.txt
├── run.py                   # Entry point (python run.py)
└── tests/
    └── ...                  # Unit tests (pytest)
```

### Step 2: Define configuration (`app/config.py`)

- Database URI: `postgresql://sms_user:sms_password@localhost:5432/student_management`
- JWT secret key: same as current (`MySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong!`)
- JWT expiration: 24 hours
- CORS: allow `http://localhost:5173`
- Server port: 8080

### Step 3: Create SQLAlchemy models (`app/models/`)

**Student model** — mirrors existing `students` table:
- `id` (Integer, PK, autoincrement)
- `email` (String, unique, not null)
- `password` (String, not null)
- `name` (String, nullable)
- `description` (String, nullable)
- `created_at` (DateTime, default=now)
- `updated_at` (DateTime, default=now, onupdate=now)

**Admin model** — mirrors existing `admins` table (same columns).

### Step 4: Implement auth routes & service (`app/routes/auth.py`, `app/services/auth_service.py`)

- `POST /api/auth/login` — check admins first, then students; validate BCrypt password; return JWT with `id`, `email`, `role` claims
- Response shape must match `LoginResponse`: `{ token, role, id, email, name }`

### Step 5: Implement student routes & service (`app/routes/student.py`, `app/services/student_service.py`)

- `GET /api/student/profile` — requires STUDENT role
- `PUT /api/student/profile` — requires STUDENT role, accepts `{ name, description }`

### Step 6: Implement admin routes & service (`app/routes/admin.py`, `app/services/admin_service.py`)

- `GET /api/admin/profile` — requires ADMIN role
- `PUT /api/admin/profile` — requires ADMIN role
- `GET /api/admin/students` — list all students
- `POST /api/admin/students` — create student (validate email uniqueness across both tables)
- `PUT /api/admin/students/<id>` — update student email/password
- `DELETE /api/admin/students/<id>` — delete student
- `GET /api/admin/admins` — list all admins
- `POST /api/admin/admins` — create admin
- `DELETE /api/admin/admins/<id>` — prevent self-deletion

### Step 7: Add error handling

- `400` for validation errors and business logic errors (email taken, invalid credentials, etc.)
- Match existing error response format: `{ "error": "message" }` and `{ "fieldName": "error message" }` for validation

### Step 8: Add data seeder (`app/seed.py`)

- On app startup, check if any admins exist
- If none, create default admin: `admin@sms.com / admin123 / Default Admin`

### Step 9: Add CORS configuration

- Allow origin: `http://localhost:5173`
- Allow methods: GET, POST, PUT, DELETE, OPTIONS
- Allow headers: all
- Support credentials

### Step 10: Write `requirements.txt`

```
Flask==3.0.2
Flask-SQLAlchemy==3.1.1
Flask-JWT-Extended==4.6.0
Flask-CORS==4.0.0
psycopg2-binary==2.9.9
werkzeug>=3.0.0
```

### Step 11: Clean up old Java backend

- Remove all Java source files (`src/`), `pom.xml`, `.mvn/`, `mvnw`, `mvnw.cmd`
- Keep `docker-compose.yml` at project root (unchanged)

### Step 12: Verify & test

- Start PostgreSQL via `docker-compose up -d`
- Install Python deps: `pip install -r requirements.txt`
- Run Flask: `python run.py`
- Confirm all 12 endpoints work with existing frontend
- Verify login with default admin credentials
- Verify JWT tokens are accepted by frontend

---

## Risks & Considerations

1. **Password compatibility**: The existing BCrypt hashes in the DB were created by Spring Security's `BCryptPasswordEncoder`. Python's `werkzeug.security.check_password_hash` uses a different format prefix. We must use the `bcrypt` Python library directly to verify existing hashes (both use standard `$2a$`/`$2b$` format, which is compatible).

2. **JWT compatibility**: If there are active sessions during migration, existing JWT tokens signed by the Java backend will still be valid as long as we use the same secret and algorithm (HS256). Flask-JWT-Extended uses PyJWT which supports this.

3. **DateTime serialization**: Java returns ISO 8601 format (`2024-01-15T10:30:00`). We must ensure Python returns the same format (use `.isoformat()` on datetime objects).

4. **Validation error format**: The Spring Boot `GlobalExceptionHandler` returns field-level errors as `{ "field": "message" }`. We need to match this format exactly.

5. **No downtime concern**: This is a dev environment, so no migration strategy needed — just swap and restart.
