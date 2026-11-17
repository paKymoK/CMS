package com.takypok.authservice.web;

import com.takypok.authservice.config.auth.LdapAutoProvisionSuccessHandler;
import com.takypok.authservice.config.auth.PendingMfa;
import com.takypok.authservice.service.MfaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The second step of a sign-in. The password was already checked and the person is parked in the
 * session as {@link PendingMfa}; nobody is signed in until a code passes here. The sign-in then
 * finishes exactly as a one-step one does: new session id, security context, the same success
 * handler (LDAP provisioning, last login, audit, back to the page that asked).
 *
 * <p>Every outcome of the POST is a redirect (or the finished sign-in), so a refused code can be
 * shown with a plain reload-safe page.
 */
@Controller
@RequiredArgsConstructor
public class MfaChallengeController {

  private final MfaService mfaService;
  private final SecurityContextRepository securityContextRepository;
  private final LdapAutoProvisionSuccessHandler successHandler;

  private final ChangeSessionIdAuthenticationStrategy sessionStrategy =
      new ChangeSessionIdAuthenticationStrategy();

  @GetMapping("/login/mfa")
  public String form(
      HttpServletRequest request, @RequestParam(required = false) String recovery, Model model) {
    PendingMfa pending = PendingMfa.current(request.getSession(false));
    if (pending == null) {
      return "redirect:/login";
    }
    model.addAttribute("account", pending.principal().getName());
    model.addAttribute("recovery", recovery != null);
    return "mfa-challenge";
  }

  @PostMapping("/login/mfa")
  public void submit(
      @RequestParam String code,
      @RequestParam(required = false) String recovery,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, ServletException {
    String base = request.getContextPath();
    PendingMfa pending = PendingMfa.current(request.getSession(false));
    if (pending == null) {
      response.sendRedirect(base + "/login");
      return;
    }

    MfaService.Outcome outcome =
        mfaService.verifyAtLogin(pending.principal().getName(), code, pending.remoteAddress());
    if (outcome == MfaService.Outcome.LOCKED) {
      response.sendRedirect(base + "/login/mfa?locked");
      return;
    }
    if (!outcome.accepted()) {
      response.sendRedirect(base + "/login/mfa?error" + (recovery != null ? "&recovery" : ""));
      return;
    }

    PendingMfa.clear(request.getSession(false));
    Authentication authentication = pending.principal();
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    sessionStrategy.onAuthentication(authentication, request, response);
    securityContextRepository.saveContext(context, request, response);
    successHandler.onAuthenticationSuccess(request, response, authentication);
  }

  /** Gives up: back to the password page, nothing is signed in. */
  @PostMapping("/login/mfa/cancel")
  public String cancel(HttpServletRequest request) {
    PendingMfa.clear(request.getSession(false));
    return "redirect:/login";
  }
}
