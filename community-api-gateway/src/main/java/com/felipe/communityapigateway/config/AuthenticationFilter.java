package com.felipe.communityapigateway.config;

import com.felipe.communityapigateway.exceptions.MissingAuthException;
import com.felipe.communityapigateway.services.AuthService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final RouteValidator routeValidator;
  private final AuthService authService;

  public AuthenticationFilter(RouteValidator routeValidator, AuthService authService) {
    super(Config.class);
    this.routeValidator = routeValidator;
    this.authService = authService;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      if(this.routeValidator.isSecured.test(request)) {
        String authorizationHeader = this.extractAuthorizationHeader(request);
        String token = this.extractToken(authorizationHeader);
        Map<String, String> claims = this.authService.validateToken(token);
        String email = claims.get("email");
        String userId = claims.get("userId");

        request = request.mutate()
          .header("email", email)
          .header("userId", userId)
          .build();
      }
      return chain.filter(exchange.mutate().request(request).build());
    });
  }

  private String extractAuthorizationHeader(ServerHttpRequest request) {
    String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new MissingAuthException();
    }
    return authorizationHeader;
  }

  private String extractToken(String authorizationHeader) {
    return authorizationHeader.replace("Bearer ", "");
  }

  public static class Config {}
}
