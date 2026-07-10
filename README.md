🏨 Hotel BDI — Sistema de Reservas con Microservicios

Spring Boot Java MySQL Swagger Eureka

Sistema de gestión hotelera desarrollado con arquitectura de microservicios usando Spring Boot, Spring Cloud y MySQL. Incluye autenticación JWT, documentación Swagger/OpenAPI y descubrimiento de servicios con Eureka.

👥 Equipo de Desarrollo

| Integrante | Microservicios |
|---|---|
| Edixon Cabriles (Jhonaiker) | ms-auth · ms-clientes · ms-reservas · ms-pagos |
| Belén Conalef | api-gateway · ms-hoteles · ms-habitaciones · ms-servicios |
| Ignacio Díaz | eureka-server · ms-notificaciones · ms-reportes · ms-housekeeping |

🏗️ Arquitectura del Sistema

```
Frontend / Postman
        │
        ▼
  api-gateway (8080) ──── eureka-server (8761)
        │
        ├── ms-auth        (8081)  — Edixon
        ├── ms-clientes    (8084)  — Edixon
        ├── ms-reservas    (8082)  — Edixon
        ├── ms-pagos       (8083)  — Edixon
        │
        ├── ms-hoteles     (8085)  — Belén
        ├── ms-habitaciones(8086)  — Belén
        ├── ms-servicios   (8087)  — Belén
        │
        ├── ms-notificaciones (8088) — Ignacio ❌ pendiente / sin conectar
        ├── ms-reportes       (8089) — Ignacio ❌ pendiente / sin conectar
        └── ms-housekeeping   (8090) — Ignacio ❌ pendiente / sin conectar
```

 📦 Microservicios

🔐 ms-auth — Puerto 8081
Gestión de autenticación y usuarios con JWT.
- Registro e inicio de sesión de usuarios
- Generación y validación de tokens JWT
- Roles de usuario (ADMIN, CLIENTE)
- **Swagger:** `http://localhost:8081/doc/swagger-ui/index.html`

👤 ms-clientes — Puerto 8084
Gestión completa de clientes del hotel.
- CRUD de clientes
- Búsqueda por email
- **Swagger:** `http://localhost:8084/doc/swagger-ui/index.html`

📅 ms-reservas — Puerto 8082
Gestión de reservas de habitaciones.
- Crear, listar, actualizar y eliminar reservas
- Consulta de reservas por cliente
- Gestión de estados de reserva
- **Swagger:** `http://localhost:8082/doc/swagger-ui/index.html`

💳 ms-pagos — Puerto 8083
Procesamiento de pagos asociados a reservas.
- Procesamiento de pagos
- Consulta de pagos por reserva
- Gestión de estados de pago
- **Swagger:** `http://localhost:8083/doc/swagger-ui/index.html`

🏩 ms-hoteles — Puerto 8085
Gestión de hoteles registrados en el sistema.

🛏️ ms-habitaciones — Puerto 8086
Gestión de habitaciones por hotel.

🛎️ ms-servicios — Puerto 8087
Servicios adicionales del hotel.

🔔 ms-notificaciones — Puerto 8088
Envío de notificaciones a clientes.
> ❌ Pendiente — aún sin conectar al resto del sistema.

 📊 ms-reportes — Puerto 8089
Generación de reportes del sistema.
> ❌ Pendiente — aún sin conectar al resto del sistema.

🧹 ms-housekeeping — Puerto 8090
Gestión de limpieza y mantenimiento de habitaciones.
> ❌ Pendiente — aún sin conectar al resto del sistema.

🚀 Cómo levantar el proyecto

Prerrequisitos
- Java 17+
- Maven 3.8+
- MySQL 8.0
- IntelliJ IDEA (recomendado)

Orden de inicio

Siempre respetar este orden:

```bash
1. Eureka Server (primero siempre)
cd eureka-server
./mvnw spring-boot:run

2. API Gateway
cd api-gateway
./mvnw spring-boot:run

3. Microservicios (en cualquier orden)
cd ms-auth && ./mvnw spring-boot:run
cd ms-clientes && ./mvnw spring-boot:run
cd ms-reservas && ./mvnw spring-boot:run
cd ms-pagos && ./mvnw spring-boot:run
```

Bases de datos necesarias (MySQL)

```sql
CREATE DATABASE db_auth;
CREATE DATABASE db_clientes;
CREATE DATABASE db_reservas;
CREATE DATABASE db_pagos;
CREATE DATABASE db_hoteles;
CREATE DATABASE db_habitaciones;
```

📋 Tecnologías Utilizadas

| Tecnología | Uso |
|---|---|
| Spring Boot 3.x | Framework principal |
| Spring Cloud Netflix Eureka | Descubrimiento de servicios |
| Spring Cloud Gateway | API Gateway |
| Spring Security + JWT | Autenticación y autorización |
| Spring Data JPA | Persistencia de datos |
| Flyway | Migraciones de base de datos |
| MySQL 8.0 | Base de datos |
| Lombok | Reducción de boilerplate |
| Springdoc OpenAPI (Swagger) | Documentación de APIs |
| WebClient | Comunicación entre microservicios |

📖 Documentación Swagger

Una vez levantados los microservicios, la documentación está disponible en:

| Microservicio | URL Swagger |
|---|---|
| ms-auth | http://localhost:8081/doc/swagger-ui/index.html |
| ms-clientes | http://localhost:8084/doc/swagger-ui/index.html |
| ms-reservas | http://localhost:8082/doc/swagger-ui/index.html |
| ms-pagos | http://localhost:8083/doc/swagger-ui/index.html |

🗄️ Estructura del Repositorio

```
hotel-reservas-microservices/
├── eureka-server/
├── api-gateway/
├── ms-auth/
├── ms-clientes/
├── ms-reservas/
├── ms-pagos/
├── ms-hoteles/
├── ms-habitaciones/
├── ms-servicios/
├── ms-notificaciones/
├── ms-reportes/
├── ms-housekeeping/
└── README.md
```

🔄 Flujo de una Reserva Completa

Login → ms-auth (obtener JWT)
Buscar hab. → ms-habitaciones (verificar disponibilidad)
Reservar → ms-reservas (crear reserva)
Pagar → ms-pagos (procesar pago)
Notificar → ms-notificaciones (confirmar por email)

---

📝 Convención de Commits

Este proyecto usa [Conventional Commits](https://www.conventionalcommits.org/):

| Prefijo | Uso |
|---------|-----|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de errores |
| `docs` | Documentación |
| `refactor` | Refactorización |
| `chore` | Mantenimiento |

---

*Proyecto desarrollado para el curso Desarrollo Fullstack I (DSY1103) — DuocUC*
