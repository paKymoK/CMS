import type { Metadata } from "next";
import { getTranslations } from "next-intl/server";
import { Link } from "@/i18n/navigation";
import { ErrorPage, primaryActionClass, secondaryActionClass } from "@/components/error/ErrorPage";

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations("errors");
  return { title: `${t("notFound.title")} | CMC Global` };
}

export default async function NotFound() {
  const t = await getTranslations("errors");
  return (
    <ErrorPage
      code="404"
      eyebrow={t("notFound.eyebrow")}
      title={t("notFound.title")}
      body={t("notFound.body")}
      actions={
        <>
          <Link href="/" className={primaryActionClass}>
            {t("backHome")}
          </Link>
          <Link href="/insights" className={secondaryActionClass}>
            {t("browseInsights")}
          </Link>
        </>
      }
    />
  );
}
