# SupportLink Backend

This is the backend implementation for SupportLink based on PRD v1.0.

## Tech Stack
- Java 21
- Spring Boot 3.2.0
- Spring Security (JWT)
- Spring Data JPA
- MySQL

## Setup

1. **Database**: Ensure you have MySQL running. Create a database named `support_link`.
   ```sql
   CREATE DATABASE support_link;
   ```
2. **Configuration**: Update `src/main/resources/application.properties` with your MySQL credentials.
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

## Build Command

```powershell
mvn clean package -DskipTests   
```

## Running the Application

```bash
mvn spring-boot:run
```

## API Documentation

### Auth
- `POST /api/auth/register`: Register a new user.
- `POST /api/auth/login`: Login (User or Agent).
- `GET /api/auth/me`: Get current user info.

### FAQs
- `GET /api/faqs`: List FAQs.
- `GET /api/faqs/{id}`: Get FAQ detail.
- `POST /api/faqs`: Create FAQ (Admin only).
- `PUT /api/faqs/{id}`: Update FAQ (Admin only).
- `DELETE /api/faqs/{id}`: Delete FAQ (Admin only).

### Tickets
- `POST /api/tickets`: Create ticket (User only).
- `GET /api/tickets`: List tickets (User: own, Agent: all).
- `GET /api/tickets/{id}`: Get ticket detail.
- `PATCH /api/tickets/{id}`: Update ticket status/assignee (Agent only).
- `GET /api/tickets/{id}/replies`: Get replies.
- `POST /api/tickets/{id}/replies`: Add reply.

## Notes
- `Ticket` entity was enhanced with `description` and `category` fields to meet API requirements, although they were missing in the PRD schema table definition.

## Swagger UI
- 애플리케이션을 실행하면 `/swagger-ui/index.html` 경로(기본 설정 시)에서 API 문서를 확인하고 테스트  

```url 
http://localhost:8081/swagger-ui/index.html
```

