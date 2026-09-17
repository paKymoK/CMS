package com.takypok.authservice.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login(HttpSession session, Model model) {
    Boolean locked = (Boolean) session.getAttribute("loginLocked");
    Integer attemptsLeft = (Integer) session.getAttribute("loginAttemptsLeft");
    session.removeAttribute("loginLocked");
    session.removeAttribute("loginAttemptsLeft");
    if (locked != null) model.addAttribute("locked", locked);
    if (attemptsLeft != null) model.addAttribute("attemptsLeft", attemptsLeft);
    return "login";
  }
}
