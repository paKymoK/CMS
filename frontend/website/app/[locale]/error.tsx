"use client";

import { useEffect } from "react";
import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { ErrorPage, primaryActionClass, secondaryActionClass } from "@/components/error/ErrorPage";

export default function RouteError({ error, retry }: { error: unknown; retry: () => void }) {
  const t = useTranslations("errors");
  const digest = error instanceof Error ? (error as Error & { digest?: string }).digest : undefined;

  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <ErrorPage
      code="500"
      eyebrow={t("serverError.eyebrow")}
      title={t("serverError.title")}
      body={t("serverError.body")}
      footnote={digest ? t("reference", { id: digest }) : undefined}
      actions={
        <>
          <button type="button" onClick={() => retry()} className={primaryActionClass}>
            {t("tryAgain")}
          </button>
          <Link href="/" className={secondaryActionClass}>
            {t("backHome")}
          </Link>
        </>
      }
    />
  );
}
