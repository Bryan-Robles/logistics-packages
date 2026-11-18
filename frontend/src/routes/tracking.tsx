import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";
import { TrackingPage } from "@/presentation/pages/TrackingPage";

// Validar search params
const trackingSearchSchema = z.object({
  paqueteId: z.string().uuid().optional().catch(undefined),
});

export const Route = createFileRoute("/tracking")({
  component: TrackingPage,
  validateSearch: (search) => trackingSearchSchema.parse(search),
  // Authentication is enforced globally by AuthGuard in __root.tsx
});
