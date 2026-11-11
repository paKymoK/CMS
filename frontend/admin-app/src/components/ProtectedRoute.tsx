import { Navigate, Outlet } from "react-router-dom";
import { Spin } from "antd";
import { useAuth } from "../auth/useAuth";
import { tokenStore } from "../auth/tokenStore";

export default function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spin size="large" />
      </div>
    );
  }

  if (!isAuthenticated && !tokenStore.getAccess()) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
