# Guía de contribución

Gracias por tu interés en el proyecto. Esta guía describe cómo proponer cambios para mantener el repositorio estable y el historial legible.

> El proyecto está archivado como entrega académica (ver el [README](README.md)). Los cambios bienvenidos son correcciones, mejoras de documentación, tests y refactors que no cambien el alcance funcional.

## Flujo de trabajo

`main` está protegida: **no se hace push directo**. Todo cambio entra por Pull Request, y la integración continua debe pasar antes del merge.

1. Crea una rama desde `main` actualizado:

   ```bash
   git switch main
   git pull
   git switch -c fix/distancia-coordenadas-manuales
   ```

2. Haz commits pequeños y enfocados (ver [Commits](#commits)).
3. Corre las verificaciones locales (ver [Antes de abrir el PR](#antes-de-abrir-el-pr)).
4. Sube la rama y abre un Pull Request hacia `main`, completando la plantilla.
5. Cuando la CI esté en verde, haz merge con **Squash and merge** y borra la rama.

### Nombres de rama

`<tipo>/<descripcion-corta-en-kebab-case>`

| Tipo | Uso |
| --- | --- |
| `feat/` | Funcionalidad nueva |
| `fix/` | Corrección de un bug |
| `refactor/` | Cambio interno sin cambiar el comportamiento |
| `test/` | Tests nuevos o corregidos |
| `docs/` | Solo documentación |
| `ci/` | Integración continua |
| `chore/` | Dependencias, configuración, mantenimiento |

Ejemplos: `fix/busqueda-gestion-por-tecla`, `docs/diagrama-secuencia-pesaje`, `chore/actualizar-spring-boot`.

## Commits

Se usa [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/):

```
<tipo>(<ámbito opcional>): <resumen en imperativo>

<cuerpo opcional: qué cambia y por qué>
```

- Tipos: `feat`, `fix`, `refactor`, `test`, `docs`, `ci`, `chore`, `style`, `perf`.
- Ámbitos habituales: `backend`, `frontend`, `docker`, `docs`.
- El resumen va en minúscula, sin punto final y con 72 caracteres como máximo.

```
fix(backend): recalcular la distancia al ingresar coordenadas manuales
feat(frontend): mostrar la categoría de carga en el tracking
```

## Antes de abrir el PR

```bash
# Backend (requiere Docker para los tests de integración)
cd backend && ./gradlew test

# Frontend
cd frontend && npx tsc --noEmit && npm run lint && npm run build

# App completa
docker compose up --build
```

La CI corre exactamente estas verificaciones. El lint del frontend no admite warnings.

## Estándares de código

### Backend

- Respeta la **arquitectura hexagonal**:
  - `domain` no depende de Spring ni de la infraestructura;
  - la lógica de negocio va en casos de uso de `application`;
  - los controladores solo traducen HTTP ↔ caso de uso.
- Todo bug corregido o regla nueva lleva un test: unitario con Mockito para los casos de uso, o de integración con Testcontainers si toca la base de datos, SQS o S3.
- Los cambios de esquema van en una **nueva** migración Flyway (`V<n>__descripcion.sql`). Nunca se editan las migraciones existentes.

### Frontend

- TypeScript estricto: sin `any` (la regla de ESLint lo marca como error). Para errores de la API usa `ApiError` y `errorInfo()`.
- Las llamadas HTTP van en `src/infrastructure/http`. Los componentes no llaman a `axios` directamente.
- Formato con Prettier: `npm run format`.
- No edites `src/components/ui/` a mano: son componentes generados por shadcn/ui.

### Seguridad

- Nunca subas secretos: `.env`, API keys, contraseñas, tokens. Usa variables de entorno y documenta las nuevas en los `.env.example`.
- Si subiste un secreto por error, avisa en el PR: hay que revocarlo, porque borrarlo del código no basta.

## Pull Requests

- **Un PR por tema.** Si el cambio crece, divídelo.
- Describe qué cambia, por qué y cómo se probó. Para cambios visuales, agrega capturas.
- Enlaza el issue relacionado (`Closes #12`) si existe.

## Versionado

Se usa [SemVer](https://semver.org/lang/es/) con tags anotados en `main` (`v1.0.1` para correcciones, `v1.1.0` para funcionalidades compatibles).

## Reportar problemas

Abre un issue con la plantilla correspondiente (bug o mejora), con pasos para reproducir y el comportamiento esperado.
