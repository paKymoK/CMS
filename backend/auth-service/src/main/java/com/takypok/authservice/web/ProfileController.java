package com.takypok.authservice.web;

import com.takypok.authservice.model.request.UpdateUserProfileRequest;
import com.takypok.authservice.model.response.UserResponse;
import com.takypok.authservice.service.AvatarService;
import com.takypok.authservice.service.SuperAdminService;
import com.takypok.authservice.service.UserService;
import com.takypok.core.exception.ApplicationException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * "My profile" (BRD USR-07), for every signed-in user: view and edit one's own full name, display
 * name, date of birth and picture, change the password, sign out. It only ever acts on the
 * signed-in account: nothing here takes a sub from the request, and roles and site access are not
 * editable here. Name and password of a directory (LDAP) account belong to the directory and are
 * read-only.
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

  private final UserService userService;
  private final AvatarService avatarService;
  private final SuperAdminService superAdminService;

  @Value("${auth.admin-app-url}")
  private String adminAppUrl;

  @ModelAttribute("cmsUrl")
  String cmsUrl() {
    return adminAppUrl;
  }

  @ModelAttribute("me")
  String me(Authentication authentication) {
    return authentication.getName();
  }

  @GetMapping("/profile")
  public String view(
      Authentication authentication,
      @RequestParam(required = false) String avatarError,
      Model model) {
    String sub = authentication.getName();
    UserResponse user;
    try {
      user = userService.get(sub);
    } catch (ApplicationException ex) {
      model.addAttribute("error", "Your profile could not be found.");
      model.addAttribute("active", "profile");
      model.addAttribute("showAdminNav", false);
      return "admin/profile";
    }
    model.addAttribute("user", user);
    model.addAttribute("directory", "DIRECTORY".equals(user.getAccountType()));
    model.addAttribute(
        "avatarVersion",
        avatarService.find(sub).map(a -> a.updatedAt().toInstant().toEpochMilli()).orElse(null));
    model.addAttribute("showAdminNav", superAdminService.isActiveSuperAdmin(authentication));
    model.addAttribute("active", "profile");
    if ("size".equals(avatarError)) {
      model.addAttribute("error", "The picture must be 512 KB or smaller.");
    }
    return "admin/profile";
  }

  @PostMapping("/profile")
  public String save(
      Authentication authentication,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String displayName,
      @RequestParam(required = false) String dateOfBirth,
      RedirectAttributes redirect) {
    String sub = authentication.getName();
    try {
      boolean directory = "DIRECTORY".equals(userService.get(sub).getAccountType());
      UpdateUserProfileRequest request = new UpdateUserProfileRequest();
      if (!directory) {
        request.setName(Forms.blankToNull(name));
      }
      request.setDisplayName(Forms.blankToNull(displayName));
      request.setDateOfBirth(Forms.parseBirthDate(dateOfBirth));
      userService.updateProfile(sub, request);
      redirect.addFlashAttribute("flash", "Profile saved.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/profile";
  }

  @PostMapping("/profile/avatar")
  public String uploadAvatar(
      Authentication authentication,
      @RequestParam("file") MultipartFile file,
      RedirectAttributes redirect) {
    try {
      avatarService.save(authentication.getName(), file.getBytes());
      redirect.addFlashAttribute("flash", "Picture updated.");
    } catch (IOException ex) {
      redirect.addFlashAttribute("error", "The file could not be read.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/profile";
  }

  @PostMapping("/profile/avatar/remove")
  public String removeAvatar(Authentication authentication, RedirectAttributes redirect) {
    try {
      avatarService.remove(authentication.getName());
      redirect.addFlashAttribute("flash", "Picture removed.");
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/profile";
  }

  @PostMapping("/profile/password")
  public String changePassword(
      Authentication authentication,
      @RequestParam String current,
      @RequestParam("new") String newPassword,
      @RequestParam String confirm,
      RedirectAttributes redirect) {
    try {
      if (!newPassword.equals(confirm)) {
        throw new IllegalArgumentException("The two new passwords do not match.");
      }
      if (userService.changePassword(authentication.getName(), current, newPassword)) {
        redirect.addFlashAttribute(
            "flash",
            "Password changed. Other sessions will need to sign in again within 15 minutes.");
      } else {
        redirect.addFlashAttribute("error", "The current password is not correct.");
      }
    } catch (RuntimeException ex) {
      redirect.addFlashAttribute("error", message(ex));
    }
    return "redirect:/profile";
  }

  /** Any signed-in user may see a picture (it will appear next to names). Cached per version. */
  @GetMapping("/avatar/{sub:.+}")
  public ResponseEntity<byte[]> avatar(@PathVariable String sub) {
    return avatarService
        .find(sub)
        .map(
            a ->
                ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(a.contentType()))
                    .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePrivate())
                    .body(a.data()))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

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
      return "That could not be saved.";
    }
    return "Something went wrong. Nothing was changed.";
  }
}
