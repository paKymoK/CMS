package com.takypok.authservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The company's own email domains are for staff only: a local (guest) account may not use an
 * address there, neither as its username nor as its email. Staff addresses come from the directory,
 * so a guest can never clash with a staff member and email stays unique without special cases.
 *
 * <p>Set {@code auth.internal-domain} ({@code AUTH_INTERNAL_DOMAIN}) to one domain or several,
 * comma-separated, e.g. {@code example.com} locally. A subdomain of a listed domain counts too
 * ({@code name@mail.example.com}); a look-alike such as {@code notexample.com} does not.
 */
@Component
public class EmailDomainPolicy {

  private final List<String> domains;

  public EmailDomainPolicy(@Value("${auth.internal-domain}") String configured) {
    this.domains =
        Arrays.stream(configured == null ? new String[0] : configured.split(","))
            .map(d -> d.trim().toLowerCase(Locale.ROOT))
            .map(d -> d.startsWith("@") ? d.substring(1) : d)
            .filter(d -> !d.isBlank())
            .toList();
  }

  /** True when the address is at one of the company domains (or a subdomain of one). */
  public boolean isInternal(String email) {
    String domain = domainOf(email);
    if (domain == null) return false;
    return domains.stream().anyMatch(d -> domain.equals(d) || domain.endsWith("." + d));
  }

  /**
   * For a guest's username or email. Throws IllegalArgumentException if it is not an address or is
   * at a company domain.
   *
   * @param what "Username" or "Email", for the message
   */
  public void requireGuestAddress(String email, String what) {
    if (domainOf(email) == null) {
      throw new IllegalArgumentException(what + " must be an email address (name@domain).");
    }
    if (isInternal(email)) {
      throw new IllegalArgumentException(
          "Addresses at "
              + String.join(", ", domains.stream().map(d -> "@" + d).toList())
              + " are reserved for staff accounts and cannot be used here.");
    }
  }

  /** The lower-cased part after the single '@', or null if this is not a plausible address. */
  private static String domainOf(String email) {
    if (email == null) return null;
    String e = email.trim();
    int at = e.indexOf('@');
    if (at < 1 || at != e.lastIndexOf('@') || at == e.length() - 1) return null;
    return e.substring(at + 1).toLowerCase(Locale.ROOT);
  }
}
