package com.felipe.community_post_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.PostPageResponseDTO;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.dtos.PostUpdateDTO;
import com.felipe.community_post_service.dtos.mappers.PostMapper;
import com.felipe.community_post_service.exceptions.AccessDeniedException;
import com.felipe.community_post_service.exceptions.RecordNotFoundException;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.services.PostService;
import com.felipe.community_post_service.services.UploadService;
import com.felipe.community_post_service.util.response.CustomResponseBody;
import com.felipe.community_post_service.util.response.ResponseConditionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  PostService postService;

  @MockBean
  UploadService uploadService;

  @MockBean
  PostMapper postMapper;

  private GenerateMocks mockData;
  private final String BASE_URL = "/api/posts";

  @BeforeEach
  void setUp() {
    this.mockData = new GenerateMocks();
  }

  @Test
  @DisplayName("create - Should return a success response with created status code and the created post")
  void createPostSuccess() throws Exception {
    Post post = this.mockData.getPosts().get(0);
    PostCreateDTO postCreateDTO = new PostCreateDTO(
      "Post 1",
      "A great content",
      new String[]{"great", "post"}
    );
    String jsonBody = this.objectMapper.writeValueAsString(postCreateDTO);
    MockMultipartFile imageMultipart = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );
    String postImageUri = "http://localhost:8080/images/uploads/post/image.jpg";
    PostResponseDTO postResponseDTO = new PostResponseDTO(
      post.getId(),
      post.getTitle(),
      post.getContent(),
      post.getOwnerId(),
      post.getTags(),
      postImageUri,
      post.getCreatedAt(),
      post.getUpdatedAt()
    );

    when(this.uploadService.convertJsonStringToObject(jsonBody, PostCreateDTO.class)).thenReturn(postCreateDTO);
    when(this.postService.create(eq("02"), eq(postCreateDTO), any(MockMultipartFile.class))).thenReturn(post);
    when(this.postMapper.toPostResponseDTO(post)).thenReturn(postResponseDTO);

    this.mockMvc.perform(multipart(HttpMethod.POST, BASE_URL)
      .file("data", jsonBody.getBytes())
      .file("image", imageMultipart.getBytes())
      .header("userId", "02")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
      .andExpect(jsonPath("$.message").value("Post criado com sucesso"))
      .andExpect(jsonPath("$.data.id").value(postResponseDTO.id()))
      .andExpect(jsonPath("$.data.title").value(postResponseDTO.title()))
      .andExpect(jsonPath("$.data.content").value(postResponseDTO.content()))
      .andExpect(jsonPath("$.data.ownerId").value(postResponseDTO.ownerId()))
      .andExpect(jsonPath("$.data.tags[0]").value(postResponseDTO.tags()[0]))
      .andExpect(jsonPath("$.data.tags[1]").value(postResponseDTO.tags()[1]))
      .andExpect(jsonPath("$.data.postImage").value(postResponseDTO.postImage()))
      .andExpect(jsonPath("$.data.createdAt").value(postResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(postResponseDTO.updatedAt().toString()));

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, PostCreateDTO.class);
    verify(this.postService, times(1)).create(eq("02"), eq(postCreateDTO), any(MockMultipartFile.class));
    verify(this.postMapper, times(1)).toPostResponseDTO(post);
  }

  @Test
  @DisplayName("getAllPosts - Should return a success response with Ok status code and all posts")
  void getAllPostsSuccess() throws Exception {
    List<Post> posts = this.mockData.getPosts();
    Page<Post> allPostsPage = new PageImpl<>(posts);
    String postImageUri = "http://localhost:8080/images/uploads/post/image.jpg";
    List<PostResponseDTO> postsResponseDTO = allPostsPage.getContent().stream()
      .map(post -> new PostResponseDTO(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getOwnerId(),
        post.getTags(),
        postImageUri,
        post.getCreatedAt(),
        post.getUpdatedAt()
      ))
      .toList();
    PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO(
      postsResponseDTO,
      allPostsPage.getTotalElements(),
      allPostsPage.getTotalPages()
    );

    CustomResponseBody<PostPageResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Todos os posts");
    response.setData(postPageResponseDTO);

    String jsonResponseBody = this.objectMapper.writeValueAsString(response);

    when(this.postService.getAllPosts(0)).thenReturn(allPostsPage);
    when(this.postMapper.toPostResponseDTO(posts.get(0))).thenReturn(postsResponseDTO.get(0));
    when(this.postMapper.toPostResponseDTO(posts.get(1))).thenReturn(postsResponseDTO.get(1));

    this.mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().json(jsonResponseBody));

    verify(this.postService, times(1)).getAllPosts(0);
    verify(this.postMapper, times(1)).toPostResponseDTO(posts.get(0));
    verify(this.postMapper, times(1)).toPostResponseDTO(posts.get(1));
  }

  @Test
  @DisplayName("getById - Should return a success response with Ok status code and the found post")
  void getByIdSuccess() throws Exception {
    Post post = this.mockData.getPosts().get(0);
    PostResponseDTO postResponseDTO = new PostResponseDTO(
      post.getId(),
      post.getTitle(),
      post.getContent(),
      post.getOwnerId(),
      post.getTags(),
      "http://localhost:8080/images/uploads/post/image.jpg",
      post.getCreatedAt(),
      post.getUpdatedAt()
    );

    when(this.postService.getById("01")).thenReturn(post);
    when(this.postMapper.toPostResponseDTO(post)).thenReturn(postResponseDTO);

    this.mockMvc.perform(get(BASE_URL + "/01")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Post de id: '01' encontrado"))
      .andExpect(jsonPath("$.data.id").value(postResponseDTO.id()))
      .andExpect(jsonPath("$.data.title").value(postResponseDTO.title()))
      .andExpect(jsonPath("$.data.content").value(postResponseDTO.content()))
      .andExpect(jsonPath("$.data.ownerId").value(postResponseDTO.ownerId()))
      .andExpect(jsonPath("$.data.tags[0]").value(postResponseDTO.tags()[0]))
      .andExpect(jsonPath("$.data.tags[1]").value(postResponseDTO.tags()[1]))
      .andExpect(jsonPath("$.data.createdAt").value(postResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(postResponseDTO.updatedAt().toString()));

    verify(this.postService, times(1)).getById("01");
    verify(this.postMapper, times(1)).toPostResponseDTO(post);
  }

  @Test
  @DisplayName("getById - Should return an error response with not found status code")
  void getByIdFailsByPostNotFound() throws Exception {
    when(this.postService.getById("01"))
      .thenThrow(new RecordNotFoundException("Post de id: '01' não encontrado"));

    this.mockMvc.perform(get(BASE_URL + "/01")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Post de id: '01' não encontrado"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.postService, times(1)).getById("01");
    verify(this.postMapper, never()).toPostResponseDTO(any(Post.class));
  }

  @Test
  @DisplayName("getAllUserPosts - Should return a success response with Ok status code and a Page with all user posts")
  void getAllUserPostsSuccess() throws Exception {
    List<Post> posts = this.mockData.getPosts();
    Page<Post> allUserPostsPage = new PageImpl<>(posts);
    List<PostResponseDTO> postsResponseDTO = allUserPostsPage.getContent()
      .stream()
      .map(post -> new PostResponseDTO(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getOwnerId(),
        post.getTags(),
        "http://localhost:8080/images/uploads/post/image.jpg",
        post.getCreatedAt(),
        post.getUpdatedAt()
      ))
      .toList();
    PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO(
      postsResponseDTO,
      allUserPostsPage.getTotalElements(),
      allUserPostsPage.getTotalPages()
    );

    CustomResponseBody<PostPageResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Todos os posts do usuário de id: '02'");
    response.setData(postPageResponseDTO);

    String jsonResponseBody = this.objectMapper.writeValueAsString(response);

    when(this.postService.getAllUserPosts("02", 0)).thenReturn(allUserPostsPage);
    when(this.postMapper.toPostResponseDTO(posts.get(0))).thenReturn(postsResponseDTO.get(0));
    when(this.postMapper.toPostResponseDTO(posts.get(1))).thenReturn(postsResponseDTO.get(1));

    this.mockMvc.perform(get(BASE_URL + "/users/02")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().json(jsonResponseBody));

    verify(this.postService, times(1)).getAllUserPosts("02", 0);
    verify(this.postMapper, times(1)).toPostResponseDTO(posts.get(0));
    verify(this.postMapper, times(1)).toPostResponseDTO(posts.get(1));
  }

  @Test
  @DisplayName("update - Should return a success response with Ok status code and the updated post")
  void updateSuccess() throws Exception {
    Post post = this.mockData.getPosts().get(0);
    PostUpdateDTO postUpdateDTO = new PostUpdateDTO(
      "Updated post",
      "Updated content",
      new String[]{"updated", "post"}
    );
    MockMultipartFile mockFile = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );
    PostResponseDTO postResponseDTO = new PostResponseDTO(
      post.getId(),
      postUpdateDTO.title(),
      postUpdateDTO.content(),
      post.getOwnerId(),
      postUpdateDTO.tags(),
      "http://localhost:8080/images/uploads/post/updated_image.jpg",
      post.getCreatedAt(),
      post.getUpdatedAt()
    );
    String jsonBody = this.objectMapper.writeValueAsString(postUpdateDTO);

    when(this.uploadService.convertJsonStringToObject(jsonBody, PostUpdateDTO.class)).thenReturn(postUpdateDTO);
    when(this.postService.update(eq("01"), eq("02"), eq(postUpdateDTO), any(MockMultipartFile.class))).thenReturn(post);
    when(this.postMapper.toPostResponseDTO(post)).thenReturn(postResponseDTO);

    this.mockMvc.perform(multipart(HttpMethod.PATCH, BASE_URL + "/01")
      .file("data", jsonBody.getBytes())
      .file("image", mockFile.getBytes())
      .header("userId", "02")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Post atualizado com sucesso"))
      .andExpect(jsonPath("$.data.id").value(postResponseDTO.id()))
      .andExpect(jsonPath("$.data.title").value(postResponseDTO.title()))
      .andExpect(jsonPath("$.data.content").value(postResponseDTO.content()))
      .andExpect(jsonPath("$.data.ownerId").value(postResponseDTO.ownerId()))
      .andExpect(jsonPath("$.data.tags[0]").value(postResponseDTO.tags()[0]))
      .andExpect(jsonPath("$.data.tags[1]").value(postResponseDTO.tags()[1]))
      .andExpect(jsonPath("$.data.postImage").value(postResponseDTO.postImage()))
      .andExpect(jsonPath("$.data.createdAt").value(postResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(postResponseDTO.updatedAt().toString()));

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, PostUpdateDTO.class);
    verify(this.postService, times(1)).update(eq("01"), eq("02"), eq(postUpdateDTO), any(MockMultipartFile.class));
    verify(this.postMapper, times(1)).toPostResponseDTO(post);
  }

  @Test
  @DisplayName("update - Should return an error response with forbidden status code")
  void updateFailsByAccessDenied() throws Exception {
    PostUpdateDTO postUpdateDTO = new PostUpdateDTO(
      "Updated post",
      "Updated content",
      new String[]{"updated", "post"}
    );
    MockMultipartFile mockFile = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );
    String jsonBody = this.objectMapper.writeValueAsString(postUpdateDTO);

    when(this.uploadService.convertJsonStringToObject(jsonBody, PostUpdateDTO.class)).thenReturn(postUpdateDTO);
    when(this.postService.update(eq("01"), eq("01"), eq(postUpdateDTO), any(MockMultipartFile.class)))
      .thenThrow(new AccessDeniedException("Você não tem permissão para alterar este recurso"));

    this.mockMvc.perform(multipart(HttpMethod.PATCH, BASE_URL + "/01")
      .file("data", jsonBody.getBytes())
      .file("image", mockFile.getBytes())
      .header("userId", "01")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Você não tem permissão para alterar este recurso"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, PostUpdateDTO.class);
    verify(this.postService, times(1)).update(eq("01"), eq("01"), eq(postUpdateDTO), any(MockMultipartFile.class));
    verify(this.postMapper, never()).toPostResponseDTO(any(Post.class));
  }

  @Test
  @DisplayName("update - Should return an error response with not found status code")
  void updateFailsByPostNotFound() throws Exception {
    PostUpdateDTO postUpdateDTO = new PostUpdateDTO(
      "Updated post",
      "Updated content",
      new String[]{"updated", "post"}
    );
    MockMultipartFile mockFile = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );
    String jsonBody = this.objectMapper.writeValueAsString(postUpdateDTO);

    when(this.uploadService.convertJsonStringToObject(jsonBody, PostUpdateDTO.class)).thenReturn(postUpdateDTO);
    when(this.postService.update(eq("01"), eq("01"), eq(postUpdateDTO), any(MockMultipartFile.class)))
      .thenThrow(new RecordNotFoundException("Post de id: '01' não encontrado"));

    this.mockMvc.perform(multipart(HttpMethod.PATCH, BASE_URL + "/01")
        .file("data", jsonBody.getBytes())
        .file("image", mockFile.getBytes())
        .header("userId", "01")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Post de id: '01' não encontrado"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, PostUpdateDTO.class);
    verify(this.postService, times(1)).update(eq("01"), eq("01"), eq(postUpdateDTO), any(MockMultipartFile.class));
    verify(this.postMapper, never()).toPostResponseDTO(any(Post.class));
  }

  @Test
  @DisplayName("delete - Should return a success response with Ok status code and the deleted post")
  void deleteSuccess() throws Exception {
    Post post = this.mockData.getPosts().get(0);
    PostResponseDTO postResponseDTO = new PostResponseDTO(
      post.getId(),
      post.getTitle(),
      post.getContent(),
      post.getOwnerId(),
      post.getTags(),
      "http://localhost:8080/images/uploads/post/image.jpg",
      post.getCreatedAt(),
      post.getUpdatedAt()
    );

    when(this.postService.delete("01", "02")).thenReturn(post);
    when(this.postMapper.toPostResponseDTO(post)).thenReturn(postResponseDTO);

    this.mockMvc.perform(delete(BASE_URL + "/01")
        .header("userId", "02")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Post de id: '01' excluído com sucesso"))
      .andExpect(jsonPath("$.data.deletedPost.id").value(postResponseDTO.id()))
      .andExpect(jsonPath("$.data.deletedPost.title").value(postResponseDTO.title()))
      .andExpect(jsonPath("$.data.deletedPost.content").value(postResponseDTO.content()))
      .andExpect(jsonPath("$.data.deletedPost.ownerId").value(postResponseDTO.ownerId()))
      .andExpect(jsonPath("$.data.deletedPost.tags[0]").value(postResponseDTO.tags()[0]))
      .andExpect(jsonPath("$.data.deletedPost.tags[1]").value(postResponseDTO.tags()[1]))
      .andExpect(jsonPath("$.data.deletedPost.postImage").value(postResponseDTO.postImage()))
      .andExpect(jsonPath("$.data.deletedPost.createdAt").value(postResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.deletedPost.updatedAt").value(postResponseDTO.updatedAt().toString()));

    verify(this.postService, times(1)).delete("01", "02");
    verify(this.postMapper, times(1)).toPostResponseDTO(post);
  }

  @Test
  @DisplayName("delete - Should return an error response with forbidden status code")
  void deleteFailsByAccessDenied() throws Exception {
    when(this.postService.delete("01", "01"))
      .thenThrow(new AccessDeniedException("Você não tem permissão para remover este recurso"));

    this.mockMvc.perform(delete(BASE_URL + "/01")
      .header("userId", "01")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Você não tem permissão para remover este recurso"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.postService, times(1)).delete("01", "01");
    verify(this.postMapper, never()).toPostResponseDTO(any(Post.class));
  }

  @Test
  @DisplayName("delete - Should return an error response with not found status code")
  void deleteFailsByPostNotFound() throws Exception {
    when(this.postService.delete("01", "02"))
      .thenThrow(new RecordNotFoundException("Post de id: '01' não encontrado"));

    this.mockMvc.perform(delete(BASE_URL + "/01")
      .header("userId", "02")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Post de id: '01' não encontrado"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.postService, times(1)).delete("01", "02");
    verify(this.postMapper, never()).toPostResponseDTO(any(Post.class));
  }
}
