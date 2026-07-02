# ResearchIQ Backend

ResearchIQ is a Dockerized Spring Boot microservices backend for a research paper intelligence platform. It supports user authentication, research paper upload, cloud file storage, AI-powered paper analysis, insight generation, asynchronous event processing, and containerized deployment.

## Tech Stack

* Java 17
* Spring Boot
* Spring Cloud Gateway
* Netflix Eureka Service Discovery
* Spring Security + JWT
* Spring Data JPA
* MySQL
* MongoDB Atlas
* RabbitMQ
* Cloudinary
* Gemini API
* Docker
* Docker Compose

## Microservices

| Service             |         Port | Description                                    |
| ------------------- | -----------: | ---------------------------------------------- |
| Discovery Service   |         8761 | Eureka service registry                        |
| API Gateway         |         8080 | Single entry point for frontend/API clients    |
| Auth Service        |         8081 | User registration, login, JWT authentication   |
| Paper Service       |         8082 | PDF upload, Cloudinary storage, paper metadata |
| AI Analysis Service |         8083 | AI-based paper analysis using Gemini           |
| Insight Service     |         8084 | Advanced research insights and AI utilities    |
| MySQL               |    3307:3306 | Stores auth and paper data                     |
| RabbitMQ            | 5672 / 15672 | Message broker for async processing            |

## Architecture

```text
Frontend
   |
   v
API Gateway
   |
   v
Eureka Discovery
   |
   +--> Auth Service  ---> MySQL: researchiq_auth
   |
   +--> Paper Service ---> MySQL: researchiq_paper
   |          |
   |          v
   |       RabbitMQ
   |
   +--> AI Analysis Service ---> MongoDB Atlas + Gemini API
   |
   +--> Insight Service -------> MongoDB Atlas + Gemini API
```

## Features

* User registration and login
* JWT-based authentication
* API Gateway based routing
* Eureka service discovery
* PDF upload support
* Cloudinary file storage
* MySQL persistence
* MongoDB Atlas integration
* RabbitMQ-based asynchronous communication
* AI analysis using Gemini API
* Global exception handling
* Request validation
* CORS configuration
* Dockerized microservices
* Docker Compose orchestration

## Prerequisites

Install the following:

* Java 17
* Maven
* Docker Desktop
* Docker Compose
* MySQL client or MySQL Workbench optional
* Postman optional

## Environment Variables

Create a `.env` file in the project root:

```env
MYSQL_ROOT_PASSWORD=your_mysql_root_password
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_jwt_secret

MONGO_URI=your_mongodb_atlas_uri
GEMINI_API_KEY=your_gemini_api_key

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

Do not commit `.env`.

Use `.env.example` to document required variables:

```env
MYSQL_ROOT_PASSWORD=your_mysql_root_password
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_jwt_secret

MONGO_URI=your_mongodb_atlas_uri
GEMINI_API_KEY=your_gemini_api_key

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

## MySQL Initialization

The project uses one MySQL container with two databases.

Create:

```text
mysql/init.sql
```

Content:

```sql
CREATE DATABASE IF NOT EXISTS researchiq_auth;
CREATE DATABASE IF NOT EXISTS researchiq_paper;
```

## Run with Docker Compose

From the project root:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

Stop all containers:

```bash
docker compose down
```

View logs:

```bash
docker logs -f api-gateway
docker logs -f auth-service
docker logs -f paper-service
docker logs -f ai-analysis-service
docker logs -f insight-service
```

## Service URLs

| Tool/Service       | URL                    |
| ------------------ | ---------------------- |
| API Gateway        | http://localhost:8080  |
| Eureka Dashboard   | http://localhost:8761  |
| RabbitMQ Dashboard | http://localhost:15672 |
| MySQL Host Access  | localhost:3307         |

RabbitMQ default login:

```text
username: guest
password: guest
```

## API Testing

All APIs should be tested through the API Gateway:

```text
http://localhost:8080
```

Do not call individual services directly from the frontend.

### Auth APIs

Register:

```http
POST http://localhost:8080/auth/register
```

Example body:

```json
{
  "fullName": "Ashish Kumar",
  "email": "ashish@example.com",
  "password": "Password@123"
}
```

Login:

```http
POST http://localhost:8080/auth/login
```

Example body:

```json
{
  "email": "ashish@example.com",
  "password": "Password@123"
}
```

### Paper APIs

Upload paper:

```http
POST http://localhost:8080/papers/upload
```

Headers:

```text
Authorization: Bearer <token>
X-User-Email: ashish@example.com
```

Body:

```text
form-data
key: file
type: File
value: select PDF
```

Get paper:

```http
GET http://localhost:8080/papers/{paperId}
```

Delete paper:

```http
DELETE http://localhost:8080/papers/{paperId}
```

### AI Analysis APIs

Trigger analysis:

```http
POST http://localhost:8080/analysis/{paperId}
```

Get analysis:

```http
GET http://localhost:8080/analysis/{paperId}
```

Delete analysis:

```http
DELETE http://localhost:8080/analysis/{paperId}
```

## Database Access

Enter MySQL container:

```bash
docker exec -it mysql mysql -uroot -p
```

Show databases:

```sql
SHOW DATABASES;
```

Use auth database:

```sql
USE researchiq_auth;
SHOW TABLES;
```

Use paper database:

```sql
USE researchiq_paper;
SHOW TABLES;
```

If using MySQL Workbench:

```text
Host: localhost
Port: 3307
Username: root
Password: value from .env
```

## Docker Images

The backend uses Docker images for:

```text
discovery-service
api-gateway
auth-service
paper-service
ai-analysis-service
insight-service
mysql:8.0
rabbitmq:3-management
```

To rebuild a service image manually:

```bash
cd auth-service
mvn clean package
docker build -t auth-service .
```

Then restart Compose:

```bash
docker compose up -d
```

## Project Structure

```text
ResearchIQ/
├── api-gateway/
├── auth-service/
├── discovery-service/
├── paper-service/
├── ai-analysis-service/
├── insight-service/
├── mysql/
│   └── init.sql
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Important Notes

* The frontend should call only the API Gateway.
* Service-to-service communication happens through Eureka and Docker networking.
* MySQL and RabbitMQ run in Docker.
* MongoDB is hosted on MongoDB Atlas.
* Secrets must be stored in `.env`, not committed to GitHub.
* Rotate any secrets that were previously exposed before making the repository public.

## Future Improvements

* Swagger/OpenAPI documentation
* GitHub Actions CI/CD
* Centralized logging
* Redis caching
* Role-based authorization
* Cloud deployment using a VM or container hosting platform
* React frontend integration
