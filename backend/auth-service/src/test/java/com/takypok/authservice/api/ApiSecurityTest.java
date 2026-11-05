package com.takypok.authservice.api;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.takypok.authservice.config.ApiSecurityConfig;
import com.takypok.authservice.model.entity.UserStatus;
import com.takypok.authservice.service.SuperAdminService;
import com.takypok.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** The management API answers only to an active Super Admin carrying a bearer token. */
@WebMvcTest(UserController.class)
@Import(ApiSecurityConfig.class)
class ApiSecurityTest {

  @Autowired MockMvc mvc;

  @MockitoBean JwtDecoder jwtDecoder;
  @MockitoBean SuperAdminService superAdminService;
  @MockitoBean UserService userService;

  private static Authentication namedAdmin() {
    return argThat(a -> a != null && "admin".equals(a.getName()));
  }

  @Test
  void noTokenIsUnauthorized() throws Exception {
    mvc.perform(get("/v1/users")).andExpect(status().isUnauthorized());
    verifyNoInteractions(userService);
  }

  @Test
  void signedInUserWhoIsNotSuperAdminIsForbidden() throws Exception {
    // the mock answers false for everyone it was not told about

    mvc.perform(get("/v1/users").with(jwt().jwt(j -> j.subject("editor"))))
        .andExpect(status().isForbidden());
    verifyNoInteractions(userService);
  }

  @Test
  void superAdminMayList() throws Exception {
    when(superAdminService.isActiveSuperAdmin(namedAdmin())).thenReturn(true);

    mvc.perform(get("/v1/users").with(jwt().jwt(j -> j.subject("admin"))))
        .andExpect(status().isOk());
    verify(userService).search(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt());
  }

  @Test
  void superAdminMayChangeStatusWithoutCsrfToken() throws Exception {
    when(superAdminService.isActiveSuperAdmin(namedAdmin())).thenReturn(true);

    mvc.perform(
            patch("/v1/users/john@example.com/status")
                .with(jwt().jwt(j -> j.subject("admin")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INACTIVE\"}"))
        .andExpect(status().isOk());
    verify(userService).setStatus("john@example.com", UserStatus.INACTIVE);
  }

  @Test
  void otherManagementPathsAreCoveredToo() throws Exception {
    mvc.perform(get("/v1/clients/x/roles")).andExpect(status().isUnauthorized());
    mvc.perform(get("/v1/groups")).andExpect(status().isUnauthorized());
  }
}
