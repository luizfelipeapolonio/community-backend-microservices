package com.felipe.community_post_service.controllers;

import com.felipe.community_post_service.dtos.CommentCreateAndUpdateDTO;
import com.felipe.community_post_service.dtos.CommentResponseDTO;
import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.PostPageResponseDTO;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.dtos.PostUpdateDTO;
import com.felipe.community_post_service.dtos.mappers.PostMapper;
import com.felipe.community_post_service.models.Comment;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.services.CommentService;
import com.felipe.community_post_service.services.PostService;
import com.felipe.community_post_service.services.UploadService;
import com.felipe.community_post_service.util.response.CustomResponseBody;
import com.felipe.community_post_service.util.response.ResponseConditionStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;
  private final UploadService uploadService;
  private final PostMapper postMapper;
  private final CommentService commentService;

  public PostController(PostService postService, UploadService uploadService, PostMapper postMapper, CommentService commentService) {
    this.postService = postService;
    this.uploadService = uploadService;
    this.postMapper = postMapper;
    this.commentService = commentService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomResponseBody<PostResponseDTO> create(
    @RequestHeader("userId") String userId,
    @RequestPart("data") String jsonPostCreate,
    @RequestPart("image") MultipartFile image
  ) {
    PostCreateDTO postCreateDTO = this.uploadService.convertJsonStringToObject(jsonPostCreate, PostCreateDTO.class);
    Post createdPost = this.postService.create(userId, postCreateDTO, image);
    PostResponseDTO postResponseDTO = this.postMapper.toPostResponseDTO(createdPost);

    CustomResponseBody<PostResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.CREATED);
    response.setMessage("Post criado com sucesso");
    response.setData(postResponseDTO);
    return response;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<PostPageResponseDTO> getAllPosts(@RequestParam(defaultValue = "0") int page) {
    Page<Post> postPage = this.postService.getAllPosts(page);
    List<PostResponseDTO> postsDTO = postPage.getContent()
      .stream()
      .map(this.postMapper::toPostResponseDTO)
      .toList();
    PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO(
      postsDTO,
      postPage.getTotalElements(),
      postPage.getTotalPages()
    );

    CustomResponseBody<PostPageResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Todos os posts");
    response.setData(postPageResponseDTO);
    return response;
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<Map<String, List<PostResponseDTO>>> deleteAllFromUser(@RequestHeader("userId") String userId) {
    List<Post> deletedPosts = this.postService.deleteAllFromUser(userId);
    List<PostResponseDTO> postResponseDTOs = deletedPosts.stream().map(this.postMapper::toPostResponseDTO).toList();

    Map<String, List<PostResponseDTO>> deletedPostsMap = new HashMap<>(1);
    deletedPostsMap.put("deletedPosts", postResponseDTOs);

    CustomResponseBody<Map<String, List<PostResponseDTO>>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Todos os posts do usuário de id: '" + userId + "' foram excluídos com sucesso");
    response.setData(deletedPostsMap);
    return response;
  }

  @GetMapping("/{postId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<PostResponseDTO> getById(@PathVariable String postId) {
    Post foundPost = this.postService.getById(postId);
    PostResponseDTO postResponseDTO = this.postMapper.toPostResponseDTO(foundPost);

    CustomResponseBody<PostResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Post de id: '" + postId + "' encontrado");
    response.setData(postResponseDTO);
    return response;
  }

  @PatchMapping("/{postId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<PostResponseDTO> update(
    @PathVariable String postId,
    @RequestHeader("userId") String userId,
    @RequestPart("data") String jsonUpdateDTO,
    @RequestPart(name = "image", required = false) MultipartFile image
  ) {
    PostUpdateDTO postUpdateDTO = this.uploadService.convertJsonStringToObject(jsonUpdateDTO, PostUpdateDTO.class);
    Post updatedPost = this.postService.update(postId, userId, postUpdateDTO, image);
    PostResponseDTO postResponseDTO = this.postMapper.toPostResponseDTO(updatedPost);

    CustomResponseBody<PostResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Post atualizado com sucesso");
    response.setData(postResponseDTO);
    return response;
  }

  @DeleteMapping("/{postId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<Map<String, PostResponseDTO>> delete(
    @PathVariable String postId,
    @RequestHeader("userId") String userId
  ) {
    Post deletedPost = this.postService.delete(postId, userId);
    PostResponseDTO postResponseDTO = this.postMapper.toPostResponseDTO(deletedPost);

    Map<String, PostResponseDTO> deletedPostMap = new HashMap<>(1);
    deletedPostMap.put("deletedPost", postResponseDTO);

    CustomResponseBody<Map<String, PostResponseDTO>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Post de id: '" + postId + "' excluído com sucesso");
    response.setData(deletedPostMap);
    return response;
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<PostPageResponseDTO> getAllUserPosts(
    @PathVariable String userId,
    @RequestParam(defaultValue = "0") int page
  ) {
    Page<Post> allPosts = this.postService.getAllUserPosts(userId, page);
    List<PostResponseDTO> postsResponseDTO = allPosts.getContent()
      .stream()
      .map(this.postMapper::toPostResponseDTO)
      .toList();
    PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO(
      postsResponseDTO,
      allPosts.getTotalElements(),
      allPosts.getTotalPages()
    );

    CustomResponseBody<PostPageResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Todos os posts do usuário de id: '" + userId + "'");
    response.setData(postPageResponseDTO);
    return response;
  }

  @PostMapping("/{postId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomResponseBody<CommentResponseDTO> insertComment(
    @RequestHeader("userId") String userId,
    @PathVariable String postId,
    @RequestBody @Valid CommentCreateAndUpdateDTO commentDTO
  ) {
    Comment comment = this.commentService.insertComment(userId, postId, commentDTO);
    CommentResponseDTO commentResponseDTO = new CommentResponseDTO(comment);

    CustomResponseBody<CommentResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.CREATED);
    response.setMessage("Comentário inserido com sucesso no post de id: '" + postId +"'");
    response.setData(commentResponseDTO);
    return response;
  }
}
