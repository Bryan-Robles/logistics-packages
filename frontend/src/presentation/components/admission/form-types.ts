import type { Control, FieldErrors, FormState, UseFormSetValue } from "react-hook-form";
import type {
  AdmissionFormData,
  AdmissionFormInput,
} from "@/domain/validators/admission.validator";

// Typed react-hook-form handles shared by the admission form sections.
export type AdmissionControl = Control<AdmissionFormInput, unknown, AdmissionFormData>;
export type AdmissionErrors = FieldErrors<AdmissionFormInput>;
export type AdmissionTouched = FormState<AdmissionFormInput>["touchedFields"];
export type AdmissionSetValue = UseFormSetValue<AdmissionFormInput>;
