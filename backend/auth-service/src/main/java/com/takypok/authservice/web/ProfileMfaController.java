package com.takypok.authservice.web;

import com.takypok.authservice.service.MfaService;
import com.takypok.authservice.service.QrCodes;
import com.takypok.authservice.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Turning two-step verification on and off from "My profile". Only ever acts on the signed-in
 * account. Setup needs a first code from the app before it takes effect; turning it off and
 * renewing recovery codes need a current code (an app code or a recovery code), so a stolen open
 * browser cannot switch it off.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile/mfa")
public class ProfileMfaController {

  private static final String VIEW = "admin/mfa-setup";

  private final MfaService mfaService;
  private final SuperAdminService superAdminService;

  @ModelAttribute("me")
  String me(Authentication authentication) {
    return authentication.getName();
  }

  @ModelAttribute("showAdminNav")
  boolean showAdminNav(Authentication authentication) {
    return superAdminService.isActiveSuperAdmin(authentication);
  }

  @GetMapping("/setup")
  public String setup(Authentication authentication, Model model, RedirectAttributes redirect) {
    String sub = authentication.getName();
    if (!mfaService.isAvailable() || mfaService.status(sub).enabled()) {
      return "redirect:/profile";
    }
    MfaService.Enrollment enrollment = mfaService.beginOrResume(sub);
    model.addAttribute("qr", QrCodes.pngDataUri(enrollment.uri(), 260));
    model.addAttribute("secret", groups(enrollment.secret()));
    model.addAttribute("codes", null);
    model.addAttribute("active", "profile");
    return VIEW;
  }

  @PostMapping("/setup")
  public String confirm(
      Authentication authentication,
      @RequestParam String code,
      Model model,
      RedirectAttributes redirect) {
    MfaService.Codes result = mfaService.confirm(authentication.getName(), code);
    if (!result.outcome().accepted()) {
      redirect.addFlashAttribute("error", refusal(result.outcome()));
      return "redirect:/profile/mfa/setup";
    }
    return showCodes(model, result.codes(), "Two-step verification is on.");
  }

  @PostMapping("/cancel")
  public String cancel(Authentication authentication) {
    mfaService.cancelSetup(authentication.getName());
    return "redirect:/profile";
  }

  @PostMapping("/disable")
  public String disable(
      Authentication authentication, @RequestParam String code, RedirectAttributes redirect) {
    MfaService.Outcome outcome = mfaService.disable(authentication.getName(), code);
    if (outcome.accepted()) {
      redirect.addFlashAttribute("flash", "Two-step verification is off.");
    } else {
      redirect.addFlashAttribute("error", refusal(outcome));
    }
    return "redirect:/profile";
  }

  @PostMapping("/recovery")
  public String renewRecovery(
      Authentication authentication,
      @RequestParam String code,
      Model model,
      RedirectAttributes redirect) {
    MfaService.Codes result = mfaService.renewRecoveryCodes(authentication.getName(), code);
    if (!result.outcome().accepted()) {
      redirect.addFlashAttribute("error", refusal(result.outcome()));
      return "redirect:/profile";
    }
    return showCodes(model, result.codes(), "New recovery codes. The old ones no longer work.");
  }

  private String showCodes(Model model, java.util.List<String> codes, String heading) {
    model.addAttribute("codes", codes);
    model.addAttribute("heading", heading);
    model.addAttribute("active", "profile");
    return VIEW;
  }

  private static String refusal(MfaService.Outcome outcome) {
    return outcome == MfaService.Outcome.LOCKED
        ? "Too many wrong codes. Try again in 10 minutes."
        : "That code is not correct. Check the app and try the current code.";
  }

  /** "ABCD EFGH ..." so a typed-in key is easy to read off. */
  private static String groups(String secret) {
    return secret.replaceAll("(.{4})(?=.)", "$1 ");
  }
}
