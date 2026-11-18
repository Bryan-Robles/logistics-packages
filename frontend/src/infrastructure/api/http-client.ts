import axios, { AxiosError } from "axios";

const TOKEN_KEY = import.meta.env.VITE_TOKEN_KEY || "auth_token";

function getToken(): string | null {
  if (typeof window !== "undefined" && window.localStorage) {
    return localStorage.getItem(TOKEN_KEY);
  }
  return null;
}

function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: parseInt(import.meta.env.VITE_API_TIMEOUT || "15000", 10),
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      removeToken();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

export interface ApiErrorResponse {
  timestamp?: string;
  status?: number;
  codigo?: string;
  mensaje?: string;
  errores?: Array<{ campo: string; mensaje: string }>;
}

export function parseApiError(error: unknown): ApiErrorResponse {
  if (axios.isAxiosError(error) && error.response?.data) {
    return error.response.data as ApiErrorResponse;
  }
  return {
    codigo: "ERROR_INESPERADO",
    mensaje: error instanceof Error ? error.message : "Error inesperado",
  };
}

/** Error thrown by the HTTP services, carrying the backend error code. */
export class ApiError extends Error {
  constructor(
    message: string,
    readonly codigo?: string,
    readonly status?: number,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

/** Builds an ApiError from any failed request, using `fallback` when the backend sends no message. */
export function toApiError(error: unknown, fallback: string): ApiError {
  const parsed = parseApiError(error);
  return new ApiError(parsed.mensaje || fallback, parsed.codigo, parsed.status);
}

/** Message and code from any caught value, for UI catch blocks. */
export function errorInfo(error: unknown): { message: string; codigo?: string } {
  if (error instanceof ApiError) return { message: error.message, codigo: error.codigo };
  if (error instanceof Error) return { message: error.message };
  return { message: String(error) };
}

export { getToken, removeToken, TOKEN_KEY };
export default api;
