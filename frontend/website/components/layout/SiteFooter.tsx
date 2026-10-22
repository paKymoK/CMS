import { getTranslations } from "next-intl/server";
import type { FooterNavCategory, SiteFooterContent } from "@/content/home/types";
import { FacebookIcon, InstagramIcon, TwitterIcon } from "@/components/ui/SocialIcons";

/**
 * The dark footer from the Figma "Footer": a "Let's connect" block with social icons on the left,
 * four link columns on the right, and a rule with the copyright and legal links. Columns come from
 * the CMS's footer navigation; the rest from the "site_footer" content item.
 */
export async function SiteFooter({
  footerNav,
  footer,
}: {
  footerNav: FooterNavCategory[];
  footer?: SiteFooterContent;
}) {
  const t = await getTranslations("footer");
  const year = new Date().getFullYear();
  const socials = [
    { name: "Facebook", href: footer?.facebookUrl, Icon: FacebookIcon },
    { name: "Twitter", href: footer?.twitterUrl, Icon: TwitterIcon },
    { name: "Instagram", href: footer?.instagramUrl, Icon: InstagramIcon },
  ].filter((s) => s.href);

  return (
    <footer className="design-canvas">
      <div className="ftr">
        <p className="ftr-lead">{footer?.title ?? "Let's connect"}</p>
        {footer?.ctaUrl && (
          <a href={footer.ctaUrl} className="ftr-cta">
            {footer.ctaLabel ?? "Get in touch"}
          </a>
        )}
        {socials.length > 0 && (
          <div className="ftr-social">
            {socials.map(({ name, href, Icon }) => (
              <a key={name} href={href} aria-label={name} className="text-white">
                <Icon className="h-full w-full" />
              </a>
            ))}
          </div>
        )}

        <nav aria-label="Footer" className="ftr-cols">
          {footerNav.slice(0, 4).map((category) => (
            <div key={category.label} className="ftr-col">
              <h3 className="ftr-col-title">{category.label}</h3>
              <ul>
                {category.links.map((link) => (
                  <li key={link}>
                    <a href="#">{link}</a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </nav>

        <div className="ftr-bar">
          <p>{footer?.copyright ?? t("copyright", { year })}</p>
          <div className="ftr-legal">
            {footer?.privacyLabel && <a href={footer.privacyUrl ?? "#"}>{footer.privacyLabel}</a>}
            {footer?.termsLabel && <a href={footer.termsUrl ?? "#"}>{footer.termsLabel}</a>}
          </div>
        </div>
      </div>
    </footer>
  );
}
