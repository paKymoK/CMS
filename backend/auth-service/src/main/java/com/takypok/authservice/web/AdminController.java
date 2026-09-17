package com.takypok.authservice.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

  @GetMapping(value = {"/admin", "/admin/"})
  public String admin() {
    return "forward:/admin/index.html";
  }
}
