package com.felipe.community_post_service.controllers;

import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.PostPageResponseDTO;
import com.felipe.community_post_service.dtos.PostResponseDTO;
import com.felipe.community_post_service.dtos.mappers.PostMapper;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.services.PostService;
import com.felipe.community_post_service.services.UploadService;
import com.felipe.community_post_service.util.response.CustomResponseBody;
import com.felipe.community_post_service.util.response.ResponseConditionStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;
  private final UploadService uploadService;
  private final PostMapper postMapper;

  public PostController(PostService postService, UploadService uploadService, PostMapper postMapper) {
    this.postService = postService;
    this.uploadService = uploadService;
    this.postMapper = postMapper;
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
}
