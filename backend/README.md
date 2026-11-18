# Backend — Logistics Packages

API REST del Módulo 1 (Java 21, Spring Boot 3.2, arquitectura hexagonal).

```bash
cp .env.example .env
docker compose up --build   # API en http://localhost:8080, Swagger en /swagger-ui.html
./gradlew test              # requiere Docker (Testcontainers)
```

Estructura:

```
src/main/java/com/logistics/packages/
  domain/          modelo, value objects, excepciones (sin dependencias de framework)
  application/     casos de uso y puertos
  infrastructure/  controladores REST, JPA, SQS, S3, seguridad, config
src/main/resources/db/migration/   migraciones Flyway
```

Ver el [README principal](../README.md).
