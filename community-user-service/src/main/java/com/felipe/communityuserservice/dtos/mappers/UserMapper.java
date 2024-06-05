package com.felipe.communityuserservice.dtos.mappers;

import com.felipe.communityuserservice.clients.UploadClient;
import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.models.User;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class UserMapper {

  private final DiscoveryClient discoveryClient;
  private final UploadClient uploadClient;

  public UserMapper(DiscoveryClient discoveryClient, UploadClient uploadClient) {
    this.discoveryClient = discoveryClient;
    this.uploadClient = uploadClient;
  }

  public UserResponseDTO toDTO(User user) {
    return new UserResponseDTO(
      user.getId(),
      user.getName(),
      user.getEmail(),
      user.getBio(),
      this.generateProfileImageUri(user.getProfileImage()),
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }

  private String generateProfileImageUri(String profileImagePath) {
    if(profileImagePath == null) return null;

    List<ServiceInstance> services = this.discoveryClient.getInstances("COMMUNITY-API-GATEWAY");
    URI gatewayUri = services.get(0).getUri();
    String uploadDirectory = this.uploadClient.getUploadProperties();
    String imageLocation = profileImagePath.split("#")[1];
    String imagePath = "images/" + uploadDirectory + "/" + imageLocation;

    return UriComponentsBuilder.newInstance()
      .uri(gatewayUri)
      .path(imagePath)
      .toUriString();
  }
}
