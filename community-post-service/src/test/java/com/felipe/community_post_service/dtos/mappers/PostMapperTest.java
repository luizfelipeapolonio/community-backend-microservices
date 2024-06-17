package com.felipe.community_post_service.dtos.mappers;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.clients.UploadClient;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.models.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PostMapperTest {

  @InjectMocks
  PostMapper postMapper;

  @Mock
  DiscoveryClient discoveryClient;

  @Mock
  UploadClient uploadClient;

  @Test
  @DisplayName("toPostResponseDTO - Should successfully convert a Post entity to a PostResponseDTO")
  void convertPostEntityToPostResponseDTOSuccess() {
    List<ServiceInstance> services = this.getServiceInstances();
    List<Post> posts = new GenerateMocks().getPosts();
    Post post = posts.get(0);
    String postImageUri = "http://localhost:8080/images/uploads/post/image.jpg";

    when(this.discoveryClient.getInstances("COMMUNITY-API-GATEWAY")).thenReturn(services);
    when(this.uploadClient.getUploadProperties()).thenReturn("uploads");

    PostResponseDTO convertedPost = this.postMapper.toPostResponseDTO(post);

    assertThat(convertedPost.id()).isEqualTo(post.getId());
    assertThat(convertedPost.title()).isEqualTo(post.getTitle());
    assertThat(convertedPost.content()).isEqualTo(post.getContent());
    assertThat(convertedPost.ownerId()).isEqualTo(post.getOwnerId());
    assertThat(convertedPost.tags()).isEqualTo(post.getTags());
    assertThat(convertedPost.postImage()).isEqualTo(postImageUri);
    assertThat(convertedPost.createdAt()).isEqualTo(post.getCreatedAt());
    assertThat(convertedPost.updatedAt()).isEqualTo(post.getUpdatedAt());

    verify(this.discoveryClient, times(1)).getInstances("COMMUNITY-API-GATEWAY");
    verify(this.uploadClient, times(1)).getUploadProperties();
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
