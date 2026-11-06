package com.takypok.authservice.web;

import com.takypok.authservice.service.AccountMailService;
import com.takypok.authservice.service.PasswordSetupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The public pages behind the links in emails: activate an invited account, ask for a password
 * reset, and set a new password. None needs a session. They never say whether an email address has
 * an account, and a link that does not work looks the same whatever the reason.
 */
@Controller
@RequiredArgsConstructor
public class AccountSetupController {

  private final PasswordSetupService setup;
  private final AccountMailService accountMail;

  // ── Activation (invitation link) ──

  @GetMapping("/activate")
  public String activateForm(@RequestParam(required = false) String token, Model model) {
    model.addAttribute("token", token);
    model.addAttribute("account", setup.peekActivation(token).orElse(null));
    return "activate";
  }

  @PostMapping("/activate")
  public String activate(
      @RequestParam String token,
      @RequestParam String password,
      @RequestParam String confirm,
      HttpServletRequest request,
      Model model) {
    try {
      if (!password.equals(confirm)) {
        throw new IllegalArgumentException("The two passwords do not match.");
      }
      setup.activate(token, password, request.getRemoteAddr());
      return "redirect:/login?activated";
    } catch (PasswordSetupService.InvalidLinkException ex) {
      model.addAttribute("account", null);
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", ex.getMessage());
      model.addAttribute("account", setup.peekActivation(token).orElse(null));
    }
    model.addAttribute("token", token);
    return "activate";
  }

  // ── Forgot password ──

  @GetMapping("/forgot-password")
  public String forgotForm(Model model) {
    model.addAttribute("enabled", accountMail.isEnabled());
    model.addAttribute("sent", false);
    return "forgot-password";
  }

  @PostMapping("/forgot-password")
  public String forgot(@RequestParam String email, HttpServletRequest request, Model model) {
    accountMail.requestReset(email, request.getRemoteAddr());
    model.addAttribute("enabled", accountMail.isEnabled());
    model.addAttribute("sent", accountMail.isEnabled()); // the same answer for every address
    return "forgot-password";
  }

  // ── Reset (link from the email) ──

  @GetMapping("/reset-password")
  public String resetForm(@RequestParam(required = false) String token, Model model) {
    model.addAttribute("token", token);
    model.addAttribute("account", setup.peekReset(token).orElse(null));
    return "reset-password";
  }

  @PostMapping("/reset-password")
  public String reset(
      @RequestParam String token,
      @RequestParam String password,
      @RequestParam String confirm,
      HttpServletRequest request,
      Model model) {
    try {
      if (!password.equals(confirm)) {
        throw new IllegalArgumentException("The two passwords do not match.");
      }
      setup.reset(token, password, request.getRemoteAddr());
      return "redirect:/login?reset";
    } catch (PasswordSetupService.InvalidLinkException ex) {
      model.addAttribute("account", null);
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", ex.getMessage());
      model.addAttribute("account", setup.peekReset(token).orElse(null));
    }
    model.addAttribute("token", token);
    return "reset-password";
  }
}
