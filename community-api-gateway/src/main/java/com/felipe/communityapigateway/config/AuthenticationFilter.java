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
      String token = null;

      if(this.routeValidator.isSecured.test(request)) {
        if(this.isAuthMissing(request)) {
          throw new MissingAuthException();
        }

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
          token = this.extractToken(authorizationHeader);
        }

        // TODO: REST call to the user service to validate token
        Map<String, String> claims = this.authService.validateToken(token);

        System.out.println("email: " + claims.get("email"));
        System.out.println("userId: " + claims.get("userId"));
      }
      return chain.filter(exchange);
    });
  }

  private boolean isAuthMissing(ServerHttpRequest request) {
    return !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
  }

  private String extractToken(String authorizationHeader) {
    return authorizationHeader.replace("Bearer ", "");
  }

  public static class Config {}
}
