import type { Control, FieldErrors, UseFormSetValue } from "react-hook-form";
import type {
  AdmissionFormData,
  AdmissionFormInput,
} from "@/domain/validators/admission.validator";

// Typed react-hook-form handles shared by the admission form sections.
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- matches useForm's default context type
export type AdmissionControl = Control<AdmissionFormInput, any, AdmissionFormData>;
export type AdmissionErrors = FieldErrors<AdmissionFormInput>;
export type AdmissionSetValue = UseFormSetValue<AdmissionFormInput>;
