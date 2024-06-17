package com.felipe.community_post_service.dtos.mappers;

import com.felipe.community_post_service.clients.UploadClient;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.models.Post;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class PostMapper {

  private final DiscoveryClient discoveryClient;
  private final UploadClient uploadClient;
  private ServiceInstance gatewayInstance;
  private String uploadDirectory;

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

  private void getGatewayInstance() {
    this.gatewayInstance = this.discoveryClient.getInstances("COMMUNITY-API-GATEWAY").get(0);
  }

  private void getUploadDirectory() {
    this.uploadDirectory = this.uploadClient.getUploadProperties();
  }

  private String generatePostImageUri(String postImagePath) {
    // Check if a gateway service instance already exists
    if(this.gatewayInstance == null) {
      this.getGatewayInstance();
    }
    // Check if upload directory has already been set
    if(this.uploadDirectory == null) {
      this.getUploadDirectory();
    }
    URI gatewayUri = this.gatewayInstance.getUri();
    String imageLocation = postImagePath.split("#")[1];
    String imagePath = "images/" + this.uploadDirectory + "/" + imageLocation;
    return UriComponentsBuilder.newInstance()
      .uri(gatewayUri)
      .path(imagePath)
      .toUriString();
  }
}
