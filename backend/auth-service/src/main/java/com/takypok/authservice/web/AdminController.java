package com.takypok.authservice.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

  @GetMapping(value = {"/admin", "/admin/"})
  public String admin() {
    return "redirect:/admin/users";
  }

  /** Shown to a signed-in user who may not manage users (see SecurityConfig). */
  @GetMapping("/access-denied")
  public String accessDenied() {
    return "admin/forbidden";
  }
}
