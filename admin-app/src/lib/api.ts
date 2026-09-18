import axios, { type AxiosInstance } from "axios";
import { message } from "antd";
import { notifyLogout } from "./tokenSync";
import { refreshTokenIfPossible } from "./refreshSingleton";

let isLoggingOut = false;

function toPlainHeaders(headers: unknown): Record<string, string> {
  if (headers && typeof headers === "object" && !Array.isArray(headers)) {
    return { ...(headers as Record<string, string>) };
  }
  return {};
}

interface RetriableConfig {
  _retry?: boolean;
  headers?: unknown;
}

function isRetriableConfig(config: unknown): config is RetriableConfig {
  return !!config && typeof config === "object";
}

/** Every admin-app API call goes through gateway-service, not straight to the owning service.
 * VITE_BASE_URL is just the gateway's own origin — each backend service is reached by prefixing
 * its gateway route id (e.g. "content-service"), which the gateway strips before forwarding via
 * lb:// discovery. Add a new `createApi("<route-id>")` call here as new services come online. */
const GATEWAY_BASE_URL = import.meta.env.VITE_BASE_URL as string;

export function serviceUrl(servicePath: string): string {
  return `${GATEWAY_BASE_URL}/${servicePath}`;
}

function createApi(servicePath: string): AxiosInstance {
  const api = axios.create({ baseURL: serviceUrl(servicePath) });

  api.interceptors.request.use((config) => {
    const token = sessionStorage.getItem("access_token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });

  api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const status = error.response?.status;

      if (status === 401 && isRetriableConfig(error.config) && !error.config._retry) {
        error.config._retry = true;
        const newAccessToken = await refreshTokenIfPossible();
        if (newAccessToken) {
          isLoggingOut = false;
          const headers = toPlainHeaders(error.config.headers);
          headers.Authorization = `Bearer ${newAccessToken}`;
          return api({ ...error.config, headers });
        }
        if (!isLoggingOut) {
          isLoggingOut = true;
          notifyLogout();
          setTimeout(() => {
            isLoggingOut = false;
          }, 3000);
        }
        return Promise.reject(error);
      }

      if (status >= 400 && status < 500) {
        const msg: string | undefined = error.response?.data?.status?.message;
        message.error(msg || "Something went wrong");
      }

      return Promise.reject(error);
    },
  );

  return api;
}

export const contentApi = createApi("content-service");
export const mediaApi = createApi("media-service");
