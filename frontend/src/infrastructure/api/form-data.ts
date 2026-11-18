/**
 * Builds multipart/form-data for Spring endpoints bound with @ModelAttribute.
 *
 * Nested objects are flattened with dot notation (remitente.tipoDocumento),
 * which is how Spring binds nested properties. null/undefined values are skipped
 * so optional fields stay unset on the backend. Files/Blobs are appended as-is.
 */
export function toFormData(data: object, evidencia?: File | Blob | null): FormData {
  const formData = new FormData();

  const append = (value: unknown, key: string) => {
    if (value === null || value === undefined) return;
    if (value instanceof Blob) {
      formData.append(key, value);
    } else if (value instanceof Date) {
      formData.append(key, value.toISOString());
    } else if (typeof value === "object") {
      for (const [childKey, childValue] of Object.entries(value)) {
        append(childValue, key ? `${key}.${childKey}` : childKey);
      }
    } else {
      formData.append(key, String(value));
    }
  };

  append(data, "");
  if (evidencia) formData.append("evidencia", evidencia);
  return formData;
}

/** Axios sets the multipart boundary itself when this header is used with FormData. */
export const MULTIPART_HEADERS = { "Content-Type": "multipart/form-data" } as const;
