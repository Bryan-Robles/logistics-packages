# Logistics Packages — Módulo 1: Gestión de Paquetes

Sistema de gestión logística para la trazabilidad de paquetes desde su ingreso a sede: admisión, pesaje, clasificación por zona, almacenaje, solicitud de ruta y gestión de novedades.

Proyecto académico desarrollado en equipo (Universidad del Magdalena, semestre 2026-1). Forma parte de un sistema de tres módulos (Paquetes, Rutas y Facturación) que se comunican por AWS SQS. Este repositorio contiene **solo el Módulo 1**.

> **Estado: archivado.** El desarrollo terminó al cierre del semestre. Este repo es una copia consolidada y estabilizada del trabajo entregado, sin funcionalidades nuevas. Ver [Alcance y limitaciones](#alcance-y-limitaciones).

## Stack

| Capa | Tecnologías |
| --- | --- |
| Backend | Java 21, Spring Boot 3.2, Spring Security + JWT, Spring Data JPA, Flyway, PostgreSQL 16 |
| Mensajería / storage | AWS SQS y S3 (Spring Cloud AWS), LocalStack en local |
| Frontend | React 19, TypeScript, TanStack Start/Router/Query, Tailwind CSS 4, shadcn/ui, React Hook Form + Zod |
| Infra local | Docker Compose |

El backend usa **arquitectura hexagonal** (`domain` / `application` / `infrastructure`) y el frontend una separación por capas equivalente (`domain` / `application` / `infrastructure` / `presentation`).

## Estructura

```
backend/    API REST (Spring Boot)
frontend/   Aplicación web (React + TanStack Start)
docs/       Requisitos, casos de uso, specs, planes de ejecución y prototipos
```

## Funcionalidades implementadas

| Caso de uso | Backend | Frontend |
| --- | --- | --- |
| UC-001 Registrar admisión de paquete (geocodificación de dirección) | `POST /api/paquetes/admision` | `/admision` |
| UC-002 Procesar pesaje y dimensiones | `POST /api/paquetes/pesaje` | `/pesaje` |
| UC-003 Solicitar ruta (evento SQS a Módulo 2) | vía SQS | desde pesaje |
| UC-004 Preparar paquete para almacenaje | `/api/paquetes/{id}/almacenaje` | `/gestion` |
| UC-005 Clasificar paquete por zona destino | `/api/paquetes/clasificacion/*` | `/clasificacion` |
| UC-006 Actualizar estado de disponibilidad (eventos de ruta) | listener SQS | `/tracking` |
| UC-007 Gestionar novedades (dañado, extraviado, devolución) | `/api/paquetes/{id}/novedades` | `/novedades`, `/reportar-novedad` |
| Autenticación | `POST /api/auth/login`, `/register` | `/login`, `/register` |

Documentación interactiva de la API: `http://localhost:8080/swagger-ui.html`.

## Ejecutar en local

Requisitos: Docker, Node.js 20+ (y Java 21 si se corre el backend fuera de Docker).

**1. Backend + PostgreSQL + LocalStack**

```bash
cd backend
cp .env.example .env
docker compose up --build
```

La API queda en `http://localhost:8080`. Flyway crea el esquema y carga datos semilla.

**2. Frontend**

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

La aplicación queda en `http://localhost:3000`.

Usuarios de desarrollo creados al arrancar el backend: `admin` / `admin123` y `operador` / `operador123`. Son solo para uso local.

## Calidad

```bash
cd backend && ./gradlew test        # 205 tests (unitarios + integración con Testcontainers; requiere Docker)
cd frontend && npx tsc --noEmit     # 0 errores de tipos
cd frontend && npm run lint         # 0 errores (quedan warnings de tipado `any`)
```

Flujo verificado de punta a punta contra el stack local: login → admisión → coordenadas manuales → pesaje → almacenaje → clasificación → tracking, y reporte de novedad con evidencia.

## Alcance y limitaciones

Lo que **no** quedó terminado al cierre del proyecto:

- **Integración con otros módulos:** la integración real con los Módulos 2 (Rutas) y 3 (Facturación) dependía de cuentas AWS de otros equipos. En local se usa LocalStack: M1 publica sus eventos, pero los de esos módulos no llegan, así que no se asignan rutas.
- **Geocodificación:** requiere una API key de Google Maps (`GOOGLE_MAPS_API_KEY`). Sin ella, el paquete queda con GPS `PENDIENTE` y las coordenadas se ingresan manualmente.
- **Distancia con coordenadas manuales:** la distancia estimada no se recalcula al ingresar coordenadas manuales, así que el precio de esos paquetes usa distancia 0.
- **URLs de evidencia:** las URLs devueltas apuntan al formato de AWS S3 real, no a LocalStack.
- **Notificaciones:** los SMS están simulados (se registran en el log) y el email requiere credenciales SMTP.
- **Frontend:** quedan pantallas con datos de ejemplo y algunos tipos `any` sin precisar.

## Equipo

- Carlos Sehuanes
- Bryan Robles
- Karyme Sánchez
- Juan Rivera

Repositorios originales:
- Backend: [InsoftUnimag/20261LogisticsPackages](https://github.com/InsoftUnimag/20261LogisticsPackages)
- Frontend: [Csehuanes-code/20261LogisticsPackagesFrontend](https://github.com/Csehuanes-code/20261LogisticsPackagesFrontend)

Este repositorio conserva el historial completo de commits de ambos. Se eliminaron del historial artefactos de build y credenciales que se habían subido por error.
