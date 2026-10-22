import { createContext } from "react";

export interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  accessToken: string | null;
  user: Record<string, unknown> | null;
  authError: string | null;
  login: () => Promise<void>;
  logout: () => void;
  handleCallback: (code: string, codeVerifier: string) => Promise<void>;
  clearAuthError: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);
