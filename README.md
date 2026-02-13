# Student Management System

A full-stack application for managing students and admins, built with **React + TypeScript** (frontend) and **Spring Boot + Java** (backend), backed by **PostgreSQL**.

## Architecture

```
├── backend/          # Spring Boot REST API (Java 17, Maven)
├── frontend/         # React SPA (TypeScript, Vite)
├── docker-compose.yml # PostgreSQL database
└── README.md
```

## Features

### Admin
- Login with email/password
- Update own name and description
- Add, edit (email/password), and remove students
- Add and remove other admins

### Student
- Login with credentials (set by admin)
- Update own name and description

## Tech Stack

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Frontend  | React 18, TypeScript, Vite, Axios   |
| Backend   | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Auth      | JWT (jjwt library)                  |
| Database  | PostgreSQL 16                       |
| Infra     | Docker Compose                      |

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose

### 1. Start the Database

```bash
docker-compose up -d
```

This starts PostgreSQL on port 5432 with database `student_management`.

### 2. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

On first start, a default admin is created:
- **Email:** admin@sms.com
- **Password:** admin123

### 3. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:5173`.

## API Endpoints

### Authentication
| Method | Endpoint          | Description        | Auth |
|--------|-------------------|--------------------|------|
| POST   | `/api/auth/login` | Login (admin/student) | No |

### Admin
| Method | Endpoint                | Description             | Auth  |
|--------|-------------------------|-------------------------|-------|
| GET    | `/api/admin/profile`    | Get admin profile       | Admin |
| PUT    | `/api/admin/profile`    | Update name/description | Admin |
| GET    | `/api/admin/students`   | List all students       | Admin |
| POST   | `/api/admin/students`   | Add a student           | Admin |
| PUT    | `/api/admin/students/:id` | Update student email/password | Admin |
| DELETE | `/api/admin/students/:id` | Remove a student      | Admin |
| GET    | `/api/admin/admins`     | List all admins         | Admin |
| POST   | `/api/admin/admins`     | Add an admin            | Admin |
| DELETE | `/api/admin/admins/:id` | Remove an admin         | Admin |

### Student
| Method | Endpoint              | Description             | Auth    |
|--------|-----------------------|-------------------------|---------|
| GET    | `/api/student/profile` | Get student profile    | Student |
| PUT    | `/api/student/profile` | Update name/description | Student |

## Project Structure

### Backend (`backend/`)
```
src/main/java/com/sms/
├── StudentManagementApplication.java   # Entry point
├── config/
│   ├── SecurityConfig.java             # Spring Security + CORS
│   ├── GlobalExceptionHandler.java     # Error handling
│   └── DataSeeder.java                 # Default admin seed
├── security/
│   ├── JwtTokenProvider.java           # JWT generation/validation
│   └── JwtAuthenticationFilter.java    # Request filter
├── entity/
│   ├── Admin.java                      # Admin JPA entity
│   └── Student.java                    # Student JPA entity
├── repository/
│   ├── AdminRepository.java
│   └── StudentRepository.java
├── dto/                                # Request/response objects
├── service/
│   ├── AuthService.java
│   ├── AdminService.java
│   └── StudentService.java
└── controller/
    ├── AuthController.java
    ├── AdminController.java
    └── StudentController.java
```

### Frontend (`frontend/`)
```
src/
├── main.tsx                 # Entry point
├── App.tsx                  # Routes
├── api/client.ts            # Axios API client
├── context/AuthContext.tsx   # Auth state management
├── components/
│   ├── Navbar.tsx
│   └── ProtectedRoute.tsx
├── pages/
│   ├── LoginPage.tsx
│   ├── AdminDashboard.tsx
│   ├── AdminProfile.tsx
│   ├── ManageStudents.tsx
│   ├── ManageAdmins.tsx
│   ├── StudentDashboard.tsx
│   └── StudentProfile.tsx
└── styles/App.css
```
