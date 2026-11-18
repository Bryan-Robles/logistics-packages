## Qué cambia

<!-- Resumen corto del cambio -->

## Por qué

<!-- Problema que resuelve o motivación. Enlaza el issue si existe: Closes #123 -->

## Cómo se probó

<!-- Tests agregados o ejecutados, pasos manuales, capturas si hay cambios visuales -->

## Checklist

- [ ] La rama sigue la convención `<tipo>/<descripcion>` y los commits siguen Conventional Commits
- [ ] `./gradlew test` pasa (si toca el backend)
- [ ] `npx tsc --noEmit`, `npm run lint` y `npm run build` pasan (si toca el frontend)
- [ ] Agregué o actualicé los tests del cambio
- [ ] Actualicé la documentación y los `.env.example` si hacía falta
- [ ] No incluye secretos (API keys, contraseñas, `.env`)
