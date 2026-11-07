package com.takypok.authservice.web;

import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.model.request.ClientRoleAssignmentRequest;
import com.takypok.authservice.model.request.CreateUserRequest;
import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.model.response.ClientRoleAssignmentResponse;
import com.takypok.authservice.model.response.UserResponse;
import com.takypok.authservice.service.AccountMailService;
import com.takypok.authservice.service.ClientRoleService;
import com.takypok.authservice.service.DepartmentService;
import com.takypok.authservice.service.SessionService;
import com.takypok.authservice.service.SuperAdminService;
import com.takypok.authservice.service.UnitService;
import com.takypok.authservice.service.UserService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.PageResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

/**
 * The Users screens (BRD USR-01 to USR-04), server-rendered. Reached only by an active Super Admin
 * (SecurityConfig) with the browser session from /login; forms carry a CSRF token. Everything goes
 * through the same services as the /v1 API, so the rules (inactive accounts, the last Super Admin)
 * are the same in both.
 *
 * <p>Errors are caught here and shown on the page: the global advice would answer with JSON.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {

  private static final int PAGE_SIZE = 20;
  private static final int MIN_PASSWORD_LENGTH = 8;

  /** The roles that can be given on cms-admin. Today only the one. */
  private static final List<String> ROLES = List.of(SuperAdminService.ROLE);

  private static final Pattern SITE_CODE = Pattern.compile("^[a-z0-9][a-z0-9_-]{0,49}$");

  private final UserService userService;
  private final ClientRoleService clientRoleService;
  private final DepartmentService departmentService;
  private final UnitService unitService;
  private final RegisteredClientRepository registeredClientRepository;
  private final AccountMailService accountMail;
  private final SessionService sessionService;

  @Value("${auth.admin-app-url}")
  private String adminAppUrl;

  @ModelAttribute("cmsUrl")
  String cmsUrl() {
    return adminAppUrl;
  }

  /** Whether an SMTP server is configured: the screens only offer email features when it is. */
  @ModelAttribute("mailEnabled")
  boolean mailEnabled() {
    return accountMail.isEnabled();
  }

  @ModelAttribute("me")
  String me(Authentication authentication) {
    return authentication.getName();
  }

  @GetMapping
  public String list(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) String project,
      @RequestParam(defaultValue = "0") int page,
      Model model) {
    PageResponse<UserResponse> result;
    try {
      result = userService.search(q, status, role, project, page, PAGE_SIZE);
    } catch (ApplicationException ex) {
      model.addAttribute("error", message(ex));
      result = PageResponse.<UserResponse>builder().content(List.of()).size(PAGE_SIZE).build();
    }
    model.addAttribute("result", result);
    model.addAttribute("q", q == null ? "" : q);
    model.addAttribute("status", status == null ? "" : status);
    model.addAttribute("role", role == null ? "" : role);
    model.addAttribute("project", project == null ? "" : project);
    model.addAttribute("roleOptions", ROLES);
    model.addAttribute("active", "users");
    return "admin/users";
  }

  @GetMapping("/new")
  public String newForm(Model model) {
    model.addAttribute("active", "users");
    return "admin/user-new";
  }

  @PostMapping("/new")
  public String create(
      @RequestParam String username,
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "password") String mode,
      @RequestParam(required = false) String password,
      @RequestParam(required = false) String confirm,
      @RequestParam(required = false) String notify,
      Model model,
      RedirectAttributes redirect) {
    String login = username.trim();
    boolean invite = "invite".equals(mode);
    String error = null;
    if (invite && !accountMail.isEnabled()) {
      error = "Email is not set up, so an invitation cannot be sent.";
    } else if (!invite) {
      if (password == null || !password.equals(confirm)) {
        error = "The two passwords do not match.";
      } else if (password.length() < MIN_PASSWORD_LENGTH) {
        error = "The password needs at least " + MIN_PASSWORD_LENGTH + " characters.";
      }
    }
    if (error == null) {
      try {
        if (invite) {
          userService.createInvited(login, Forms.blankToNull(name));
          if (accountMail.sendInvitation(login)) {
            redirect.addFlashAttribute("flash", "User created and invitation sent.");
          } else {
            redirect.addFlashAttribute("flash", "User created.");
            redirect.addFlashAttribute(
                "error", "The invitation email could not be sent. Use Resend invitation below.");
          }
        } else {
          userService.create(new CreateUserRequest(login, password));
          if (name != null && !name.isBlank()) {
            UpdateUserProfileRequest profile = new UpdateUserProfileRequest();
            profile.setName(name.trim());
            userService.updateProfile(login, profile);
          }
          redirect.addFlashAttribute("flash", "User created.");
          if (notify != null && accountMail.isEnabled() && !accountMail.sendCreatedNotice(login)) {
            redirect.addFlashAttribute("error", "The notification email could not be sent.");
          }
        }
        return "redirect:/admin/users/" + UriUtils.encodePathSegment(login, StandardCharsets.UTF_8);
      } catch (RuntimeException ex) {
        error = message(ex);
      }
    }
    model.addAttribute("error", error);
    model.addAttribute("username", login);
    model.addAttribute("name", name == null ? "" : name);
    model.addAttribute("mode", invite ? "invite" : "password");
    model.addAttribute("active", "users");
    return "admin/user-new";
  }

  @GetMapping("/{sub}")
  public String detail(@PathVariable String sub, Model model, RedirectAttributes redirect) {
    UserResponse user;
    try {
      user = userService.get(sub);
    } catch (ApplicationException ex) {
      redirect.addFlashAttribute("error", "That user does not exist.");
      return "redirect:/admin/users";
    }
    model.addAttribute("user", user);
    model.addAttribute(
        "superAdmin",
        user.getRoles().stream()
            .anyMatch(r -> r.getProjectId() == null && SuperAdminService.ROLE.equals(r.getRole())));
    model.addAttribute("direct", directAssignments(sub));
    model.addAttribute("sessions", sessionService.list(sub));
    model.addAttribute("departments", departmentService.get());
    model.addAttribute("units", unitService.get(null));
    model.addAttribute("roleOptions", ROLES);
    model.addAttribute("active", "users");
    return "admin/user-detail";
  }

  @PostMapping("/{sub}/profile")
  public String saveProfile(
      @PathVariable String sub,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) Long departmentId,
      @RequestParam(required = false) Long unitId,
      @RequestParam(required = false) String displayName,
      @RequestParam(required = false) String dateOfBirth,
      RedirectAttributes redirect) {
    try {
      boolean directory = "DIRECTORY".equals(userService.get(sub).getAccountType());
      if (departmentId != null
          && unitId != null
          && !departmentId.equals(unitService.getById(unitId).getDepartmentId())) {
        throw new IllegalArgumentException("That unit does not belong to the chosen department.");
      }
      // The form is the whole profile: an empty field clears it. A directory account's name and
      // email are LDAP's, so the screen leaves them out of the update.
      UpdateUserProfileRequest request = new UpdateUserProfileRequest();
      if (!directory) {
        request.setName(Forms.blankToNull(name));
        request.setEmail(Forms.blankToNull(email));
      }
      request.setDepartmentId(departmentId);
      request.setUnitId(unitId);
      request.setDisplayName(Forms.blankToNull(displayName));
      request.setDateOfBirth(Forms.parseBirthDate(dateOfBirth));
      userService.updateProfile(sub, request);
      redirect.addFlashAttribute("flash", "Profile saved.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/status")
  public String setStatus(
      @PathVariable String sub, @RequestParam String status, RedirectAttributes redirect) {
    try {
      UserStatus target = UserStatus.valueOf(status);
      userService.setStatus(sub, target);
      redirect.addFlashAttribute(
          "flash",
          target == UserStatus.INACTIVE
              ? "Account deactivated. It can no longer sign in."
              : "Account reactivated.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/delete")
  public String delete(
      @PathVariable String sub, Authentication authentication, RedirectAttributes redirect) {
    try {
      userService.delete(sub, authentication.getName());
      redirect.addFlashAttribute("flash", "Account deleted.");
      return "redirect:/admin/users";
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
      return "redirect:/admin/users/{sub}";
    }
  }

  @PostMapping("/{sub}/sessions/{id}/end")
  public String endSession(
      @PathVariable String sub, @PathVariable String id, RedirectAttributes redirect) {
    try {
      sessionService.end(sub, id);
      redirect.addFlashAttribute("flash", "Session ended.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/sessions/end-all")
  public String endAllSessions(@PathVariable String sub, RedirectAttributes redirect) {
    sessionService.endAll(sub, null, "ended by an administrator");
    redirect.addFlashAttribute("flash", "Every session of this person was ended.");
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/invitation")
  public String resendInvitation(@PathVariable String sub, RedirectAttributes redirect) {
    try {
      if (!accountMail.isEnabled()) {
        throw new IllegalArgumentException("Email is not set up.");
      }
      if (accountMail.sendInvitation(sub)) {
        redirect.addFlashAttribute("flash", "Invitation sent.");
      } else {
        redirect.addFlashAttribute("error", "The invitation email could not be sent.");
      }
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/password-reset")
  public String sendPasswordReset(@PathVariable String sub, RedirectAttributes redirect) {
    try {
      if (!accountMail.isEnabled()) {
        throw new IllegalArgumentException("Email is not set up.");
      }
      if (accountMail.sendPasswordReset(sub)) {
        redirect.addFlashAttribute(
            "flash", "Reset link sent. The password stays as it is until the person uses it.");
      } else {
        redirect.addFlashAttribute("error", "The reset email could not be sent.");
      }
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/roles")
  public String addRole(
      @PathVariable String sub,
      @RequestParam String role,
      @RequestParam(required = false) String project,
      RedirectAttributes redirect) {
    try {
      if (!ROLES.contains(role)) {
        throw new IllegalArgumentException("Unknown role.");
      }
      String site = Forms.blankToNull(project);
      if (site != null && !SITE_CODE.matcher(site).matches()) {
        throw new IllegalArgumentException(
            "A site code is lower-case letters, digits, - or _ (for example en).");
      }
      clientRoleService.assign(
          cmsAdminClientId(), new ClientRoleAssignmentRequest(sub, null, site, role));
      redirect.addFlashAttribute("flash", "Role added.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  @PostMapping("/{sub}/roles/{assignmentId}/remove")
  public String removeRole(
      @PathVariable String sub, @PathVariable String assignmentId, RedirectAttributes redirect) {
    try {
      // Only this user's own direct assignments can be removed from this page.
      boolean theirs =
          directAssignments(sub).stream().anyMatch(a -> a.getId().equals(assignmentId));
      if (!theirs) {
        throw new IllegalArgumentException("That role is not assigned to this user.");
      }
      clientRoleService.remove(cmsAdminClientId(), assignmentId);
      redirect.addFlashAttribute("flash", "Role removed.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/admin/users/{sub}";
  }

  private List<ClientRoleAssignmentResponse> directAssignments(String sub) {
    return clientRoleService.getAssignments(cmsAdminClientId()).stream()
        .filter(a -> "USER".equals(a.getType()) && sub.equals(a.getSubjectId()))
        .toList();
  }

  private String cmsAdminClientId() {
    RegisteredClient client =
        registeredClientRepository.findByClientId(SuperAdminService.CLIENT_ID);
    if (client == null) {
      throw new IllegalStateException("The cms-admin client is not registered.");
    }
    return client.getId();
  }

  /** The sentence to show: ApplicationException keeps it in its first argument. */
  private static String message(RuntimeException ex) {
    if (ex instanceof ApplicationException app
        && app.getArgs() != null
        && app.getArgs().length > 0) {
      return app.getArgs()[0];
    }
    if (ex instanceof IllegalArgumentException) {
      return ex.getMessage();
    }
    if (ex instanceof DataAccessException) {
      return "That could not be saved. It may already exist.";
    }
    return "Something went wrong. Nothing was changed.";
  }
}
