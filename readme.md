# ResearchIQ

ResearchIQ is a full-stack AI-powered research paper intelligence platform built with React and Spring Boot microservices. It supports user authentication, PDF upload, cloud file storage, asynchronous AI paper analysis, AI-generated summaries, paper comparison, research gap discovery, literature review generation, and Dockerized backend deployment.

---

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Cloud Gateway
- Netflix Eureka Service Discovery
- Spring Security + JWT
- Spring Data JPA / Hibernate
- MySQL
- MongoDB Atlas
- RabbitMQ
- Cloudinary
- Gemini API
- OpenFeign
- Docker
- Docker Compose

### Frontend

- React
- Vite
- Tailwind CSS
- Axios
- React Router
- Lucide React
- React Markdown
- Remark GFM

---

## Microservices

| Service | Port | Description |
|---|---:|---|
| Discovery Service | 8761 | Eureka service registry |
| API Gateway | 8080 | Single entry point for frontend/API clients |
| Auth Service | 8081 | User registration, login, JWT authentication |
| Paper Service | 8082 | PDF upload, Cloudinary storage, paper metadata |
| AI Analysis Service | 8083 | AI-powered paper analysis using Gemini |
| Insight Service | 8084 | Advanced research insights, comparison, gaps, literature review |
| MySQL | 3307:3306 | Stores auth and paper metadata |
| RabbitMQ | 5672 / 15672 | Message broker for asynchronous processing |
| React Frontend | 5173 | User interface for authentication, uploads, dashboard, and research tools |

---

## Architecture

```text
React Frontend :5173
      |
      v
API Gateway :8080
      |
      v
Eureka Discovery :8761
      |
      +--> Auth Service :8081
      |        |
      |        v
      |     MySQL - researchiq_auth
      |
      +--> Paper Service :8082
      |        |
      |        +--> MySQL - researchiq_paper
      |        |
      |        +--> Cloudinary
      |        |
      |        v
      |     RabbitMQ
      |
      +--> AI Analysis Service :8083
      |        |
      |        +--> MongoDB Atlas
      |        |
      |        +--> Gemini API
      |
      +--> Insight Service :8084
               |
               +--> MongoDB Atlas
               |
               +--> Gemini API
```

---

## Core Flow

```text
User registers/logs in
    |
    v
JWT token generated
    |
    v
Frontend stores token and calls API Gateway
    |
    v
User uploads PDF
    |
    v
Paper Service stores PDF in Cloudinary
    |
    v
Paper metadata saved in MySQL
    |
    v
RabbitMQ upload event published
    |
    v
AI Analysis Service consumes event
    |
    v
PDF text is extracted
    |
    v
Gemini generates paper analysis
    |
    v
Analysis result saved in MongoDB
    |
    v
Frontend displays dashboard and research tools
```

---

## Features

### Backend Features

- User registration and login
- JWT-based authentication
- API Gateway routing
- Eureka service discovery
- PDF upload support
- Cloudinary file storage
- MySQL persistence for users and paper metadata
- MongoDB Atlas persistence for AI analysis
- RabbitMQ-based asynchronous event processing
- Gemini-powered research paper analysis
- AI-generated paper summary, keywords, novelty score, research gap, limitations, and future work
- Paper comparison
- Research gap discovery
- Literature review generation
- Delete flow for paper metadata and analysis cleanup
- CORS configuration for frontend integration
- Dockerized microservices
- Docker Compose orchestration

### Frontend Features

- Login and registration pages
- Protected routes
- JWT-based frontend authentication
- Dashboard for uploaded/analyzed papers
- PDF upload from UI
- Research Center workspace
- Select papers for AI actions
- AI paper insights
- Paper comparison
- Research gap analysis
- Literature review generation
- Markdown popup rendering for AI results
- Scrollable comparison tables
- Copy and download AI-generated markdown results
- Paper details modal with open/download PDF options
- Delete paper from UI

---

## Prerequisites

Install the following:

- Java 17
- Maven
- Docker Desktop
- Docker Compose
- Node.js
- npm
- MySQL client or MySQL Workbench optional
- Postman optional

---

## Environment Variables

Create a `.env` file in the project root for backend services.

```env
MYSQL_ROOT_PASSWORD=your_mysql_root_password
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000

MONGO_URI=your_mongodb_atlas_uri
GEMINI_API_KEY=your_gemini_api_key

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

Do not commit `.env`.

Create `.env.example` in the project root:

```env
MYSQL_ROOT_PASSWORD=your_mysql_root_password
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000

MONGO_URI=your_mongodb_atlas_uri
GEMINI_API_KEY=your_gemini_api_key

CORS_ALLOWED_ORIGINS=http://localhost:5173
```

For the frontend, create:

```text
researchiq-frontend/.env
```

Content:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Also create:

```text
researchiq-frontend/.env.example
```

Content:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Do not commit the real frontend `.env`.

---

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

---

## Run Backend with Docker Compose

From the project root:

```bash
docker compose up -d
```

Build and start all services:

```bash
docker compose up -d --build
```

Check running containers:

```bash
docker ps
```

Stop all containers:

```bash
docker compose down
```

Stop containers and remove volumes:

```bash
docker compose down -v
```

View logs:

```bash
docker logs -f api-gateway
docker logs -f auth-service
docker logs -f paper-service
docker logs -f ai-analysis-service
docker logs -f insight-service
```

---

## Run Frontend

Go to the frontend directory:

```bash
cd researchiq-frontend
```

Install dependencies:

```bash
npm install
```

Start development server:

```bash
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

Build frontend:

```bash
npm run build
```

---

## Service URLs

| Tool / Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| RabbitMQ Dashboard | http://localhost:15672 |
| MySQL Host Access | localhost:3307 |

RabbitMQ default login:

```text
username: guest
password: guest
```

---

# API Documentation

All APIs should be tested through the API Gateway:

```text
http://localhost:8080
```

Do not call individual services directly from the frontend.

---

## Auth APIs

### Register User

```http
POST /auth/register
```

Full URL:

```http
POST http://localhost:8080/auth/register
```

Request body:

```json
{
  "fullName": "Ashish Kumar",
  "email": "ashish@example.com",
  "password": "Password@123"
}
```

---

### Login User

```http
POST /auth/login
```

Full URL:

```http
POST http://localhost:8080/auth/login
```

Request body:

```json
{
  "email": "ashish@example.com",
  "password": "Password@123"
}
```

Response example:

```json
{
  "token": "jwt_token_here"
}
```

---

## Paper APIs

All paper APIs require authentication.

Header:

```http
Authorization: Bearer <token>
```

The frontend should only send the JWT token. The API Gateway extracts the user email from the token and forwards it internally as `X-User-Email`.

---

### Upload Paper

```http
POST /papers/upload
```

Full URL:

```http
POST http://localhost:8080/papers/upload
```

Headers:

```http
Authorization: Bearer <token>
```

Body:

```text
form-data
key: file
type: File
value: select PDF file
```

---

### Get Paper by ID

```http
GET /papers/{paperId}
```

Example:

```http
GET http://localhost:8080/papers/1
```

Headers:

```http
Authorization: Bearer <token>
```

---

### Delete Paper

```http
DELETE /papers/{paperId}
```

Example:

```http
DELETE http://localhost:8080/papers/1
```

Headers:

```http
Authorization: Bearer <token>
```

This deletes paper metadata from MySQL and publishes a delete event so related AI analysis can be removed from MongoDB.

---

## AI Analysis APIs

All analysis APIs require authentication.

Header:

```http
Authorization: Bearer <token>
```

---

### Trigger Analysis Manually

```http
POST /analysis/{paperId}
```

Example:

```http
POST http://localhost:8080/analysis/1
```

Usually analysis is triggered automatically after upload using RabbitMQ. This endpoint is useful for manual retry/testing.

---

### Get All Analyses

```http
GET /analysis
```

Example:

```http
GET http://localhost:8080/analysis
```

---

### Get Analysis by Paper ID

```http
GET /analysis/{paperId}
```

Example:

```http
GET http://localhost:8080/analysis/1
```

---

### Get Dashboard Analyses for Logged-in User

```http
GET /analysis/dashboard
```

Example:

```http
GET http://localhost:8080/analysis/dashboard
```

The API Gateway injects the logged-in user email internally using the JWT token.

---

### Delete Analysis by Paper ID

```http
DELETE /analysis/{paperId}
```

Example:

```http
DELETE http://localhost:8080/analysis/1
```

---

## Insight APIs

All insight APIs require authentication.

Header:

```http
Authorization: Bearer <token>
```

---

### Get Detailed Paper Insights

```http
GET /insights/paper/{paperId}
```

Example:

```http
GET http://localhost:8080/insights/paper/1
```

Response example:

```json
{
  "markdown": "# Paper Insights\n\n..."
}
```

---

### Compare Papers

```http
POST /insights/compare
```

Example:

```http
POST http://localhost:8080/insights/compare
```

Request body:

```json
{
  "paperIds": [1, 2]
}
```

Response example:

```json
{
  "markdown": "# Paper Comparison\n\n..."
}
```

---

### Discover Research Gaps

```http
POST /insights/research-gap
```

Example:

```http
POST http://localhost:8080/insights/research-gap
```

Request body:

```json
{
  "paperIds": [1, 2, 3]
}
```

Response example:

```json
{
  "markdown": "# Research Gap Analysis\n\n..."
}
```

---

### Generate Literature Review

```http
POST /insights/literature-review
```

Example:

```http
POST http://localhost:8080/insights/literature-review
```

Request body:

```json
{
  "paperIds": [1, 2, 3]
}
```

Response example:

```json
{
  "markdown": "# Literature Review\n\n..."
}
```

---

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

Check papers:

```sql
USE researchiq_paper;
SELECT * FROM papers;
```

Check users:

```sql
USE researchiq_auth;
SELECT * FROM users;
```

Using MySQL Workbench:

```text
Host: localhost
Port: 3307
Username: root
Password: value from .env
```

---

## RabbitMQ

RabbitMQ dashboard:

```text
http://localhost:15672
```

Default credentials:

```text
username: guest
password: guest
```

Common queues:

```text
paper.upload.queue
paper.delete.queue
```

RabbitMQ is used to trigger asynchronous AI analysis after a paper upload and to handle cleanup events after deletion.

---

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

Build all services using Docker Compose:

```bash
docker compose up -d --build
```

Rebuild one service manually:

```bash
cd auth-service
mvn clean package -DskipTests
docker build -t auth-service:latest .
```

Restart one service:

```bash
docker compose up -d --no-deps --force-recreate auth-service
```

---

## Project Structure

```text
ResearchIQ/
├── api-gateway/
├── auth-service/
├── discovery-service/
├── paper-service/
├── ai-analysis-service/
├── insight-service/
├── researchiq-frontend/
├── mysql/
│   └── init.sql
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Important Notes

- The frontend should call only the API Gateway.
- Do not expose individual microservices directly to the frontend.
- Service-to-service communication happens through Docker networking, Eureka, and OpenFeign.
- MySQL and RabbitMQ run in Docker.
- MongoDB is hosted on MongoDB Atlas.
- Secrets must be stored in `.env`, not committed to GitHub.
- Rotate any secrets that were previously exposed before making the repository public.
- Gemini free-tier quota can cause `429 TOO_MANY_REQUESTS` errors during frequent testing.

---

## Recommended `.gitignore`

```gitignore
# Environment
.env
*.env
!.env.example

# Maven / Java
target/
*.jar

# Node / React
node_modules/
dist/
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# IDE
.idea/
.vscode/
*.iml

# Logs
logs/
*.log

# OS
.DS_Store
Thumbs.db
```

---

## GitHub Setup

Initialize Git from the project root:

```bash
git init
git add .
git commit -m "Initial commit: ResearchIQ full-stack microservices platform"
```

Create a GitHub repository, then push:

```bash
git branch -M main
git remote add origin https://github.com/your-username/researchiq-ai-paper-platform.git
git push -u origin main
```

Recommended repository name:

```text
researchiq-ai-paper-platform
```

Recommended repository description:

```text
Full-stack AI research paper intelligence platform built with React and Spring Boot microservices, featuring JWT auth, RabbitMQ async processing, MySQL, MongoDB, Cloudinary, Gemini API, Eureka, Docker, and Spring Cloud Gateway.
```

Recommended topics:

```text
spring-boot
microservices
spring-cloud
api-gateway
eureka
jwt-authentication
rabbitmq
mysql
mongodb
docker
docker-compose
react
vite
tailwindcss
gemini-api
cloudinary
ai
research-paper-analysis
```

---

## Future Improvements

- Swagger/OpenAPI documentation
- GitHub Actions CI/CD
- Centralized logging using ELK or Grafana Loki
- Redis caching
- Role-based authorization
- Retry and dead-letter queue support for RabbitMQ
- AI quota fallback and retry handling
- Kubernetes deployment
- Cloud deployment on AWS/GCP/Azure
- Unit and integration tests
- Frontend deployment integration

---

## Author

Ashish Kumar
