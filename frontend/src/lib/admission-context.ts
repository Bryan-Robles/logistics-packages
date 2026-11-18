import { createContext, useContext } from "react";

/**
 * Contexto global para el flujo de admisión de paquetes (FE-1)
 *
 * Persiste el estado entre AdmissionPage → WeighingPage:
 * - paqueteId: ID generado por el backend tras POST /api/paquetes/admision
 * - etiquetaDigital: Etiqueta única del paquete (ej: PK-xxxxx)
 * - estadoGps: PENDIENTE o RESUELTO (para mostrar/ocultar panel de coordenadas manuales)
 * - estado: Estado actual del paquete (ej: RECIBIDO_EN_SEDE)
 * - sedeId: ID de la sede donde se registra el paquete
 */

interface AdmissionContextType {
  paqueteId: string | null;
  etiquetaDigital: string | null;
  estadoGps: "PENDIENTE" | "RESUELTO" | null;
  estado: string | null;
  sedeId: string | null;
  sedeNombre: string | null;
  zonaId: string | null;
  remitenteNombre: string | null;
  destinatarioNombre: string | null;
  direccionDestinoTexto: string | null;
  distanciaKm: number | null;
  tarifaBase: number | null;
  tarifaPorKg: number | null;
  tarifaPorKm: number | null;
  setPaqueteId: (id: string) => void;
  setEtiquetaDigital: (etiqueta: string) => void;
  setEstadoGps: (estado: "PENDIENTE" | "RESUELTO") => void;
  setEstado: (estado: string) => void;
  setSedeId: (id: string) => void;
  setSedeNombre: (nombre: string) => void;
  setZonaId: (id: string) => void;
  setRemitenteNombre: (nombre: string) => void;
  setDestinatarioNombre: (nombre: string) => void;
  setDireccionDestinoTexto: (direccion: string) => void;
  setDistanciaKm: (distancia: number) => void;
  setTarifaBase: (tarifa: number | null) => void;
  setTarifaPorKg: (tarifa: number | null) => void;
  setTarifaPorKm: (tarifa: number | null) => void;
  clearAdmission: () => void;
}

export const AdmissionContext = createContext<AdmissionContextType | undefined>(undefined);

export function useAdmission() {
  const context = useContext(AdmissionContext);
  if (!context) {
    throw new Error("useAdmission debe ser usado dentro de AdmissionProvider");
  }
  return context;
}
