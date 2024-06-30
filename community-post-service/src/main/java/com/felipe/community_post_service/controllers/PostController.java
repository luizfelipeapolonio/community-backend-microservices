package com.felipe.community_post_service.controllers;

import com.felipe.community_post_service.dtos.CommentCreateAndUpdateDTO;
import com.felipe.community_post_service.dtos.CommentPageResponseDTO;
import com.felipe.community_post_service.dtos.CommentResponseDTO;
import com.felipe.community_post_service.dtos.LikeDislikeResponseDTO;
import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.PostFullResponseDTO;
import com.felipe.community_post_service.dtos.PostLikeDislikeResponseDTO;
import com.felipe.community_post_service.dtos.PostPageResponseDTO;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.dtos.PostUpdateDTO;
import com.felipe.community_post_service.dtos.mappers.PostMapper;
import com.felipe.community_post_service.models.Comment;
import com.felipe.community_post_service.models.LikeDislike;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.services.CommentService;
import com.felipe.community_post_service.services.LikeDislikeService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;
  private final UploadService uploadService;
  private final PostMapper postMapper;
  private final CommentService commentService;
  private final LikeDislikeService likeDislikeService;

  public PostController(
    PostService postService,
    UploadService uploadService,
    PostMapper postMapper,
    CommentService commentService,
    LikeDislikeService likeDislikeService
  ) {
    this.postService = postService;
    this.uploadService = uploadService;
    this.postMapper = postMapper;
    this.commentService = commentService;
    this.likeDislikeService = likeDislikeService;
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
  public CustomResponseBody<PostPageResponseDTO> getAllPosts(
    @RequestParam(name = "q", required = false) String query,
    @RequestParam(defaultValue = "0") int page
  ) {
    Page<Post> postPage = this.postService.getAllPosts(query, page);
    List<PostResponseDTO> postsDTO = postPage.getContent()
      .stream()
      .map(this.postMapper::toPostResponseDTO)
      .toList();
    PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO(
      postsDTO,
      postPage.getTotalElements(),
      postPage.getTotalPages()
    );
    String message = query == null ? "Todos os posts" : "Todos os posts que contém '" + query + "' no título ou tags";

    CustomResponseBody<PostPageResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage(message);
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
  public CustomResponseBody<PostFullResponseDTO> getById(
    @PathVariable String postId,
    @RequestHeader("userId") String userId
  ) {
    Post foundPost = this.postService.getById(postId);
    Page<Comment> allCommentsPage = this.commentService.getAllPostComments(foundPost.getId(), 0);
    List<CommentResponseDTO> commentResponseDTOs = allCommentsPage.getContent()
      .stream()
      .map(CommentResponseDTO::new)
      .toList();
    CommentPageResponseDTO commentPageResponseDTO = new CommentPageResponseDTO(
      commentResponseDTOs,
      allCommentsPage.getTotalElements(),
      allCommentsPage.getTotalPages()
    );
    PostResponseDTO postResponseDTO = this.postMapper.toPostResponseDTO(foundPost);
    Optional<LikeDislike> likeOrDislike = this.likeDislikeService.checkLikeOrDislike(postId, userId);
    PostLikeDislikeResponseDTO postLikeDislikeDTO = new PostLikeDislikeResponseDTO(
      likeOrDislike.isPresent(),
      likeOrDislike.map(LikeDislike::getType).orElse(null)
    );
    PostFullResponseDTO postFullResponseDTO = new PostFullResponseDTO(postResponseDTO, commentPageResponseDTO, postLikeDislikeDTO);

    CustomResponseBody<PostFullResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Post de id: '" + postId + "' encontrado");
    response.setData(postFullResponseDTO);
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

  @GetMapping("/{postId}/comments")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<CommentPageResponseDTO> getAllPostComments(
    @PathVariable String postId,
    @RequestParam(defaultValue = "0") int page
  ) {
    Page<Comment> allCommentsPage = this.commentService.getAllPostComments(postId, page);
    List<CommentResponseDTO> commentResponseDTOs = allCommentsPage.getContent()
      .stream()
      .map(CommentResponseDTO::new)
      .toList();
    CommentPageResponseDTO commentPageResponseDTO = new CommentPageResponseDTO(
      commentResponseDTOs,
      allCommentsPage.getTotalElements(),
      allCommentsPage.getTotalPages()
    );

    CustomResponseBody<CommentPageResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Todos os comentários do post de id: '" + postId + "'");
    response.setData(commentPageResponseDTO);
    return response;
  }

  @PatchMapping("/{postId}/comments/{commentId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<CommentResponseDTO> edit(
    @RequestHeader("userId") String userId,
    @PathVariable String postId,
    @PathVariable String commentId,
    @RequestBody @Valid CommentCreateAndUpdateDTO commentDTO
  ) {
    Comment editedComment = this.commentService.edit(postId, commentId, userId, commentDTO);
    CommentResponseDTO commentResponseDTO = new CommentResponseDTO(editedComment);

    CustomResponseBody<CommentResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Comentário editado com sucesso");
    response.setData(commentResponseDTO);
    return response;
  }

  @DeleteMapping("/{postId}/comments/{commentId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<Map<String, CommentResponseDTO>> deleteComment(
    @RequestHeader("userId") String userId,
    @PathVariable String postId,
    @PathVariable String commentId
  ) {
    Comment deletedComment = this.commentService.delete(postId, commentId, userId);
    CommentResponseDTO commentResponseDTO = new CommentResponseDTO(deletedComment);

    Map<String, CommentResponseDTO> deletedCommentMap = new HashMap<>();
    deletedCommentMap.put("deletedComment", commentResponseDTO);

    CustomResponseBody<Map<String, CommentResponseDTO>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Comentário excluído com sucesso");
    response.setData(deletedCommentMap);
    return response;
  }

  @PatchMapping("/{postId}/like")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<LikeDislikeResponseDTO> like(
    @RequestHeader("userId") String userId,
    @PathVariable String postId
  ) {
    Optional<LikeDislike> like = likeDislikeService.like(postId, userId);
    LikeDislikeResponseDTO likeDTO = null;

    if(like.isPresent()) {
      likeDTO = new LikeDislikeResponseDTO(like.get());
    }

    CustomResponseBody<LikeDislikeResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage(like.isPresent() ? "Like inserido com sucesso" : "Like removido com sucesso");
    response.setData(likeDTO);
    return response;
  }

  @PatchMapping("/{postId}/dislike")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<LikeDislikeResponseDTO> dislike(
    @RequestHeader("userId") String userId,
    @PathVariable String postId
  ) {
    Optional<LikeDislike> dislike = this.likeDislikeService.dislike(postId, userId);
    LikeDislikeResponseDTO dislikeDTO = null;

    if(dislike.isPresent()) {
      dislikeDTO = new LikeDislikeResponseDTO(dislike.get());
    }

    CustomResponseBody<LikeDislikeResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage(dislike.isPresent() ? "Dislike inserido com sucesso" : "Dislike removido com sucesso");
    response.setData(dislikeDTO);
    return response;
  }
}
