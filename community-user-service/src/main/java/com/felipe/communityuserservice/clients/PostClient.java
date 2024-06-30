package com.felipe.communityuserservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "COMMUNITY-POST-SERVICE", path = "/api/posts")
public interface PostClient {

  @DeleteMapping
  void deleteAllPosts(@RequestHeader("userId") String userId);
}
