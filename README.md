# 🚀 Recruitment Service API

## What Is This Project?

Hello there! This project is a Spring Boot application that serves as the backend for a job recruitment platform. Think of it as the "brain" behind a job search website - it connects employers who need workers with job seekers who need jobs!

Just like how a matchmaking service helps people find compatible partners, our Recruitment Service API helps match the right people with the right jobs. It keeps track of:
- Companies that are hiring
- Jobs those companies are offering
- People looking for work
- Resumes those people create

## 🏗️ Project Structure - The Building Blocks

Our project follows a clean, layered architecture that separates different concerns:

```
src/main/java/vn/unigap/
├── api/
│   ├── controller/ - Receives web requests (like a receptionist)
│   ├── dto/        - Carries data between layers (like messengers)
│   ├── entity/     - Represents database tables (like blueprints)
│   ├── repository/ - Talks to the database (like librarians)
│   └── service/    - Contains business logic (like managers)
├── authentication/ - Handles security (like security guards)
├── common/         - Shared tools (like utility belts)
└── RecruitmentServiceApplication.java - The starting point
```

## 📊 The Data Model - How Things Are Connected

Our system has six main entities that work together:

1. **EMPLOYER** - Companies looking to hire people
    - Can post multiple job listings

2. **JOB** - Individual job openings
    - Posted by one employer
    - Belongs to one or more fields
    - Available in one or more provinces

3. **SEEKER** - People looking for jobs
    - Lives in one province
    - Can have multiple resumes

4. **RESUME** - Job seeker's CV
    - Belongs to one seeker
    - Targets specific job fields
    - Can be used to apply in multiple provinces

5. **JOB_FIELD** - Categories of jobs (IT, Marketing, etc.)
    - Pre-loaded in the database

6. **JOB_PROVINCE** - Geographical areas
    - Pre-loaded in the database

Think of these as interconnected puzzle pieces that form our complete system!

## 🛠️ Technologies We're Using

- **Java 17** - Our main programming language
- **Spring Boot** - Framework that makes Java development easier
- **Spring Data JPA** - Simplifies database interactions
- **Spring Security & JWT** - Keeps our application secure
- **MySQL** - Stores most of our data
- **Redis** - Makes things faster by caching frequently accessed data
- **MongoDB** - Logs all API requests and responses
- **Swagger/OpenAPI** - Creates interactive API documentation
- **Sentry** - Helps us track and fix errors

## 🌟 Features - What Our API Can Do

### For Employers
- Create and manage company profiles
- Post new job listings with details about requirements
- Update or remove job listings
- Find candidates that match job requirements

### For Job Seekers
- Create personal profiles
- Create and manage multiple resumes
- Specify skills and preferred job fields
- Set preferred work locations

### System Features
- Secure authentication using JWT tokens
- Standardized API responses
- Caching for improved performance
- Comprehensive error handling
- Detailed logging and monitoring

## 🔄 API Response Format

All our APIs return responses in a consistent JSON format:

```json
{
  "errorCode": 0,
  "statusCode": 200,
  "message": "Success!",
  "object": { /* returned data */ }
}
```

For paginated data, the `object` field follows this structure:

```json
{
  "page": 1,
  "pageSize": 10,
  "totalElements": 100,
  "totalPages": 10,
  "data": [ /* array of items */ ]
}
```

This consistency makes our API predictable and easier to work with!

## 🏁 How to Run the Project

### What You'll Need First

Think of these as your ingredients before cooking:

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Redis 6.0 or higher
- MongoDB 4.4 or higher

### Setting Up Your Kitchen (Environment)

1. **Prepare MySQL**:
   ```sql
   CREATE DATABASE job_db;
   ```

2. **Configure the application**:
    - Open `src/main/resources/application.yml`
    - Update database credentials if needed

3. **Cook the Project (Build & Run)**:
   ```bash
   # Go to the project directory
   cd recruitment-service-api

   # Clean and build the project
   mvn clean package

   # Run the application
   mvn spring-boot:run
   ```

4. The application will be ready to serve at `http://localhost:8080`

5. Check out the API documentation at:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 📚 Main API Endpoints - Our Service Menu

- **Authentication**:
    - `POST /auth/login` - Get your access token

- **Employers**:
    - `GET /employer` - List all employers
    - `GET /employer/{id}` - Get one employer's details
    - `POST /employer` - Create a new employer
    - `PUT /employer/{id}` - Update an employer
    - `DELETE /employer/{id}` - Remove an employer

- **Jobs**:
    - `GET /job` - List all jobs
    - `GET /job/{id}` - Get one job's details
    - `POST /job` - Create a new job
    - `PUT /job/{id}` - Update a job
    - `DELETE /job/{id}` - Remove a job

- **Seekers**:
    - `GET /seeker` - List all job seekers
    - `GET /seeker/{id}` - Get one seeker's details
    - `POST /seeker` - Create a new seeker
    - `PUT /seeker/{id}` - Update a seeker
    - `DELETE /seeker/{id}` - Remove a seeker

- **Resumes**:
    - `GET /resume` - List all resumes
    - `GET /resume/{id}` - Get one resume's details
    - `POST /resume` - Create a new resume
    - `PUT /resume/{id}` - Update a resume
    - `DELETE /resume/{id}` - Remove a resume

- **Metrics**:
    - `GET /metrics` - Get system statistics
    - `GET /metrics/{id}` - Get job with matching seekers

## 🔒 Security - Keeping Things Safe

Our application uses JWT (JSON Web Token) for authentication:

1. First, you get your digital "ID card" (token) by logging in:
   ```
   POST /auth/login
   ```

2. Then you present this ID card for all other requests:
   ```
   Authorization: Bearer your-token-here
   ```

This works just like showing your ID to enter a secure building!

## 🧪 Testing - Making Sure Everything Works

To run the tests:

```bash
mvn test
```

This checks if all parts of our application are working correctly.

## 🎓 What You'll Learn

By working on this project, you'll gain experience with:

- Spring configuration using Java Configuration and Annotations
- Testing Spring applications with JUnit 5
- Data access with JDBC, JPA, and Spring Data
- Building applications with Spring Boot
- Understanding auto-configuration, starters, and properties
- Creating REST APIs
- Implementing security with Spring Security
- Setting up monitoring with Spring Boot Actuator

## 🔄 Development Workflow

The project is structured into sprints:
- Each sprint lasts 1-2 weeks
- Work on feature branches named `feature/sprint-X`
- Merge completed sprints into the `main` branch

## 👥 Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/sprint-1`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/sprint-1`
5. Create a Pull Request

---

I hope this README helps you understand the project! If you have questions, feel free to reach out. Happy coding! 🎉