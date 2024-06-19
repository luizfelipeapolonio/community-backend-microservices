package com.felipe.community_post_service.services;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.PostUpdateDTO;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.exceptions.AccessDeniedException;
import com.felipe.community_post_service.exceptions.RecordNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
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

  @Test
  @DisplayName("getById - Should successfully return a post given the post id")
  void getByIdSuccess() {
    Post post = this.mockData.getPosts().get(0);

    when(this.postRepository.findById("01")).thenReturn(Optional.of(post));

    Post foundPost = this.postService.getById("01");

    assertThat(foundPost.getId()).isEqualTo(post.getId());
    assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
    assertThat(foundPost.getContent()).isEqualTo(post.getContent());
    assertThat(foundPost.getOwnerId()).isEqualTo(post.getOwnerId());
    assertThat(foundPost.getPostImage()).isEqualTo(post.getPostImage());
    assertThat(foundPost.getCreatedAt()).isEqualTo(post.getCreatedAt());
    assertThat(foundPost.getUpdatedAt()).isEqualTo(post.getUpdatedAt());
    assertThat(foundPost.getComments().size()).isEqualTo(post.getComments().size());
    assertThat(foundPost.getLikeDislike().size()).isEqualTo(post.getLikeDislike().size());

    verify(this.postRepository, times(1)).findById("01");
  }

  @Test
  @DisplayName("getById - Should throw a RecordNotFoundException if the post is not found")
  void getByIdFailsByPostNotFound() {
    when(this.postRepository.findById("01")).thenReturn(Optional.empty());

    Exception thrown = catchException(() -> this.postService.getById("01"));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Post de id: '01' não encontrado");

    verify(this.postRepository, times(1)).findById("01");
  }

  @Test
  @DisplayName("getAllUserPosts - Should successfully return a Page with all posts from a specific user")
  void getAllUserPostsSuccess() {
    List<Post> posts = this.mockData.getPosts();
    Page<Post> allUserPostsPage = new PageImpl<>(posts);
    Pageable pagination = PageRequest.of(0, 10);

    when(this.postRepository.findAllByOwnerId("02", pagination)).thenReturn(allUserPostsPage);

    Page<Post> allPosts = this.postService.getAllUserPosts("02", 0);

    assertThat(allPosts.getTotalElements()).isEqualTo(posts.size());
    assertThat(allPosts.getContent().stream().map(Post::getOwnerId).toList())
      .containsExactlyInAnyOrderElementsOf(posts.stream().map(Post::getOwnerId).toList());

    verify(this.postRepository, times(1)).findAllByOwnerId("02", pagination);
  }

  @Test
  @DisplayName("update - Should successfully update a post and return it")
  void updateSuccess() {
    Post post = this.mockData.getPosts().get(0);
    PostUpdateDTO postUpdateDTO = new PostUpdateDTO(
      "Post 1 updated",
      "A great post updated",
      new String[]{"updated", "post"}
    );
    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "file.txt",
      "text/plain",
      "Mock test file".getBytes()
    );
    UploadDTO uploadDTO = new UploadDTO("post", post.getId());
    UploadResponseDTO uploadResponseDTO = new UploadResponseDTO(
      "102030",
      "image.jpg",
      "post/updated_image.jpg",
      2000L,
      "01",
      null,
      LocalDateTime.parse("2024-01-01T12:00:00.123456")
    );

    when(this.postRepository.findById("01")).thenReturn(Optional.of(post));
    when(this.uploadService.uploadImage(eq(uploadDTO), any(MockMultipartFile.class))).thenReturn(uploadResponseDTO);
    when(this.postRepository.save(post)).thenReturn(post);

    Post updatedPost = this.postService.update("01", "02", postUpdateDTO, mockFile);

    assertThat(updatedPost.getId()).isEqualTo(post.getId());
    assertThat(updatedPost.getTitle()).isEqualTo(postUpdateDTO.title());
    assertThat(updatedPost.getContent()).isEqualTo(postUpdateDTO.content());
    assertThat(updatedPost.getTags()).isEqualTo(postUpdateDTO.tags());
    assertThat(updatedPost.getOwnerId()).isEqualTo(post.getOwnerId());
    assertThat(updatedPost.getPostImage()).isEqualTo(uploadResponseDTO.id() + "#" + uploadResponseDTO.path());
    assertThat(updatedPost.getCreatedAt()).isEqualTo(post.getCreatedAt());
    assertThat(updatedPost.getUpdatedAt()).isEqualTo(post.getUpdatedAt());
    assertThat(updatedPost.getComments().size()).isEqualTo(post.getComments().size());
    assertThat(updatedPost.getLikeDislike().size()).isEqualTo(post.getLikeDislike().size());

    verify(this.postRepository, times(1)).findById("01");
    verify(this.uploadService, times(1)).deleteImage("12345#post/image.jpg");
    verify(this.uploadService, times(1)).uploadImage(uploadDTO, mockFile);
    verify(this.postRepository, times(1)).save(post);
  }

  @Test
  @DisplayName("update - Should throw an AccessDeniedException if the user id is different from the post owner id")
  void updateFailsByUserIdIsDifferentFromOwnerId() {
    Post post = this.mockData.getPosts().get(0);
    PostUpdateDTO postUpdateDTO = new PostUpdateDTO(
      "Post 1 updated",
      "A great post updated",
      new String[]{"updated", "post"}
    );
    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "file.txt",
      "text/plain",
      "Mock test file".getBytes()
    );

    when(this.postRepository.findById("01")).thenReturn(Optional.of(post));

    Exception thrown = catchException(() -> this.postService.update("01", "01", postUpdateDTO, mockFile));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Você não tem permissão para alterar este recurso");

    verify(this.postRepository, times(1)).findById("01");
    verify(this.uploadService, never()).deleteImage(anyString());
    verify(this.uploadService, never()).uploadImage(any(UploadDTO.class), any(MockMultipartFile.class));
    verify(this.postRepository, never()).save(any(Post.class));
  }

  @Test
  @DisplayName("update - Should throw a RecordNotFoundException if the post is not found")
  void updateFailsByPostNotFound() {
    PostUpdateDTO postUpdateDTO = new PostUpdateDTO(
      "Post 1 updated",
      "A great post updated",
      new String[]{"updated", "post"}
    );
    MockMultipartFile mockFile = new MockMultipartFile(
      "file",
      "file.txt",
      "text/plain",
      "Mock test file".getBytes()
    );

    when(this.postRepository.findById("01")).thenReturn(Optional.empty());

    Exception thrown = catchException(() -> this.postService.update("01", "02", postUpdateDTO, mockFile));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Post de id: '01' não encontrado");

    verify(this.postRepository, times(1)).findById("01");
    verify(this.uploadService, never()).deleteImage(anyString());
    verify(this.uploadService, never()).uploadImage(any(UploadDTO.class), any(MockMultipartFile.class));
    verify(this.postRepository, never()).save(any(Post.class));
  }

  @Test
  @DisplayName("delete - Should successfully delete a post and return it")
  void deleteSuccess() {
    Post post = this.mockData.getPosts().get(0);

    when(this.postRepository.findById("01")).thenReturn(Optional.of(post));
    doNothing().when(this.postRepository).deleteById(post.getId());
    doNothing().when(this.uploadService).deleteImage(post.getPostImage());

    Post deletedPost = this.postService.delete("01", "02");

    assertThat(deletedPost.getId()).isEqualTo(post.getId());
    assertThat(deletedPost.getTitle()).isEqualTo(post.getTitle());
    assertThat(deletedPost.getContent()).isEqualTo(post.getContent());
    assertThat(deletedPost.getTags()).isEqualTo(post.getTags());
    assertThat(deletedPost.getOwnerId()).isEqualTo(post.getOwnerId());
    assertThat(deletedPost.getPostImage()).isEqualTo(post.getPostImage());
    assertThat(deletedPost.getComments().size()).isEqualTo(post.getComments().size());
    assertThat(deletedPost.getLikeDislike().size()).isEqualTo(post.getLikeDislike().size());
    assertThat(deletedPost.getCreatedAt()).isEqualTo(post.getCreatedAt());
    assertThat(deletedPost.getUpdatedAt()).isEqualTo(post.getUpdatedAt());

    verify(this.postRepository, times(1)).findById("01");
    verify(this.postRepository, times(1)).deleteById(post.getId());
    verify(this.uploadService, times(1)).deleteImage(post.getPostImage());
  }

  @Test
  @DisplayName("delete - Should throw a RecordNotFoundException if the post is not found")
  void deleteFailsByPostNotFound() {
    when(this.postRepository.findById("01")).thenReturn(Optional.empty());

    Exception thrown = catchException(() -> this.postService.delete("01", "02"));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Post de id: '01' não encontrado");

    verify(this.postRepository, times(1)).findById("01");
    verify(this.postRepository, never()).deleteById(anyString());
    verify(this.uploadService, never()).deleteImage(anyString());
  }

  @Test
  @DisplayName("delete - Should throw an AccessDeniedException if the user id is different from post owner id")
  void deleteFailsByUserIdIsDifferentFromOwnerId() {
    Post post = this.mockData.getPosts().get(0);

    when(this.postRepository.findById("01")).thenReturn(Optional.of(post));

    Exception thrown = catchException(() -> this.postService.delete("01", "01"));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Você não tem permissão para remover este recurso");

    verify(this.postRepository, times(1)).findById("01");
    verify(this.postRepository, never()).deleteById(anyString());
    verify(this.uploadService, never()).deleteImage(anyString());
  }
}
