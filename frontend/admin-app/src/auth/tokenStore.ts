import type { TokenResponse } from "./pkce";

// The sign-in lives in localStorage so every tab of this browser shares it and it survives closing
// the browser. Only this file touches these keys. If the browser blocks storage the tokens are kept
// in memory instead (that tab still works, other tabs and restarts do not share it).
const ACCESS = "access_token";
const REFRESH = "refresh_token";
const ID = "id_token";

const memory = new Map<string, string>();

function storageWorks(): boolean {
  try {
    localStorage.setItem("cms_probe", "1");
    localStorage.removeItem("cms_probe");
    return true;
  } catch {
    return false;
  }
}
const usable = storageWorks();

function read(key: string): string | null {
  if (!usable) return memory.get(key) ?? null;
  try {
    return localStorage.getItem(key);
  } catch {
    return null;
  }
}

function write(key: string, value: string) {
  if (!usable) {
    memory.set(key, value);
    return;
  }
  try {
    localStorage.setItem(key, value);
  } catch {
    // quota or a blocked write: nothing more to do, the next call will sign in again
  }
}

function remove(key: string) {
  memory.delete(key);
  if (!usable) return;
  try {
    localStorage.removeItem(key);
  } catch {
    // see write()
  }
}

// Tokens of the old per-tab sign-in: gone from now on, so those people sign in once more.
for (const key of [ACCESS, REFRESH, ID]) {
  try {
    sessionStorage.removeItem(key);
  } catch {
    // sessionStorage blocked: nothing to clean
  }
}

export const tokenStore = {
  getAccess: () => read(ACCESS),
  getRefresh: () => read(REFRESH),
  getId: () => read(ID),

  save(tokens: Pick<TokenResponse, "access_token" | "refresh_token" | "id_token">) {
    write(ACCESS, tokens.access_token);
    if (tokens.refresh_token) write(REFRESH, tokens.refresh_token);
    if (tokens.id_token) write(ID, tokens.id_token);
  },

  /** Ends the sign-in for every tab of this browser. The access token goes first: that removal is
   * what other tabs react to. */
  clear() {
    remove(ACCESS);
    remove(REFRESH);
    remove(ID);
  },

  /** Seconds the access token has left; null if it is not a readable JWT (treated as usable). */
  secondsLeft(token: string): number | null {
    try {
      const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
      return typeof payload.exp === "number" ? payload.exp - Date.now() / 1000 : null;
    } catch {
      return null;
    }
  },
};

export const TOKEN_KEY = ACCESS;
