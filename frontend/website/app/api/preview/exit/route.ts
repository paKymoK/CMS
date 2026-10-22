import { draftMode } from "next/headers";
import { NextRequest, NextResponse } from "next/server";
import { PREVIEW_TOKEN_COOKIE } from "@/lib/cms/previewCookie";

export async function GET(request: NextRequest) {
  const draft = await draftMode();
  draft.disable();

  const response = NextResponse.redirect(new URL("/en", request.url));
  response.cookies.delete(PREVIEW_TOKEN_COOKIE);
  return response;
}
