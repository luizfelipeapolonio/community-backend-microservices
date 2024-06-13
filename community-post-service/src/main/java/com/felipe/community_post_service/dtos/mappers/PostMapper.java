package com.felipe.community_post_service.dtos.mappers;

import com.felipe.community_post_service.clients.UploadClient;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.models.Post;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class PostMapper {

  private final DiscoveryClient discoveryClient;
  private final UploadClient uploadClient;

  public PostMapper(DiscoveryClient discoveryClient, UploadClient uploadClient) {
    this.discoveryClient = discoveryClient;
    this.uploadClient = uploadClient;
  }

  public PostResponseDTO toPostResponseDTO(Post post) {
    return new PostResponseDTO(
      post.getId(),
      post.getTitle(),
      post.getContent(),
      post.getOwnerId(),
      post.getTags(),
      this.generatePostImageUri(post.getPostImage()),
      post.getCreatedAt(),
      post.getUpdatedAt()
    );
  }

  private String generatePostImageUri(String postImagePath) {
    List<ServiceInstance> services = this.discoveryClient.getInstances("COMMUNITY-API-GATEWAY");
    URI gatewayUri = services.get(0).getUri();
    String uploadDirectory = this.uploadClient.getUploadProperties();
    String imageLocation = postImagePath.split("#")[1];
    String imagePath = "images/" + uploadDirectory + "/" + imageLocation;
    return UriComponentsBuilder.newInstance()
      .uri(gatewayUri)
      .path(imagePath)
      .toUriString();
  }
}
