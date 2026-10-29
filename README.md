# Brain Dribbler - Applied Jobs Log Portal

The Applied Jobs Log Portal is a full-stack tracking registry and enterprise technology sandbox. This platform functions as a secure application lifecycle tracking portal while serving as a real-world playground for self-managed Linux infrastructure deployments.

---

## Technical Stack Overview

* Backend Framework: Spring Boot
* View Engine: Thymeleaf (HTML5 / CSS3)
* Database Layer: PostgreSQL
* Security Layer: Spring Security
* Production Environment: Ubuntu Linux
* Transactional Email: Mailgun API (via Unirest Java)

---

## Core Features & Infrastructure Sandbox

### 1. Application Lifecycle Management
Tracks corporate recruiter touchpoints, application milestone timelines, resume variations, and interview schedules within a secure relational model.

### 2. User Access & Token-Based Authentication
Manages the transition from an invite-only setup to an open platform featuring self-service user registration, email verification tokens, and automated password recovery.

### 3. Server Operations & Hosting Experimentation
Serves as a live deployment ground for low-level server operations, including configuring systemd services, managing file system permissions, and tuning Nginx reverse proxies.

---

## About the Project Ecosystem

This repository represents the application tracking node (`://braindribbler.com`). If you are interested in exploring other engineering experiments, software utility portals, or technical server specifications within this network, please refer directly to the central hub website:

* Central Gateway: [https://braindribbler.com](https://braindribbler.com)

---

## Getting Started & Local Development

### Prerequisites
* Java 17 SDK or higher
* Maven 3.8+
* PostgreSQL 14+
* Mailgun credentials configured inside `src/main/resources/secret.properties`

### Execution
1. Verify your local PostgreSQL instances match current JPA database definitions.
2. Compile and boot the local runtime environment:
   ```bash
   mvn spring-boot:run
   ```
3. The platform maps internally to `http://localhost:8080`.

---

## Platform Documentation & Technical Guides

Detailed engineering manuals and operational documentation are organized in the internal document repository to manage and scale this codebase:

* **Developer Testing Guide:** See [docs/TESTING.md](docs/TESTING.md) to run targeted command-line validation tests using isolated Maven flags (`-Dtest`) and triage raw Mailgun JSON query signatures.
* **Automated Deployment Manual:** See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) to understand the architecture behind the automated GitHub Actions pipeline, the PostgreSQL Shadow Schema migration engine, and zero-downtime Tomcat parallel deployments.
* **Relational ERD Schema Layouts:** Coming soon.
