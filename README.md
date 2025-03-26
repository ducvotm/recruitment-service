# Recruitment Service API

## What is this?

This is a backend service for a job recruitment platform built with Spring Boot. It helps connect employers with job seekers by managing job postings, employer profiles, seeker profiles, and resumes.

Think of it as the brain behind a job website - it stores all the information and handles all the behind-the-scenes work to make the website function.

## Technologies Used

- **Java 17** - The programming language
- **Spring Boot** - Framework to make Java applications easier
- **Spring Data JPA** - For talking to the database
- **Spring Security** - For keeping the API secure
- **JWT** - For user authentication (like digital ID cards)
- **MySQL** - Main database 
- **Redis** - For caching (making things faster)
- **MongoDB** - For storing API request/response logs
- **Swagger/OpenAPI** - For API documentation
- **Sentry** - For error tracking

## Main Features

### For Employers
- Create and manage company profiles
- Post new job listings
- Update job information
- View job applications

### For Job Seekers
- Create and manage personal profiles
- Upload and update resumes
- Browse job listings
- Apply to jobs

### System Features
- JWT-based authentication
- Role-based access control
- API response caching with Redis
- Request/response logging to MongoDB
- Comprehensive error handling
- Metrics collection and reporting

## Project Structure

The project follows a standard layered architecture:

```
src/main/java/vn/unigap/
├── api/
│   ├── configure/     - Configuration classes
│   ├── controller/    - REST API endpoints
│   ├── dto/           - Data Transfer Objects
│   │   ├── in/        - Input DTOs
│   │   └── out/       - Output DTOs
│   ├── entity/        - Database entities
│   │   ├── jpa/       - MySQL entities
│   │   └── mongodb/   - MongoDB entities
│   ├── repository/    - Data access
│   │   ├── jpa/       - MySQL repositories
│   │   └── mongodb/   - MongoDB repositories
│   └── service/       - Business logic
├── authentication/    - Security configuration
├── common/            - Shared utilities
│   ├── controller/    - Base controllers
│   ├── enums/         - Enumeration types
│   ├── errorcode/     - Error codes
│   ├── exception/     - Exception handling
│   ├── monitor/       - System monitoring
│   └── response/      - API response structure
└── RecruitmentServiceApplication.java - Main application entry
```

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven
- MySQL
- Redis
- MongoDB

### Setting Up the Databases
1. **MySQL Setup**:
   - Create a database named `job_db`
   - Username: `root`
   - Password: `Admin@123` (change this in production)

2. **Redis Setup**:
   - Install Redis server
   - Default port: 6379
   - Password: `Redis@123`

3. **MongoDB Setup**:
   - Install MongoDB server
   - Create a database named `sample_db`
   - Set up a user with username `user` and password `User123`

### Running the Application
1. Clone the repository
2. Configure application settings in `src/main/resources/application.yml`
3. Run the application using Maven:
```bash
mvn spring-boot:run
```
4. The application will start on port 8080 by default

## API Documentation

Once the application is running, you can access the Swagger UI to explore the API:
```
http://localhost:8080/swagger-ui.html
```

### Main API Endpoints

- **Authentication**:
  - POST `/auth/login` - Obtain JWT token

- **Employers**:
  - GET `/employer` - List all employers
  - GET `/employer/{id}` - Get employer details
  - POST `/employer` - Create new employer
  - PUT `/employer/{id}` - Update employer
  - DELETE `/employer/{id}` - Delete employer

- **Jobs**:
  - GET `/job` - List all jobs
  - GET `/job/{id}` - Get job details
  - POST `/job` - Create new job
  - PUT `/job/{id}` - Update job
  - DELETE `/job/{id}` - Delete job

- **Seekers** (Job Candidates):
  - GET `/seeker` - List all seekers
  - GET `/seeker/{id}` - Get seeker details
  - POST `/seeker` - Create new seeker
  - PUT `/seeker/{id}` - Update seeker
  - DELETE `/seeker/{id}` - Delete seeker

- **Resumes**:
  - GET `/resume` - List all resumes
  - GET `/resume/{id}` - Get resume details
  - POST `/resume` - Create new resume
  - PUT `/resume/{id}` - Update resume
  - DELETE `/resume/{id}` - Delete resume

- **Metrics**:
  - GET `/metrics` - Get application metrics

## Configuration

Key configuration files:

1. **`application.yml`** - Main configuration file including:
   - Database connections
   - Redis settings
   - MongoDB settings
   - Logging configuration

2. **`SecurityConfig.java`** - Authentication and security settings

## Error Handling

The application uses a centralized exception handling system through `ApiExceptionHandler.java`, which:
- Catches and processes all exceptions
- Returns standardized error responses
- Logs errors to Sentry for monitoring

## Caching Strategy

- Entity caching with Redis
- Cache configurations managed by `CacheConfig.java`
- Cache names defined in `application.yml`:
  - EMPLOYER
  - EMPLOYERS
  - JOB
  - JOBS
  - SEEKER
  - SEEKERS
  - RESUME
  - RESUMES

## Monitoring

- Spring Actuator endpoints available for monitoring
- Uptime monitoring through `UptimeMonitor.java`
- Sentry integration for error tracking
- Request/response logging to MongoDB

## Security

- JWT-based authentication
- Public/private key pair for JWT signing
- Role-based access control

## For New Developers

This project follows standard Spring Boot conventions. If you're new to the project:

1. Start by exploring the controller classes to understand the API endpoints
2. Look at the service interfaces to understand the business logic
3. Examine the entity classes to understand the data model
4. Run the application with the Swagger UI to see the API in action

## Tips for Contributing

1. Follow the existing code style and package structure
2. Write unit tests for new features
3. Document API changes in the Swagger annotations
4. Keep the README updated as the project evolves

## Learning Resources

If you're new to some of the technologies used:
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [JWT Authentication](https://jwt.io/introduction)

---

This project is a great showcase of Spring Boot capabilities and modern Java backend development. It demonstrates how to build a robust, scalable API with proper security, caching, and monitoring.
