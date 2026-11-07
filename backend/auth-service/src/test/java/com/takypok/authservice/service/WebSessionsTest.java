package com.takypok.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

class WebSessionsTest {

  WebSessions web = new WebSessions();

  private HttpSession session(String id, String owner) {
    HttpSession s = mock(HttpSession.class);
    when(s.getId()).thenReturn(id);
    if (owner != null) {
      SecurityContext ctx =
          new SecurityContextImpl(new UsernamePasswordAuthenticationToken(owner, "x"));
      when(s.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
          .thenReturn(ctx);
    }
    web.sessionCreated(new HttpSessionEvent(s));
    return s;
  }

  @Test
  void theHashIsStableUrlSafeAndNotTheId() {
    String h = WebSessions.hash("ABC123");

    assertThat(h)
        .isEqualTo(WebSessions.hash("ABC123"))
        .isNotEqualTo("ABC123")
        .matches("[A-Za-z0-9_-]+");
    assertThat(WebSessions.hash("ABC124")).isNotEqualTo(h);
  }

  @Test
  void endingByHashInvalidatesOnlyThatSession() {
    HttpSession a = session("A", "ann");
    HttpSession b = session("B", "ann");

    assertThat(web.endByHash(WebSessions.hash("A"))).isEqualTo(1);

    verify(a).invalidate();
    verify(b, never()).invalidate();
    assertThat(web.endByHash(null)).isZero();
  }

  @Test
  void endingEveryoneOfAPersonSparesTheKeptSessionAndOtherPeople() {
    HttpSession a = session("A", "ann");
    HttpSession b = session("B", "ann");
    HttpSession c = session("C", "bob");
    HttpSession anon = session("D", null);

    int ended = web.endAllOf("ann", WebSessions.hash("A"));

    assertThat(ended).isEqualTo(1);
    verify(b).invalidate();
    verify(a, never()).invalidate();
    verify(c, never()).invalidate();
    verify(anon, never()).invalidate();
  }

  @Test
  void aChangedSessionIdIsFollowed() {
    HttpSession s = session("OLD", "ann");
    when(s.getId()).thenReturn("NEW");
    web.sessionIdChanged(new HttpSessionEvent(s), "OLD");

    assertThat(web.endByHash(WebSessions.hash("OLD"))).isZero();
    assertThat(web.endByHash(WebSessions.hash("NEW"))).isEqualTo(1);
  }

  @Test
  void aSessionThatIsAlreadyGoneIsNotCountedOrThrown() {
    HttpSession s = session("A", "ann");
    org.mockito.Mockito.doThrow(new IllegalStateException()).when(s).invalidate();

    assertThat(web.endAllOf("ann", null)).isZero();
  }

  @Test
  void destroyedSessionsAreForgotten() {
    HttpSession s = session("A", "ann");
    web.sessionDestroyed(new HttpSessionEvent(s));

    assertThat(web.endByHash(WebSessions.hash("A"))).isZero();
  }
}
