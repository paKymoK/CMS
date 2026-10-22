import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "../auth/AuthProvider";
import { SiteProvider } from "../lib/SiteProvider";
import ProtectedRoute from "../components/ProtectedRoute";
import AppShell from "../components/AppShell";
import Login from "../pages/Login";
import Callback from "../pages/Callback";
import ContentPage from "../pages/ContentPage";
import MediaLibraryPage from "../pages/MediaLibraryPage";
import AssistantPage from "../pages/AssistantPage";
import PostEditorPage from "../pages/PostEditorPage";
import ResourceEditorPage from "../pages/ResourceEditorPage";
import SearchPage from "../pages/SearchPage";
import ActivityPage from "../pages/ActivityPage";
import { RESOURCES } from "../config/resources";

export default function AppRouter() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/callback" element={<Callback />} />
          <Route element={<ProtectedRoute />}>
            <Route
              element={
                <SiteProvider>
                  <AppShell />
                </SiteProvider>
              }
            >
              <Route index element={<Navigate to={`/content/${RESOURCES[0].key}`} replace />} />
              <Route path="/content/:resourceKey" element={<ContentPage />} />
              <Route path="/posts/:id" element={<PostEditorPage />} />
              <Route path="/case-studies/:id" element={<ResourceEditorPage resourceKey="case-studies" />} />
              <Route path="/service-pages/:id" element={<ResourceEditorPage resourceKey="service-pages" />} />
              <Route path="/industry-pages/:id" element={<ResourceEditorPage resourceKey="industry-pages" />} />
              <Route path="/search" element={<SearchPage />} />
              <Route path="/activity" element={<ActivityPage />} />
              <Route path="/media" element={<MediaLibraryPage />} />
              <Route path="/assistant" element={<AssistantPage />} />
            </Route>
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
