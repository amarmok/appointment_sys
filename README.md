# Appointment System

Spring Boot appointment scheduling system with JWT auth, role-based access,
working schedules, and smart suggestions.

## Run
Prereqs:
- Java 17+
- MySQL running (see env vars below)

Config (env vars or `src/main/resources/application.properties`):
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` and `JWT_EXPIRATION_MINUTES` (optional; defaults in properties)
- `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_AUTH`, `SMTP_STARTTLS` (optional)

Build:
```powershell
.\mvnw.cmd clean package
```

Run:
```powershell
.\mvnw.cmd spring-boot:run
```

App listens on `https://localhost:8443` (HTTPS is enabled via `keystore.p12`).

## API
All `/api/**` endpoints require a JWT (Bearer token) except `/api/auth/**`.

Auth:
- `POST /api/auth/register` (public)
- `POST /api/auth/login` (public)

Services (ADMIN for mutations):
- `GET /api/services`
- `POST /api/services`
- `PUT /api/services/{id}`
- `DELETE /api/services/{id}`

Working Schedules (ADMIN for mutations):
- `GET /api/working-schedules`
- `POST /api/working-schedules`
- `PUT /api/working-schedules/{id}`
- `DELETE /api/working-schedules/{id}`

Appointments:
- `POST /api/appointments` (CUSTOMER)
- `GET /api/appointments/me` (CUSTOMER)
- `GET /api/appointments/staff` (STAFF)
- `PUT /api/appointments/{id}/cancel` (CUSTOMER)
- `PUT /api/appointments/{id}/status?status=APPROVED|CANCELLED|FINISHED` (ADMIN)
- `GET /api/appointments/suggest?serviceId&targetDate&customerPreference`
- `GET /api/schedule/suggest?serviceId&preferredDate&preference`

WebSocket:
- STOMP endpoint: `/ws`
- Topic: `/topic/appointments`

## Team
The first stage: Qamar Al-Miqdad - Rataj Upholstered
The second stage and additional request: Naya Zodeh - Shahd Muammar
The third stage: Hala Nasser.
