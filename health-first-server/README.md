# Health First Server - Provider Registration Module

A comprehensive Spring Boot application for healthcare provider registration with secure authentication and validation.

## Features

- **Secure Provider Registration**: Complete registration system with validation
- **Password Security**: BCrypt hashing with 12 salt rounds
- **Input Sanitization**: Protection against XSS attacks
- **Comprehensive Validation**: Email, phone, license number uniqueness
- **H2 In-Memory Database**: For development and testing
- **RESTful API**: Clean, documented endpoints
- **Unit & Integration Tests**: Comprehensive test coverage

## Technology Stack

- **Spring Boot 3.2.5**
- **Spring Security** with BCrypt password encoding
- **Spring Data JPA** for database operations
- **H2 Database** (in-memory)
- **Lombok** for reducing boilerplate
- **Jakarta Validation** for input validation
- **JUnit 5** for testing

## Database Schema

### Provider Entity
- `id` (UUID, primary key)
- `first_name` (string, required, 2-50 chars)
- `last_name` (string, required, 2-50 chars)
- `email` (string, unique, required, valid email)
- `phone_number` (string, unique, required, international format)
- `password_hash` (string, required, BCrypt hashed)
- `specialization` (string, required, 3-100 chars, predefined list)
- `license_number` (string, unique, required, alphanumeric)
- `years_of_experience` (integer, 0-50)
- `clinic_address` (embedded object)
- `verification_status` (enum: PENDING/VERIFIED/REJECTED)
- `is_active` (boolean, default true)
- `created_at` (timestamp)
- `updated_at` (timestamp)

### Clinic Address (Embedded)
- `street` (string, required, max 200 chars)
- `city` (string, required, max 100 chars)
- `state` (string, required, max 50 chars)
- `zip` (string, required, valid postal code)

## API Endpoints

### 1. Provider Registration
**POST** `/api/v1/provider/register`

**Request Body:**
```json
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john.doe@clinic.com",
  "phone_number": "+1234567890",
  "password": "SecurePassword123!",
  "confirm_password": "SecurePassword123!",
  "specialization": "Cardiology",
  "license_number": "MD123456789",
  "years_of_experience": 10,
  "clinic_address": {
    "street": "123 Medical Center Dr",
    "city": "New York",
    "state": "NY",
    "zip": "10001"
  }
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Provider registered successfully. Verification email sent.",
  "data": {
    "providerId": "uuid-here",
    "email": "john.doe@clinic.com",
    "verificationStatus": "PENDING"
  }
}
```

### 2. Get Valid Specializations
**GET** `/api/v1/provider/specializations`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Valid specializations retrieved successfully",
  "data": [
    "Cardiology",
    "Dermatology",
    "Endocrinology",
    "Gastroenterology",
    "General Practice",
    "Hematology",
    "Infectious Disease",
    "Internal Medicine",
    "Neurology",
    "Obstetrics and Gynecology",
    "Oncology",
    "Ophthalmology",
    "Orthopedics",
    "Otolaryngology",
    "Pediatrics",
    "Psychiatry",
    "Pulmonology",
    "Radiology",
    "Rheumatology",
    "Urology",
    "Emergency Medicine",
    "Family Medicine",
    "Geriatrics",
    "Nephrology",
    "Pathology"
  ]
}
```

## Validation Rules

### Password Requirements
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character (@$!%*?&)

### Email Validation
- Must be unique
- Valid email format
- Automatically converted to lowercase

### Phone Number Validation
- Must be unique
- International format (e.g., +1234567890)
- Must start with + followed by country code

### License Number Validation
- Must be unique
- Alphanumeric characters only
- Automatically converted to uppercase

### Specialization Validation
- Must be from predefined list
- 3-100 characters

## Security Features

- **Password Hashing**: BCrypt with 12 salt rounds
- **Input Sanitization**: HTML character escaping
- **SQL Injection Protection**: JPA parameterized queries
- **XSS Protection**: Input sanitization
- **CSRF Protection**: Disabled for API endpoints

## Error Handling

### HTTP Status Codes
- `200` - Success
- `201` - Created (successful registration)
- `400` - Bad Request (validation errors)
- `409` - Conflict (duplicate resources)
- `422` - Unprocessable Entity (business logic errors)
- `500` - Internal Server Error

### Error Response Format
```json
{
  "success": false,
  "message": "Error description",
  "data": {
    "fieldName": "field-specific error message"
  }
}
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run
```bash
# Clone the repository
git clone <repository-url>
cd health-first-server

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Access Points
- **Application**: http://localhost:8765
- **H2 Console**: http://localhost:8765/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)

## Testing

### Run All Tests
```bash
mvn test
```

### Test Coverage
- **Unit Tests**: Service layer validation and business logic
- **Integration Tests**: Controller endpoints and API responses
- **Test Scenarios**:
  - Successful registration
  - Password mismatch
  - Invalid specialization
  - Duplicate email/phone/license
  - Input sanitization
  - Validation errors

### Test Data
The application includes comprehensive test data and scenarios covering:
- Valid provider registration
- All validation error cases
- Security testing (XSS protection)
- Duplicate resource handling

## Project Structure

```
src/
├── main/
│   ├── java/com/thinkitive/thinkemr/
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   └── ProviderController.java
│   │   ├── dto/
│   │   │   ├── ApiResponse.java
│   │   │   ├── ProviderRegistrationRequest.java
│   │   │   └── ProviderRegistrationResponse.java
│   │   ├── entity/
│   │   │   ├── ClinicAddress.java
│   │   │   └── Provider.java
│   │   ├── exception/
│   │   │   ├── DuplicateResourceException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ValidationException.java
│   │   ├── repository/
│   │   │   └── ProviderRepository.java
│   │   ├── service/
│   │   │   └── ProviderService.java
│   │   └── ThinkEMRApplication.java
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/thinkitive/thinkemr/
        ├── controller/
        │   └── ProviderControllerTest.java
        └── service/
            └── ProviderServiceTest.java
```

## Configuration

The application uses H2 in-memory database by default. Configuration can be modified in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
server:
  port: 8765
```

## Future Enhancements

- Email verification system
- JWT token authentication
- Provider profile management
- Admin verification workflow
- Audit logging
- Rate limiting
- API documentation with Swagger
- Docker containerization 