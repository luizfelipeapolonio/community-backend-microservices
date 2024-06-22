package com.felipe.community_post_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "COMMUNITY-USER-SERVICE", path = "/api/users")
public interface UserClient {

  @GetMapping("/infos/{userId}")
  Map<String, String> getUserInfos(@PathVariable String userId);
}
