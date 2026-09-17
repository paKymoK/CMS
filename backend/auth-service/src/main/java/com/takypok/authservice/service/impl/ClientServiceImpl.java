package com.takypok.authservice.service.impl;

import com.takypok.authservice.model.entity.ClientSessionPolicy;
import com.takypok.authservice.model.request.RegisteredClientRequest;
import com.takypok.authservice.model.response.RegisteredClientResponse;
import com.takypok.authservice.repository.ClientSessionPolicyRepository;
import com.takypok.authservice.service.ClientService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  private static final String SELECT_ALL_IDS =
      "SELECT id FROM oauth2_registered_client ORDER BY client_id_issued_at";
  private static final String DELETE_BY_ID = "DELETE FROM oauth2_registered_client WHERE id = ?";

  private final JdbcRegisteredClientRepository clientRepository;
  private final JdbcTemplate jdbcTemplate;
  private final ClientSessionPolicyRepository policyRepository;

  @Override
  public List<RegisteredClientResponse> getAll() {
    return jdbcTemplate.queryForList(SELECT_ALL_IDS, String.class).stream()
        .map(clientRepository::findById)
        .filter(Objects::nonNull)
        .map(
            c -> {
              ClientSessionPolicy policy = policyRepository.findById(c.getId()).orElse(null);
              boolean singleTab = policy != null && policy.isSingleTab();
              boolean failOpen = policy == null || policy.isFailOpen();
              return RegisteredClientResponse.from(c, singleTab, failOpen);
            })
        .collect(Collectors.toList());
  }

  @Override
  public RegisteredClientResponse getById(String id) {
    RegisteredClient client = clientRepository.findById(id);
    if (client == null) {
      throw new ApplicationException(Message.Application.ERROR, "Client not found");
    }
    ClientSessionPolicy policy = policyRepository.findById(id).orElse(null);
    boolean singleTab = policy != null && policy.isSingleTab();
    boolean failOpen = policy == null || policy.isFailOpen();
    return RegisteredClientResponse.from(client, singleTab, failOpen);
  }

  @Override
  public RegisteredClientResponse create(RegisteredClientRequest request) {
    if (clientRepository.findByClientId(request.getClientId()) != null) {
      throw new ApplicationException(Message.Application.ERROR, "Client ID already exists");
    }
    RegisteredClient client = buildClient(UUID.randomUUID().toString(), request, null);
    clientRepository.save(client);
    savePolicy(client.getId(), request.isSingleTabSession(), request.isFailOpen());
    return RegisteredClientResponse.from(
        client, request.isSingleTabSession(), request.isFailOpen());
  }

  @Override
  public RegisteredClientResponse update(String id, RegisteredClientRequest request) {
    RegisteredClient existing = clientRepository.findById(id);
    if (existing == null) {
      throw new ApplicationException(Message.Application.ERROR, "Client not found");
    }
    // If clientId changed, make sure the new one is not taken
    if (!existing.getClientId().equals(request.getClientId())
        && clientRepository.findByClientId(request.getClientId()) != null) {
      throw new ApplicationException(Message.Application.ERROR, "Client ID already exists");
    }
    jdbcTemplate.update(DELETE_BY_ID, id);
    RegisteredClient updated = buildClient(id, request, existing.getClientSecret());
    clientRepository.save(updated);
    savePolicy(id, request.isSingleTabSession(), request.isFailOpen());
    return RegisteredClientResponse.from(
        updated, request.isSingleTabSession(), request.isFailOpen());
  }

  @Override
  public void delete(String id) {
    if (clientRepository.findById(id) == null) {
      throw new ApplicationException(Message.Application.ERROR, "Client not found");
    }
    jdbcTemplate.update(DELETE_BY_ID, id);
  }

  private void savePolicy(String registeredClientId, boolean singleTab, boolean failOpen) {
    policyRepository.save(new ClientSessionPolicy(registeredClientId, singleTab, failOpen));
  }

  private RegisteredClient buildClient(
      String id, RegisteredClientRequest req, String existingSecret) {
    RegisteredClient.Builder builder =
        RegisteredClient.withId(id)
            .clientId(req.getClientId())
            .clientName(req.getClientName() != null ? req.getClientName() : req.getClientId());

    // Secret: use new one if provided, fall back to existing, or none for public clients
    String secret =
        (req.getClientSecret() != null && !req.getClientSecret().isBlank())
            ? "{noop}" + req.getClientSecret()
            : existingSecret;
    if (secret != null) {
      builder.clientSecret(secret);
    }

    req.getAuthenticationMethods().stream()
        .map(ClientAuthenticationMethod::new)
        .forEach(builder::clientAuthenticationMethod);

    req.getGrantTypes().stream()
        .map(AuthorizationGrantType::new)
        .forEach(builder::authorizationGrantType);

    req.getRedirectUris().forEach(builder::redirectUri);
    req.getPostLogoutRedirectUris().forEach(builder::postLogoutRedirectUri);
    req.getScopes().forEach(builder::scope);

    builder.clientSettings(
        ClientSettings.builder()
            .requireAuthorizationConsent(req.isRequireAuthorizationConsent())
            .requireProofKey(req.isRequireProofKey())
            .build());

    builder.tokenSettings(
        TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofMinutes(req.getAccessTokenTtlMinutes()))
            .refreshTokenTimeToLive(Duration.ofDays(req.getRefreshTokenTtlDays()))
            .reuseRefreshTokens(req.isReuseRefreshTokens())
            .build());

    return builder.build();
  }
}
