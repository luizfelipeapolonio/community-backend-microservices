package com.felipe.community_post_service.services;

import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final UploadService uploadService;

  public PostService(PostRepository postRepository, UploadService uploadService) {
    this.postRepository = postRepository;
    this.uploadService = uploadService;
  }

  public Post create(String userId, PostCreateDTO postCreateDTO, MultipartFile image) {
    String postId = UUID.randomUUID().toString();
    UploadDTO uploadDTO = new UploadDTO("post", postId);
    UploadResponseDTO uploadResponseDTO = this.uploadService.uploadImage(uploadDTO, image);

    Post newPost = new Post();
    newPost.setId(postId);
    newPost.setTitle(postCreateDTO.title());
    newPost.setContent(postCreateDTO.content());
    newPost.setTags(postCreateDTO.tags());
    newPost.setOwnerId(userId);
    newPost.setPostImage(uploadResponseDTO.id() + "#" + uploadResponseDTO.path());

    return this.postRepository.save(newPost);
  }
}