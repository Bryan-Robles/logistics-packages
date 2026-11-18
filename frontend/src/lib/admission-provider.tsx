import { useState, type ReactNode } from "react";
import { AdmissionContext } from "./admission-context";

export function AdmissionProvider({ children }: { children: ReactNode }) {
  const [paqueteId, setPaqueteId] = useState<string | null>(null);
  const [etiquetaDigital, setEtiquetaDigital] = useState<string | null>(null);
  const [estadoGps, setEstadoGps] = useState<"PENDIENTE" | "RESUELTO" | null>(null);
  const [estado, setEstado] = useState<string | null>(null);
  const [sedeId, setSedeId] = useState<string | null>(null);
  const [sedeNombre, setSedeNombre] = useState<string | null>(null);
  const [zonaId, setZonaId] = useState<string | null>(null);
  const [remitenteNombre, setRemitenteNombre] = useState<string | null>(null);
  const [destinatarioNombre, setDestinatarioNombre] = useState<string | null>(null);
  const [direccionDestinoTexto, setDireccionDestinoTexto] = useState<string | null>(null);
  const [distanciaKm, setDistanciaKm] = useState<number | null>(null);
  const [tarifaBase, setTarifaBase] = useState<number | null>(null);
  const [tarifaPorKg, setTarifaPorKg] = useState<number | null>(null);
  const [tarifaPorKm, setTarifaPorKm] = useState<number | null>(null);

  const clearAdmission = () => {
    setPaqueteId(null);
    setEtiquetaDigital(null);
    setEstadoGps(null);
    setEstado(null);
    setSedeId(null);
    setSedeNombre(null);
    setZonaId(null);
    setRemitenteNombre(null);
    setDestinatarioNombre(null);
    setDireccionDestinoTexto(null);
    setDistanciaKm(null);
    setTarifaBase(null);
    setTarifaPorKg(null);
    setTarifaPorKm(null);
  };

  return (
    <AdmissionContext.Provider
      value={{
        paqueteId,
        etiquetaDigital,
        estadoGps,
        estado,
        sedeId,
        sedeNombre,
        zonaId,
        remitenteNombre,
        destinatarioNombre,
        direccionDestinoTexto,
        distanciaKm,
        tarifaBase,
        tarifaPorKg,
        tarifaPorKm,
        setPaqueteId,
        setEtiquetaDigital,
        setEstadoGps,
        setEstado,
        setSedeId,
        setSedeNombre,
        setZonaId,
        setRemitenteNombre,
        setDestinatarioNombre,
        setDireccionDestinoTexto,
        setDistanciaKm,
        setTarifaBase,
        setTarifaPorKg,
        setTarifaPorKm,
        clearAdmission,
      }}
    >
      {children}
    </AdmissionContext.Provider>
  );
}
