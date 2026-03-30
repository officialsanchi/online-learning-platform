# Authentication System Backend

A comprehensive Java Spring Boot authentication system with role-based access control, JWT tokens, and email verification.

## Features

### 🔐 **Authentication & Authorization**
- User registration and login
- JWT token-based authentication
- Role-based access control (USER, ADMIN)
- Password encryption with BCrypt
- Email verification system

### 📊 **CRUD Operations**
- **CREATE**: Register new users
- **READ**: Get users (all, by ID, by username)
- **UPDATE**: Update user profiles and roles
- **DELETE**: Delete users (Admin only)

### 🏗️ **Architecture**
- **Layered Architecture**: Controller → Service → Repository
- **Microservices Ready**: Modular design for easy scaling
- **Security Layer**: JWT authentication filter and entry point
- **Data Layer**: JPA with H2 database (development)

## API Endpoints

### Public Endpoints
```
POST /api/auth/register        - Register new user
POST /api/auth/login          - User login
GET  /api/auth/verify-email   - Verify email with token
POST /api/auth/resend-verification - Resend verification email
```

### Protected Endpoints
```
GET    /api/auth/users         - Get all users (Admin only)
GET    /api/auth/users/{id}    - Get user by ID
GET    /api/auth/users/username/{username} - Get user by username
PUT    /api/auth/users/{id}    - Update user profile
PUT    /api/auth/users/{id}/role - Update user role (Admin only)
DELETE /api/auth/users/{id}    - Delete user (Admin only)
```

## Request/Response Examples

### Register User
```json
POST /api/auth/register
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

### Login User
```json
POST /api/auth/login
{
  "username": "johndoe",
  "password": "password123"
}
```

### Response Format
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "emailVerified": false
}
```

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: H2 (development), JPA/Hibernate
- **Build Tool**: Maven
- **Java Version**: 17

## Dependencies

```xml
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- jjwt (JWT library)
- h2database
```

## Configuration

### JWT Settings
```properties
application.security.jwt.secret-key=YOUR_SECRET_KEY
application.security.jwt.expiration=86400000          # 24 hours
application.security.jwt.refresh-token.expiration=604800000  # 7 days
```

### Database Settings
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

## Getting Started

1. **Clone the repository**
2. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
3. **Access H2 Console**: http://localhost:8080/h2-console
4. **Test API**: Use Postman or curl to test endpoints

## Security Features

- **Password Encryption**: BCrypt hashing
- **JWT Tokens**: Secure stateless authentication
- **Role-based Access**: Method-level security with @PreAuthorize
- **Email Verification**: Token-based email verification
- **CORS Support**: Cross-origin resource sharing enabled

## Project Structure

```
src/main/java/com/auth/
├── config/           # Security configuration
├── controller/       # REST controllers
├── dto/             # Data transfer objects
├── model/           # Entity classes
├── repository/      # Data access layer
└── service/         # Business logic layer
```

## Development Notes

- H2 console available at `/h2-console` for development
- JWT secret should be changed in production
- Email verification requires SMTP configuration
- Logging enabled for debugging

## Future Enhancements

- Email service integration (SMTP)
- Password reset functionality
- OAuth2 integration
- Rate limiting
- Audit logging
