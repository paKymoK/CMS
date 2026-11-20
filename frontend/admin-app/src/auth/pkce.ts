import { sha256 } from "@noble/hashes/sha2.js";

export const AUTH_SERVER = import.meta.env.VITE_AUTH_SERVER as string;
const CLIENT_ID = import.meta.env.VITE_CLIENT_ID as string;
const REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI as string;
const SCOPES = import.meta.env.VITE_SCOPES as string;

export function generateCodeVerifier(): string {
  const array = new Uint8Array(64);
  crypto.getRandomValues(array);
  return base64UrlEncode(array);
}

export async function generateCodeChallenge(verifier: string): Promise<string> {
  const data = new TextEncoder().encode(verifier);
  // crypto.subtle exists only in secure contexts (HTTPS / localhost); over plain HTTP on a LAN
  // address it is undefined, so fall back to a JS implementation.
  if (globalThis.crypto?.subtle) {
    return base64UrlEncode(new Uint8Array(await crypto.subtle.digest("SHA-256", data)));
  }
  return base64UrlEncode(sha256(data));
}

export function generateState(): string {
  const array = new Uint8Array(32);
  crypto.getRandomValues(array);
  return base64UrlEncode(array);
}

export function buildAuthorizationUrl(codeChallenge: string, state: string): string {
  const params = new URLSearchParams({
    response_type: "code",
    client_id: CLIENT_ID,
    redirect_uri: REDIRECT_URI,
    scope: SCOPES,
    code_challenge: codeChallenge,
    code_challenge_method: "S256",
    state,
  });
  return `${AUTH_SERVER}/oauth2/authorize?${params.toString()}`;
}

export interface TokenResponse {
  access_token: string;
  refresh_token?: string;
  token_type: string;
  expires_in: number;
  id_token?: string;
  scope: string;
}

export async function exchangeCodeForToken(
  code: string,
  codeVerifier: string,
): Promise<TokenResponse> {
  const body = new URLSearchParams({
    grant_type: "authorization_code",
    client_id: CLIENT_ID,
    redirect_uri: REDIRECT_URI,
    code,
    code_verifier: codeVerifier,
  });

  const response = await fetch(`${AUTH_SERVER}/oauth2/token`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body,
  });
  const data = (await response.json()) as TokenResponse;
  if (!data.access_token) {
    throw new Error(`Token exchange failed: ${JSON.stringify(data)}`);
  }
  return data;
}

/** The server could not be asked (network down, 5xx, 429): the refresh token may still be good. */
export class RefreshUnavailableError extends Error {}
/** The server refused the refresh token (invalid_grant, expired, revoked): the sign-in is over. */
export class RefreshRejectedError extends Error {}

// cms-admin is a PUBLIC client (ClientAuthenticationMethod.NONE, PKCE-only, no secret) —
// unlike a confidential client, the refresh_token grant authenticates with client_id in the
// body alone, never a Basic auth header (there's no secret to put in one).
export async function refreshAccessToken(refreshToken: string): Promise<TokenResponse> {
  const body = new URLSearchParams({
    grant_type: "refresh_token",
    refresh_token: refreshToken,
    client_id: CLIENT_ID,
  });

  let response: Response;
  try {
    response = await fetch(`${AUTH_SERVER}/oauth2/token`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body,
    });
  } catch {
    throw new RefreshUnavailableError("Could not reach the sign-in server");
  }
  if (response.status >= 500 || response.status === 429) {
    throw new RefreshUnavailableError(`Sign-in server answered ${response.status}`);
  }
  let data: TokenResponse | undefined;
  try {
    data = (await response.json()) as TokenResponse;
  } catch {
    throw new RefreshUnavailableError("Unreadable answer from the sign-in server");
  }
  if (!response.ok || !data?.access_token) {
    throw new RefreshRejectedError(`Token refresh refused: ${JSON.stringify(data)}`);
  }
  return data;
}

export function parseJwtPayload(token: string): Record<string, unknown> {
  const base64Url = token.split(".")[1];
  const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  const json = decodeURIComponent(
    atob(base64)
      .split("")
      .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
      .join(""),
  );
  return JSON.parse(json);
}

export function buildLogoutUrl(postLogoutRedirectUri: string, idToken?: string): string {
  const params: Record<string, string> = {
    client_id: CLIENT_ID,
    post_logout_redirect_uri: postLogoutRedirectUri,
  };
  if (idToken) params["id_token_hint"] = idToken;
  return `${AUTH_SERVER}/connect/logout?${new URLSearchParams(params).toString()}`;
}

function base64UrlEncode(bytes: Uint8Array): string {
  let binary = "";
  for (const byte of bytes) binary += String.fromCharCode(byte);
  return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}
