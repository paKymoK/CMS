import { refreshAccessToken } from "../auth/pkce";
import { notifyTokenRefreshed } from "./tokenSync";

let refreshPromise: Promise<string | null> | null = null;

export async function refreshTokenIfPossible(): Promise<string | null> {
  const refreshToken = sessionStorage.getItem("refresh_token");
  if (!refreshToken) return null;

  if (!refreshPromise) {
    refreshPromise = refreshAccessToken(refreshToken)
      .then((tokenResponse) => {
        sessionStorage.setItem("access_token", tokenResponse.access_token);
        if (tokenResponse.refresh_token) {
          sessionStorage.setItem("refresh_token", tokenResponse.refresh_token);
        }
        notifyTokenRefreshed(tokenResponse);
        return tokenResponse.access_token;
      })
      .catch(() => null)
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
}
