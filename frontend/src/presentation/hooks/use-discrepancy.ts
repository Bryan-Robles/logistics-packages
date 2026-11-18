import { useCallback, useState } from "react";
import {
  DiscrepancyApiService,
  UpdatePhysicalDataRequestDTO,
} from "@/infrastructure/http/discrepancy-api.service";
import { toast } from "sonner";
import { errorInfo } from "@/infrastructure/api/http-client";

export interface DiscrepancyFormData {
  pesoCorregido: number;
  largoCorregido: number;
  anchoCorregido: number;
  altoCorregido: number;
}

export interface DiscrepancyDifferences {
  pesoDiff: number | null;
  largoDiff: number | null;
  anchoDiff: number | null;
  altoDiff: number | null;
}

export function useDiscrepancy() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState<DiscrepancyFormData>({
    pesoCorregido: 0,
    largoCorregido: 0,
    anchoCorregido: 0,
    altoCorregido: 0,
  });

  const updateField = <K extends keyof DiscrepancyFormData>(
    key: K,
    value: DiscrepancyFormData[K],
  ) => {
    setFormData((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  // Memoizada con formData: cambia solo cuando cambian los valores corregidos
  const calculateDifferences = useCallback(
    (
      pesoOriginal: number | undefined,
      largoOriginal: number | undefined,
      anchoOriginal: number | undefined,
      altoOriginal: number | undefined,
    ): DiscrepancyDifferences => ({
      pesoDiff:
        pesoOriginal !== undefined && formData.pesoCorregido > 0
          ? formData.pesoCorregido - pesoOriginal
          : null,
      largoDiff:
        largoOriginal !== undefined && formData.largoCorregido > 0
          ? formData.largoCorregido - largoOriginal
          : null,
      anchoDiff:
        anchoOriginal !== undefined && formData.anchoCorregido > 0
          ? formData.anchoCorregido - anchoOriginal
          : null,
      altoDiff:
        altoOriginal !== undefined && formData.altoCorregido > 0
          ? formData.altoCorregido - altoOriginal
          : null,
    }),
    [formData],
  );

  const validateForm = (): boolean => {
    if (formData.pesoCorregido <= 0) {
      toast.error("❌ Peso debe ser mayor a 0");
      return false;
    }
    if (
      formData.largoCorregido <= 0 ||
      formData.anchoCorregido <= 0 ||
      formData.altoCorregido <= 0
    ) {
      toast.error("❌ Todas las dimensiones deben ser mayores a 0");
      return false;
    }
    return true;
  };

  const submitDiscrepancy = async (paqueteId: string): Promise<boolean> => {
    if (!validateForm()) {
      return false;
    }

    setIsSubmitting(true);
    try {
      const request: UpdatePhysicalDataRequestDTO = {
        pesoKg: formData.pesoCorregido,
        largoCm: formData.largoCorregido,
        anchoCm: formData.anchoCorregido,
        altoCm: formData.altoCorregido,
      };

      // Solo actualiza datos físicos, sin asignar zona
      await DiscrepancyApiService.updatePhysicalData(paqueteId, request);

      toast.success(`✅ Datos físicos actualizados correctamente`);
      return true;
    } catch (caught) {
      const error = errorInfo(caught);
      const errorMsg = error.message || "Error desconocido al procesar discrepancia";
      toast.error(`❌ ${errorMsg}`);
      return false;
    } finally {
      setIsSubmitting(false);
    }
  };

  return {
    formData,
    isSubmitting,
    updateField,
    calculateDifferences,
    submitDiscrepancy,
  };
}
