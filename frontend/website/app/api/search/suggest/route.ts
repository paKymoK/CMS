import { NextResponse, type NextRequest } from "next/server";
import { getSuggestions } from "@/lib/cms/search";

/**
 * The browser's door to as-you-type suggestions. The CMS resolves the site from the Host header of
 * whoever calls it, so the visitor's browser can't call it directly (its Host is the CMS gateway's,
 * not the site's); this handler calls it server-side, like every other CMS read on the site.
 */
const TYPES = new Set(["post", "case_study"]);

export async function GET(request: NextRequest) {
  const q = request.nextUrl.searchParams.get("q")?.trim() ?? "";
  const type = request.nextUrl.searchParams.get("type") ?? "";
  if (q.length < 2 || q.length > 100 || !TYPES.has(type)) {
    return NextResponse.json({ suggestions: [] });
  }
  return NextResponse.json({ suggestions: await getSuggestions({ q, type }) });
}
