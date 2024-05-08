package com.felipe.communityuserservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

  @Value("${jwt.public.key}")
  private RSAPublicKey publicKey;

  @Value("${jwt.private.key}")
  private RSAPrivateKey privateKey;

  public String generateToken(UserPrincipal userPrincipal) {
    Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
    return JWT.create()
      .withIssuer("community-user-service")
      .withSubject(userPrincipal.getUsername())
      .withExpiresAt(this.generateExpirationDate())
      .withClaim("userId", userPrincipal.getUser().getId())
      .sign(algorithm);
  }

  public Map<String, String> validateToken(String token) {
    Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("community-user-service").build();
    DecodedJWT decodedJWT = verifier.verify(token);

    Map<String, String> jwtClaims = new HashMap<>(2);
    jwtClaims.put("email", decodedJWT.getSubject());
    jwtClaims.put("userId", decodedJWT.getClaim("userId").asString());

    return jwtClaims;
  }

  private Instant generateExpirationDate() {
    return LocalDateTime.now().plusMinutes(2).toInstant(ZoneOffset.of("-03:00"));
  }
}
