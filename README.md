# JobBoard API

**JobBoard API** is a backend service built with **Java 21**, **Spring Boot 3**, and **PostgreSQL**.
It provides a clean and extensible REST API for managing job postings and user accounts, with features such as JWT authentication, pagination, rate limiting, and a modular job ranking system.

This project is designed as a production-style backend, following clean architecture principles and emphasizing maintainability, scalability, and testability.

## Features

- JWT Authentication & Authorization (users + roles)
- CRUD for job postings
- Search with filters + ranked results
- Configurable job ranking algorithm
- Pagination
- Rate limiting
- Global exception handling
- PostgreSQL persistence with JPA/Hibernate
- Dockerized database
- Unit tests with JUnit5 + Mockito

## Requirements

Before running the backend, ensure you have:

- **Java 21**
- **Docker** (for running PostgreSQL)

## Configuration

Create a .env file in the project root with the following environment variables:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dev-jobboard-db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JOBBOARD_JWT_SECRET=your_secret_key
```

## Running the backend

1. Clone the repository and navigate to the project directory.

    ``` bash
    git clone <repo-url>
    cd jobboard-api
    ```

2. Start the backend using the provided script:
    ```bash
    bash ./scripts/start-backend.sh
    ```
    This script will:
    - Load environment variables from .env
    - Start a PostgreSQL container
    - Initialize the database
    - Launch the Spring Boot service

    If you encounter a permissions issue with the Maven wrapper:
    ```bash
    chmod +x ./mvnw
    ```

## Running Tests

To execute the test suite:
```bash
bash ./scripts/start-tests.sh
```

## Database

- Database schema: ```./scripts/schema.sql```
- Sample data (optional): ```./scripts/sample_data.sql```

When using the startup script, the database will self-initialize automatically.

## API Overview

All endpoints follow a RESTful structure under: ```/api/v1```.

### Endpoints Summary

| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/v1/user` | Create a new user |
| `POST` | `/api/v1/job` | Create a new job posting |
| `GET` | `/api/v1/job/{id}` | Retrieve job by ID |
| `PUT` | `/api/v1/job/{id}` | Update a job |
| `DELETE` | `/api/v1/job/{id}` | Delete job |
| `GET` | `/api/v1/job/search` | Search and rank jobs based on criteria. |

## Architecture

The project follows a layered architecture:

- **Controller layer** — Handles requests, input validation, and API responses.
- **Service layer** — Contains business logic, orchestration, and rule evaluation.
- **Repository layer** — Manages data access using JPA/Hibernate.
- **Domain layer** — Core entities, rules, and business models.
- **DTOs (Data Transfer Objects)** — Encapsulate data passed between layers and to the API, ensuring separation between internal models and API contracts.

This organization ensures:

- **Maintainability** — clean separation of concerns  
- **Scalability** — logic is isolated and extendable  
- **Testability** — each layer can be tested independently  

Technologies such as Spring Boot, JPA, and Docker enable production-like development workflows.

## Job Ranking Algorithm

The job ranking system evaluates job postings using a **modular, weighted, rule-driven algorithm**.
Each rule contributes a normalized score, allowing the system to combine multiple factors into a single ranking metric.

### How it works

1. **Modular Rules**  
    Each rule implements the JobRule interface, allowing new rules to be added without modifying existing logic.

2. **Rule Examples**  
    - **Recency** — Jobs posted more recently score higher.
    - **Salary** — Jobs with competitive salaries are prioritized relative to the dataset.
    - **Company Activity** — Companies with more active postings receive a higher ranking score.

3. **Normalization**
    All raw scores are normalized between 0 (lowest) and 1 (highest) to allow consistent weighting.

4. **Weights & Aggregation**  
    Each rule has a configurable weight. The final job score is:
    ```final_score = Σ (rule_normalized_score × rule_weight)```
This score is used to order search results.

**Advantages:**  
- Extensible — easily add/remove rules
- Transparent — easy to see why a job ranks higher
- Configurable — adjust weights for business priorities
- Efficient — rules operate independently and can be cached or parallelized

## Scalability Notes

For large datasets:

- **Database optimizations** — Indexes, read replicas, partitioning
- **Caching layers** — Redis or in-memory caching for frequent queries
- **Asynchronous computations** — For expensive ranking rules
- **Horizontal scaling** — Docker/Kubernetes deployments make scaling straightforward

## Bonus features implemented

- Rate limiting
- Authentication & Authorization
- Pagination
