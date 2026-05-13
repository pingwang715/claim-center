# Claim Center

> Full-stack application demonstrating scalable software engineering practices, secure backend architecture, and modern frontend development.

---

# 📌 Overview

This project was built to demonstrate real-world software engineering skills including:

- Full-stack architecture
- REST API design
- Authentication & authorization
- Database modeling
- Responsive UI development
- State management
- Error handling
- Clean code structure


The application solves:
**digital transformation of insurance companies on the full life cycle of claims management**

---

# ✨ Key Features

## User Features
- Secure user authentication (JWT)
- Account registration & login
- CRUD operations
- Responsive UI

## Technical Features
- RESTful API architecture
- Protected routes
- Modular backend structure
- Reusable frontend components
- API integration with async handling
- Centralized error management

---

# 🏗️ System Architecture

```text
Frontend (TypeScript)
       ↓
REST API (Spring Boot)
       ↓
Database (MySQL)
```

---

# 🛠️ Tech Stack

## Frontend
- React
- TypeScript
- Tailwind CSS
- Axios

## Backend
- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Hibernate / JPA

## Database
- MySQL

## DevOps / Tools
- Docker
- Postman
- Git
- Maven

---

# ⚙️ Installation

## Clone Repository

```bash
git clone https://github.com/pingwang715/claim-center.git
cd claim-center
```

---

# 🔧 Backend Setup

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

---

# 💻 Frontend Setup

```bash
cd claim-center-ui
npm install
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

# 🔐 Environment Variables

## Frontend `.env`

```env
VITE_API_BASE_URL="http://localhost:8080/api/v1"
```

---

# 🧪 API Testing

Example request:

```http
POST /api/v1/auth/login
```

Request body:

```json
{
  "email": "test@example.com",
  "password": "password123"
}
```

---

# 🧪 Testing

## Frontend

```bash
npm run test
```

## Backend

```bash
./mvnw test
```

---

# 📈 Engineering Highlights

This project demonstrates:

- Layered backend architecture
- Secure authentication workflows
- Clean REST API design
- Separation of concerns
- Component reusability
- Professional Git workflow

---

# 🧠 Challenges & Learnings

## Challenges
- Implementing JWT authentication securely
- Designing backend architecture to keep an audit trail of claims
- Handling async API errors gracefully

## Learnings
- Improved understanding of backend security
- Better API contract design
- Real-world frontend/backend integration

---

# 📌 Future Improvements

- [ ] Add paginattion
- [ ] Add file upload
- [ ] Add CI/CD pipeline

---

# 👨‍💻 Author

## Ping Wang

- GitHub: https://github.com/pingwang715
- LinkedIn: https://linkedin.com/in/pingwangedhec

---

# 📄 License

This project is licensed under the MIT License.
