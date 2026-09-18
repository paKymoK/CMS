import type { TokenResponse } from "../auth/pkce";

type TokenSyncCallback = (tokenResponse: TokenResponse) => void;
type LogoutCallback = () => void;

let _onTokenRefreshed: TokenSyncCallback | null = null;
let _onLogout: LogoutCallback | null = null;

export function registerTokenSync(fn: TokenSyncCallback) {
  _onTokenRefreshed = fn;
}

export function notifyTokenRefreshed(tokenResponse: TokenResponse) {
  _onTokenRefreshed?.(tokenResponse);
}

export function registerLogout(fn: LogoutCallback) {
  _onLogout = fn;
}

export function notifyLogout() {
  _onLogout?.();
}
