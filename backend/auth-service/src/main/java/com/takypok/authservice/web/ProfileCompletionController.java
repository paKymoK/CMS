package com.takypok.authservice.web;

import com.takypok.authservice.model.entity.Userinfo;
import com.takypok.authservice.repository.UserinfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProfileCompletionController {

  private final UserinfoRepository userinfoRepository;
  private final RequestCache requestCache;

  @GetMapping("/profile/complete")
  public String complete(@RequestParam(required = false) String error, Model model) {
    if (error != null) {
      model.addAttribute("error", true);
    }
    return "profile-complete";
  }

  @PostMapping("/profile/complete")
  public void submit(
      @RequestParam String name,
      Authentication authentication,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    if (name == null || name.isBlank()) {
      response.sendRedirect("/profile/complete?error=true");
      return;
    }

    String sub = authentication.getName();
    Userinfo userinfo = userinfoRepository.getBySub(sub);
    if (userinfo == null) {
      userinfo = new Userinfo();
      userinfo.setSub(sub);
    }
    userinfo.setName(name.trim());
    userinfoRepository.save(userinfo);

    // The original /oauth2/authorize?... request was never consumed — it's still sitting in the
    // session's RequestCache, parked there by LdapAutoProvisionSuccessHandler's redirect here
    // instead of its usual super.onAuthenticationSuccess() call. Resume it now.
    SavedRequest savedRequest = requestCache.getRequest(request, response);
    response.sendRedirect(savedRequest != null ? savedRequest.getRedirectUrl() : "/");
  }
}
