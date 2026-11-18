// @lovable.dev/vite-tanstack-config already includes the following — do NOT add them manually
// or the app will break with duplicate plugins:
//   - tanstackStart, viteReact, tailwindcss, tsConfigPaths, cloudflare (build-only),
//     componentTagger (dev-only), VITE_* env injection, @ path alias, React/TanStack dedupe,
//     error logger plugins, and sandbox detection (port/host/strictPort).
// You can pass additional config via defineConfig({ vite: { ... } }) if needed.
import { defineConfig } from "@lovable.dev/vite-tanstack-config";

export default defineConfig({
  vite: {
    server: {
      port: 3000,
    },
  },
  // Default build targets Cloudflare Workers. BUILD_TARGET=node produces a Node server
  // (dist/server/server.js) that `vite preview` can serve, used by the Docker image.
  ...(process.env.BUILD_TARGET === "node" ? { cloudflare: false as const } : {}),
});
