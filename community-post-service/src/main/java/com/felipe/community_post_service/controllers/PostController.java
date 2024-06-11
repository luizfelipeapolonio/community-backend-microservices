package com.felipe.community_post_service.controllers;

import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.services.PostService;
import com.felipe.community_post_service.services.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;
  private final UploadService uploadService;

  public PostController(PostService postService, UploadService uploadService) {
    this.postService = postService;
    this.uploadService = uploadService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Post create(
    @RequestHeader("userId") String userId,
    @RequestPart("data") String jsonPostCreate,
    @RequestPart("image") MultipartFile image
  ) {
    PostCreateDTO postCreateDTO = this.uploadService.convertJsonStringToObject(jsonPostCreate, PostCreateDTO.class);
    return this.postService.create(userId, postCreateDTO, image);
  }
}
