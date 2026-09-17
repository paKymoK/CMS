package com.takypok.contentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication(scanBasePackages = {"com.takypok.*"})
@EnableR2dbcAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class ContentServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ContentServiceApplication.class, args);
  }
}
