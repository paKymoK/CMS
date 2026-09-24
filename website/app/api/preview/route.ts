import { draftMode } from "next/headers";
import { NextRequest, NextResponse } from "next/server";
import { getPreviewHomeContent } from "@/lib/cms/homeContent";
import { PREVIEW_TOKEN_COOKIE, PREVIEW_TOKEN_COOKIE_OPTIONS } from "@/lib/cms/previewCookie";

/**
 * Entry point admin-app opens in a new tab after minting a preview_token. Validates the token by
 * actually fetching preview content with it before enabling draft mode — a bad/expired link
 * should fail clearly here, not silently enable draft mode and fail later on the page itself.
 */
export async function GET(request: NextRequest) {
  const token = request.nextUrl.searchParams.get("token");
  if (!token) {
    return new NextResponse("Missing preview token", { status: 400 });
  }

  try {
    await getPreviewHomeContent(token);
  } catch {
    return new NextResponse("Invalid or expired preview token", { status: 401 });
  }

  const draft = await draftMode();
  draft.enable();

  const response = NextResponse.redirect(new URL("/en", request.url));
  response.cookies.set(PREVIEW_TOKEN_COOKIE, token, PREVIEW_TOKEN_COOKIE_OPTIONS);
  return response;
}
