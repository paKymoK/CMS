import { notFound } from "next/navigation";

// Unmatched URLs under a locale (/en/anything) land here so the 404 renders inside the locale
// layout, with the site header and footer, instead of the bare framework page.
export default function CatchAll() {
  notFound();
}
