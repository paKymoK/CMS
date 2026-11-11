import { refreshAccessToken, RefreshRejectedError, RefreshUnavailableError } from "../auth/pkce";
import { tokenStore } from "../auth/tokenStore";
import { notifyTokenRefreshed } from "./tokenSync";

export { RefreshUnavailableError };

// A token with less than this left is renewed rather than used.
const MIN_LIFETIME_SECONDS = 30;
// Waits before the 2nd and 3rd attempt when the server cannot be reached (Wi-Fi coming back).
const RETRY_DELAYS_MS = [1000, 3000];

let inFlight: Promise<string | null> | null = null;

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

/** One refresh at a time across every tab of the browser: the refresh token is single-use, so two
 * tabs sending it together would kill the sign-in. Without Web Locks it falls back to per-tab. */
function withLock<T>(fn: () => Promise<T>): Promise<T> {
  const locks = typeof navigator !== "undefined" ? navigator.locks : undefined;
  return locks ? (locks.request("cms-token-refresh", fn) as Promise<T>) : fn();
}

function usable(token: string | null, stale: string | null | undefined): token is string {
  if (!token || token === stale) return false;
  const left = tokenStore.secondsLeft(token);
  return left === null || left > MIN_LIFETIME_SECONDS;
}

async function renew(stale: string | null | undefined): Promise<string | null> {
  // Another tab may have renewed while this one waited for the lock.
  const current = tokenStore.getAccess();
  if (usable(current, stale)) return current;

  let refreshToken = tokenStore.getRefresh();
  for (let attempt = 0; refreshToken; attempt++) {
    try {
      const tokens = await refreshAccessToken(refreshToken);
      tokenStore.save(tokens);
      notifyTokenRefreshed(tokens);
      return tokens.access_token;
    } catch (err) {
      if (err instanceof RefreshRejectedError) {
        // Refused: unless another tab signed in again meanwhile, the sign-in is over.
        const latest = tokenStore.getRefresh();
        if (latest && latest !== refreshToken) {
          const access = tokenStore.getAccess();
          if (usable(access, stale)) return access;
          refreshToken = latest;
          continue;
        }
        return null;
      }
      if (attempt >= RETRY_DELAYS_MS.length) throw err;
      await sleep(RETRY_DELAYS_MS[attempt]);
      refreshToken = tokenStore.getRefresh() ?? refreshToken;
    }
  }
  return null;
}

/**
 * Returns a usable access token, renewing it if needed. `stale` is the token the caller found
 * unusable (expired, or answered with 401). Resolves null when the sign-in is over (no refresh
 * token, or it was refused: the caller should log out); rejects with RefreshUnavailableError when
 * the sign-in server could not be reached (the sign-in may still be good: keep it, try again).
 */
export function refreshTokenIfPossible(stale?: string | null): Promise<string | null> {
  if (!inFlight) {
    inFlight = withLock(() => renew(stale)).finally(() => {
      inFlight = null;
    });
  }
  return inFlight;
}

/** For the moments a tab wakes up: renew now if the token has run out or is about to, so the first
 * click does not wait for a 401 round trip. Resolves false only when the sign-in is over. */
export async function refreshIfExpiring(): Promise<boolean> {
  const token = tokenStore.getAccess();
  if (!token) return true;
  const left = tokenStore.secondsLeft(token);
  if (left === null || left > 60) return true;
  try {
    return (await refreshTokenIfPossible(token)) !== null;
  } catch {
    return true; // could not reach the server: not a reason to sign out
  }
}
