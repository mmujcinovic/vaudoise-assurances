# Technical exercise – Vaudoise Assurances
## Description
REST API for managing clients (Person / Company) and their contracts.
## Architecture & Design
The project follows a layered architecture with clear separation of concerns:

• **Controller layer**  
Handles HTTP requests and responses. It exposes only the necessary endpoints and delegates all logic to services.

• **Service layer**  
Contains the business rules: client creation, contract lifecycle, automatic endDate updates, soft deletion, and cost aggregation. It ensures validation and consistency.

• **Repository layer**  
Uses Spring Data JPA to manage persistence transparently, with custom queries when needed (e.g. active contracts, sums).

**DTO/Record models** are used to isolate API input/output from entities. Validation is handled with annotations and service checks.

**Soft delete** is applied on clients by using an `active` flag and closing contracts instead of removing data.

The business logic prevents operations on inactive clients to ensure data integrity. For example, creating a contract for a deactivated client returns a functional error. This approach guarantees consistency and preserves the relationship with existing contracts.

Two profiles are available:

• `h2` (file-based, no setup)

• `postgres` (via Docker)

This structure ensures maintainability, testability, and clean responsibility boundaries.
## Prerequisites
🔹 Java 17 or higher - is required to run the application.  
🔹 Maven Wrapper - is included ine the project (`./mvnw` or `mvnw.cmd`).  
🔹 All commands below were tested using Windows Command Prompt (cmd).
## Running the application
### Clone the repository (required for all options)
1️⃣ Clone the project.
```bash
git clone https://github.com/mmujcinovic/vaudoise-assurances.git
```
2️⃣ Navigate into the project directory.
```bash
cd vaudoise-assurances
```
### ▶ Option 1 — H2 profile (default, no installation required)
Runs the application using the embedded in-memory H2 database.
```bash
./mvnw spring-boot:run
```
### ▶ Option 2 — Docker (PostgreSQL profile)
🔹 Prerequisite: Docker must be installed on your machine (Docker Desktop or equivalent).  
  
Run the application using a PostgreSQL database in Docker.  
  
1️⃣ Start the PostgreSQL container.
```bash
docker compose up -d db
```
2️⃣ Run the application with the postgres profile.
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```
## API usage examples
### POST
Creates a new client of type Company.
```bash
curl -i -X POST http://localhost:8080/api/clients -H "Content-Type: application/json" -d "{\"name\":\"Vaudoise Assurances\",\"phone\":\"+4121453671\",\"email\":\"info@vaudoiseassurances.ch\",\"companyIdentifier\":\"CHF-vdAss19\"}"
```
Expected response.
```bash
HTTP 201 Created
Location: /clients/1
```
```bash
{
  "id": 1,
  "name": "Vaudoise Assurances",
  "phone": "+4121453671",
  "email": "info@vaudoiseassurances.ch",
  "active": true,
  "companyIdentifier": "CHF-vdAss19"
}
```
Creates a new client of type Person.
```bash
curl -i -X POST http://localhost:8080/api/clients -H "Content-Type: application/json" -d "{\"name\":\"Alice\",\"phone\":\"+4122333444\",\"email\":\"alice@gmail.com\",\"birthdate\":\"2000-01-01\"}"
```
Expected response.
```bash
HTTP 201 Created
Location: /clients/2
```
```bash
{
  "id": 2,
  "name": "Alice",
  "phone": "+4122333444",
  "email": "alice@gmail.com",
  "active": true,
  "birthdate": "2000-01-01"
}
```
Creates a new contract for the specified client.
```bash
curl -i -X POST http://localhost:8080/api/contracts/1 -H "Content-Type: application/json" -d "{\"startDate\":\"2025-10-20\",\"endDate\":\"\",\"costAmount\":\"500\"}"
```
Expected response.
```bash
HTTP 201 Created
Location: /contracts/1
```
```bash
{
  "id": 1,
  "clientId": 1,
  "startDate": "2025-10-20",
  "endDate": null,
  "costAmount": 500
}
```
### GET
Retrieves an active client.
```bash
curl -i -X GET http://localhost:8080/api/clients/1
```
Expected response of type Company.
```bash
{
  "id": 1,
  "name": "info-dev",
  "phone": "+4121476539",
  "email": "info@info-dev.ch",
  "active": true,
  "companyIdentifier": "CHF-geFi12s"
}
```
Expected response of type Person.
```bash
{
  "id": 1,
  "name": "Pierre",
  "phone": "+4122786489",
  "email": "pierre.info@gmail.com",
  "active": true,
  "birthdate": "2003-05-03"
}
```
Retrieves the list of active contracts for a specific client, optionally filtered by update date.
```bash
curl -i -X GET http://localhost:8080/api/contracts/1
```
```bash
curl -i -X GET "http://localhost:8080/api/contracts/1?updatedAfter=2025-01-01&updatedBefore=2025-12-31"
```
Expected response.
```bash
[
  {
    "id": 1,
    "clientId": 1,
    "startDate": "2025-10-20",
    "endDate": null,
    "costAmount": 500
  },
  {
    "id": 2,
    "clientId": 1,
    "startDate": "2025-10-20",
    "endDate": null,
    "costAmount": 250
  }
]
```
Retrieves the total cost of all active contracts for a specific client.
```bash
curl -i -X GET http://localhost:8080/api/contracts/1/sumCost
```
Expected response.
```bash
{
  "clientId": 1,
  "sumCost": 750
}
```
### PUT
Updates an active client of type Company.
```bash
curl -i -X PUT http://localhost:8080/api/clients/1 -H "Content-Type: application/json" -d "{\"name\":\"Oracle\",\"phone\":\"+4121451432\",\"email\":\"info@oracle.ch\",\"companyIdentifier\":\"INT-stWej75\"}"
```
Expected response.
```bash
{
  "id": 1,
  "name": "Oracle",
  "phone": "+4121451432",
  "email": "info@oracle.ch",
  "active": true,
  "companyIdentifier": "INT-stWej75"
}
```
Updates an active client of type Person.
```bash
curl -i -X PUT http://localhost:8080/api/clients/1 -H "Content-Type: application/json" -d "{\"name\":\"Marie\",\"phone\":\"+4100649012\",\"email\":\"marie@gmail.com\",\"birthdate\":\"1992-10-08\"}"
```
Expected response.
```bash
{
  "id": 1,
  "name": "Marie",
  "phone": "+4100649012",
  "email": "marie@gmail.com",
  "active": true,
  "birthdate": "1992-10-08"
}
```
Updates the cost of an existing contract.
```bash
curl -i -X PUT http://localhost:8080/api/contracts/1/cost -H "Content-Type: application/json" -d "{\"costAmount\":\"100\"}"
```
Expected response.
```bash
{
  "id": 1,
  "clientId": 1,
  "startDate": "2025-10-20",
  "endDate": null,
  "costAmount": 100
}
```
### DELETE
Deactivates a client.
```bash
curl -i -X DELETE http://localhost:8080/api/clients/1
```
