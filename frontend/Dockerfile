# ── Build stage ────────────────────────────────────────────────
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY . .
# Vite inlines VITE_* at build time; the browser reaches the API on the host
ARG VITE_API_BASE_URL=http://localhost:8080
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
# Node server build (default build targets Cloudflare Workers)
ENV BUILD_TARGET=node
RUN npm run build

# ── Runtime stage ──────────────────────────────────────────────
FROM node:22-alpine
WORKDIR /app
COPY --from=build /app /app
EXPOSE 3000
CMD ["npx", "vite", "preview", "--host", "0.0.0.0", "--port", "3000", "--strictPort"]
