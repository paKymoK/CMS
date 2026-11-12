import { FacebookIcon, InstagramIcon, TwitterIcon } from "./SocialIcons";

/** The "site_footer" content item: what the footer says apart from its link columns. */
export interface SiteFooterData {
  title: string;
  ctaLabel?: string;
  ctaUrl?: string;
  copyright?: string;
  privacyLabel?: string;
  privacyUrl?: string;
  termsLabel?: string;
  termsUrl?: string;
  facebookUrl?: string;
  twitterUrl?: string;
  instagramUrl?: string;
}

export interface FooterNavColumn {
  label: string;
  links: string[];
}

export interface SiteFooterViewProps {
  footerNav: FooterNavColumn[];
  footer?: SiteFooterData;
  /** Shown when the content item has no copyright line (the website translates it; the preview passes English). */
  defaultCopyright: string;
}

/**
 * The dark footer: a "Let's connect" block with social icons on the left, up to four link columns
 * on the right, and a rule with the copyright and legal links. Pure markup, so the website and the
 * admin's preview draw the same footer.
 */
export function SiteFooterView({ footerNav, footer, defaultCopyright }: SiteFooterViewProps) {
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
          <p>{footer?.copyright ?? defaultCopyright}</p>
          <div className="ftr-legal">
            {footer?.privacyLabel && <a href={footer.privacyUrl ?? "#"}>{footer.privacyLabel}</a>}
            {footer?.termsLabel && <a href={footer.termsUrl ?? "#"}>{footer.termsLabel}</a>}
          </div>
        </div>
      </div>
    </footer>
  );
}
