import { cookies } from "next/headers";
import { NextResponse } from "next/server";
import { PREVIEW_TOKEN_COOKIE, PREVIEW_TOKEN_COOKIE_OPTIONS } from "@/lib/cms/previewCookie";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/**
 * Called periodically by DraftBanner while a preview tab stays open, rotating the token before
 * its 15-minute TTL lapses. content-service's refresh deletes the old token and issues a new one
 * (see PreviewTokenServiceImpl.refresh) — the cookie is updated to the new value here so the next
 * page load/renew keeps working with it.
 */
export async function POST() {
  const cookieStore = await cookies();
  const token = cookieStore.get(PREVIEW_TOKEN_COOKIE)?.value;
  if (!token) {
    return NextResponse.json({ error: "No active preview session" }, { status: 401 });
  }

  const res = await fetch(
    `${CMS_BASE}/content-service/v1/preview/tokens/refresh?token=${encodeURIComponent(token)}`,
    { method: "POST", cache: "no-store" },
  );
  if (!res.ok) {
    return NextResponse.json({ error: "Preview session expired" }, { status: 401 });
  }
  const body = (await res.json()) as { data: { token: string; expiresAt: string } };

  const response = NextResponse.json({ ok: true, expiresAt: body.data.expiresAt });
  response.cookies.set(PREVIEW_TOKEN_COOKIE, body.data.token, PREVIEW_TOKEN_COOKIE_OPTIONS);
  return response;
}
