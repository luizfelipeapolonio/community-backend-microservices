package com.felipe.communityuserservice.dtos.mapper;

import com.felipe.communityuserservice.clients.UploadClient;
import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.dtos.mappers.UserMapper;
import com.felipe.communityuserservice.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

  @InjectMocks
  UserMapper userMapper;

  @Mock
  DiscoveryClient discoveryClient;

  @Mock
  UploadClient uploadClient;

  private User user;

  @BeforeEach
  void setUp() {
    LocalDateTime mockDateTime = LocalDateTime.parse("2024-01-01T12:00:00.123456");

    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("user1@email.com");
    user.setPassword("Encoded Password");
    user.setBio("Something meaningful");
    user.setProfileImage("imageId#path/image.jpg");
    user.setCreatedAt(mockDateTime);
    user.setUpdatedAt(mockDateTime);

    this.user = user;
  }

  @Test
  @DisplayName("toDTO - Should successfully convert a user entity into a UserResponseDTO")
  void convertToUserEntityToDTOSuccess() {
    List<ServiceInstance> services = getServiceInstances();
    String uploadDirectory = "uploads";
    String imageUri = "http://localhost:8080/images/uploads/path/image.jpg";

    when(this.discoveryClient.getInstances("COMMUNITY-API-GATEWAY")).thenReturn(services);
    when(this.uploadClient.getUploadProperties()).thenReturn(uploadDirectory);

    UserResponseDTO userResponseDTO = this.userMapper.toDTO(this.user);

    assertThat(userResponseDTO.id()).isEqualTo(this.user.getId());
    assertThat(userResponseDTO.name()).isEqualTo(this.user.getName());
    assertThat(userResponseDTO.email()).isEqualTo(this.user.getEmail());
    assertThat(userResponseDTO.bio()).isEqualTo(this.user.getBio());
    assertThat(userResponseDTO.profileImage()).isEqualTo(imageUri);
    assertThat(userResponseDTO.createdAt()).isEqualTo(this.user.getCreatedAt());
    assertThat(userResponseDTO.updatedAt()).isEqualTo(this.user.getUpdatedAt());
  }

  private List<ServiceInstance> getServiceInstances() {
    ServiceInstance gatewayServiceInstance = new ServiceInstance() {
      @Override
      public String getServiceId() {
        return "";
      }

      @Override
      public String getHost() {
        return "";
      }

      @Override
      public int getPort() {
        return 0;
      }

      @Override
      public boolean isSecure() {
        return false;
      }

      @Override
      public URI getUri() {
        return URI.create("http://localhost:8080");
      }

      @Override
      public Map<String, String> getMetadata() {
        return null;
      }
    };
    return List.of(gatewayServiceInstance);
  }
}
