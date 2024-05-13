package com.felipe.communityapigateway.services;

import com.felipe.communityapigateway.exceptions.AuthValidationException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

  private final RestClient restClient;
  private final DiscoveryClient discoveryClient;

  public AuthService(DiscoveryClient discoveryClient) {
    this.restClient = RestClient.create();
    this.discoveryClient = discoveryClient;
  }

  public Map<String, String> validateToken(String token) {
    List<ServiceInstance> services = this.discoveryClient.getInstances("COMMUNITY-USER-SERVICE");
    URI userServiceUri = null;

    if(!services.isEmpty()) {
      userServiceUri = services.get(0).getUri();
    }

    try {
      return this.restClient.get()
        .uri(userServiceUri + "/api/auth/validate")
        .header("accessToken", token)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {
        });
    } catch(HttpClientErrorException e) {
      throw new AuthValidationException(e.getResponseBodyAsString());
    }
  }
}
