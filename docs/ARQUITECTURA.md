# Arquitectura

## Contexto del sistema

El sistema logístico tiene tres módulos que se comunican por colas AWS SQS. Este repositorio implementa el **Módulo 1 (Gestión de Paquetes)**.

```mermaid
flowchart LR
    op([Operario de bodega]) --> fe[Frontend<br/>React + TanStack]
    fe -->|REST + JWT| be[Backend M1<br/>Spring Boot]
    be --> db[(PostgreSQL)]
    be -->|evidencias| s3[(S3)]
    be -->|geocodificación| gm[Google Maps API]

    be -- solicitudes-ruta-queue --> m2[Módulo 2<br/>Rutas y Flota]
    m2 -- respuestas-ruta-queue --> be
    m2 -- logistics-eventos-paquete --> be
    be -- eventos-financieros-paquete-queue --> m3[Módulo 3<br/>Facturación]
```

En local, S3 y SQS se emulan con LocalStack. Los módulos 2 y 3 no forman parte de este repositorio.

## Arquitectura hexagonal (backend)

```mermaid
flowchart TB
    subgraph infra[infrastructure]
        rest[Controladores REST]
        sqsIn[Listeners SQS]
        jpa[Adaptadores JPA]
        sqsOut[Publicadores SQS]
        s3a[Adaptador S3]
        maps[Adaptador Google Maps]
    end
    subgraph app[application]
        uc[Casos de uso]
        ports[Puertos]
    end
    subgraph domain[domain]
        model[Paquete, Sede, Zonas<br/>value objects, reglas de estado]
    end

    rest --> uc
    sqsIn --> uc
    uc --> model
    uc --> ports
    jpa -.implementa.-> ports
    sqsOut -.implementa.-> ports
    s3a -.implementa.-> ports
    maps -.implementa.-> ports
```

- **domain**: el modelo y sus reglas (transiciones de estado, validaciones de peso y dimensiones, evidencia obligatoria). No depende de Spring.
- **application**: un caso de uso por operación (admisión, pesaje, almacenaje, clasificación, novedades, eventos de ruta) y los puertos que necesita.
- **infrastructure**: adaptadores de entrada (REST, SQS) y de salida (JPA, SQS, S3, Google Maps, SMTP).

## Ciclo de vida del paquete

```mermaid
stateDiagram-v2
    [*] --> RECIBIDO_EN_SEDE: admisión (+ pesaje)
    RECIBIDO_EN_SEDE --> EN_CLASIFICACION: asignar zona de almacenaje
    EN_CLASIFICACION --> LISTO_PARA_DESPACHO: confirmar zona de destino
    RECIBIDO_EN_SEDE --> NOVEDAD_EN_BODEGA: novedad (dañado / extraviado)
    EN_CLASIFICACION --> NOVEDAD_EN_BODEGA: novedad (dañado / extraviado)

    LISTO_PARA_DESPACHO --> EN_TRANSITO: evento M2
    EN_TRANSITO --> EN_PARADA_DE_ENTREGA: evento M2
    EN_PARADA_DE_ENTREGA --> ENTREGADO: evento M2
    EN_TRANSITO --> DEVOLUCION_EN_RUTA
    EN_PARADA_DE_ENTREGA --> DEVOLUCION_EN_RUTA
    EN_TRANSITO --> DAÑADO_EN_RUTA
    EN_TRANSITO --> EXTRAVIADO_EN_RUTA
    ENTREGADO --> [*]
```

Cada transición queda registrada en `historial_estados`, con el usuario, las observaciones y la URL de evidencia si la hay. Los estados finales se publican al Módulo 3.

## Flujo de admisión y geocodificación

```mermaid
sequenceDiagram
    actor Op as Operario
    participant FE as Frontend
    participant BE as Backend
    participant GM as Google Maps
    Op->>FE: Datos de remitente, destinatario y dirección
    FE->>BE: POST /api/paquetes/admision (multipart)
    BE->>GM: Geocodificar dirección
    alt Coordenadas encontradas y dentro de cobertura
        BE-->>FE: estadoGps RESUELTO
    else Geocodificación falla
        BE-->>FE: estadoGps PENDIENTE
        Op->>FE: Coordenadas manuales
        FE->>BE: PATCH /api/paquetes/{id}/coordenadas
        BE-->>FE: estadoGps RESUELTO + distancia recalculada
    end
    FE->>BE: POST /api/paquetes/pesaje (multipart)
    BE-->>FE: Precio, categoría de carga y alertas
    BE--)BE: Solicitud de ruta a M2 (solo con GPS resuelto)
```
