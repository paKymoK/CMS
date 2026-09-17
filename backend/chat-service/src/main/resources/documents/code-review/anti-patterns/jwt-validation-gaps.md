---
id: anti-pattern.jwt-validation-gaps
category: security
severity: high
applies_to: JWT-based authentication (resource servers, custom token parsing)
---

# Incomplete JWT verification

## Why it matters

A JWT carries its own claims about who issued it, who it's for, and when it expires — none of that
is trustworthy unless it's actually checked. A resource server that verifies only the signature
(or worse, decodes the token without verifying anything) will accept a token that's expired, meant
for a different audience/client, or issued by an untrusted party, as long as the signature happens
to validate. This is easy to get subtly wrong because the "happy path" — a normal, freshly-issued
token — passes every version of the check, correct or not.

## Bad example

```java
// Manually decoding claims without validating expiry/audience/issuer
Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
    .parseClaimsJws(token).getBody();
String userId = claims.getSubject(); // used without checking exp/aud/iss first
```

## Good example

```java
http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
    .jwtDecoder(NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
        .jwtValidator(JwtValidators.createDefaultWithValidators(
            new JwtIssuerValidator(issuerUri),
            new JwtAudienceValidator(expectedAudience))) // expiry is checked by createDefaultWithValidators
        .build())));
```

## Detection heuristic

- Manual JWT parsing (`Jwts.parser`, `JWT.decode`, hand-rolled base64+JSON) instead of the
  framework's resource-server support — a strong signal validation steps are being reinvented and
  likely incomplete.
- A `JwtDecoder`/resource-server config with no audience validator — accepting any token whose
  signature and issuer match, regardless of which client it was issued to.
- Any code path that reads a claim (`sub`, a role/scope claim) before the token has been through a
  decoder/validator, or that catches and ignores a `JwtException`/`SignatureException` instead of
  rejecting the request.
- `alg: none` accepted, or the signing algorithm not pinned (accepting both symmetric and
  asymmetric algorithms lets an attacker who knows the public key forge tokens with `HS256`).
