package com.felipe.community_post_service.services;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  @InjectMocks
  PostService postService;

  @Mock
  PostRepository postRepository;

  @Mock
  UploadService uploadService;

  private GenerateMocks mockData;

  @BeforeEach
  void setUp() {
    this.mockData = new GenerateMocks();
  }

  @Test
  @DisplayName("create - Should successfully create a post and return it")
  void createPostSuccess() {
    Post post = this.mockData.getPosts().get(0);
    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "file.txt",
      "text/plain",
      "Mock test file".getBytes()
    );
    UploadResponseDTO uploadResponseDTO = new UploadResponseDTO(
      "012345",
      "image.jpg",
      "post/image.jpg",
      2000L,
      "01",
      null,
      LocalDateTime.parse("2024-01-01T12:00:00.123456")
    );
    PostCreateDTO postCreateDTO = new PostCreateDTO(
      post.getTitle(),
      post.getContent(),
      post.getTags()
    );
    UUID mockedId = UUID.fromString("e3a703da-14be-4e96-a722-682f3a9a1be4");
    UploadDTO uploadDTO = new UploadDTO("post", mockedId.toString());

    try (MockedStatic<UUID> uuid = Mockito.mockStatic(UUID.class)) {
      uuid.when(UUID::randomUUID).thenReturn(mockedId);
      when(this.uploadService.uploadImage(eq(uploadDTO), any(MockMultipartFile.class))).thenReturn(uploadResponseDTO);
      when(this.postRepository.save(any(Post.class))).thenReturn(post);

      Post createdPost = this.postService.create("02", postCreateDTO, mockFile);

      assertThat(createdPost).isEqualTo(post);

      verify(this.uploadService, times(1)).uploadImage(eq(uploadDTO), any(MockMultipartFile.class));
      verify(this.postRepository, times(1)).save(any(Post.class));
    }
  }

  @Test
  @DisplayName("getAllPosts - Should successfully return all a Page with all posts")
  void getAllPostsSuccess() {
    List<Post> posts = this.mockData.getPosts();
    Page<Post> postPage = new PageImpl<>(posts);
    Pageable pagination = PageRequest.of(0, 10);

    when(this.postRepository.findAll(pagination)).thenReturn(postPage);

    Page<Post> allPostsPage = this.postService.getAllPosts(0);

    assertThat(allPostsPage.getTotalElements()).isEqualTo(2);
    assertThat(allPostsPage.getContent()).isEqualTo(posts);

    verify(this.postRepository, times(1)).findAll(pagination);
  }
}
