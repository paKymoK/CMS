import type { HomeContent } from "@/content/home/types";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/**
 * content-service resolves the site from this request's Host header (never a query param),
 * so this must always go through gateway-service, never straight to content-service's own
 * port — see NEXT_PUBLIC_CMS_API_BASE_URL's comment in .env.example.
 */
export async function getHomeContent(): Promise<HomeContent> {
  const res = await fetch(`${CMS_BASE}/content-service/v1/home`, {
    next: { revalidate: 60 },
  });
  if (!res.ok) {
    throw new Error(`Failed to load home content: ${res.status} ${res.statusText}`);
  }
  const body = (await res.json()) as { data: HomeContent };
  return body.data;
}
