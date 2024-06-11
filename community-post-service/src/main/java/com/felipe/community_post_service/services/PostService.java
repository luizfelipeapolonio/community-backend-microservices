package com.felipe.community_post_service.services;

import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final UploadService uploadService;

  public PostService(PostRepository postRepository, UploadService uploadService) {
    this.postRepository = postRepository;
    this.uploadService = uploadService;
  }

  public Post create(String userId, PostCreateDTO postCreateDTO, MultipartFile image) {
    Post newPost = new Post();
    newPost.setTitle(postCreateDTO.title());
    newPost.setContent(postCreateDTO.content());
    newPost.setTags(postCreateDTO.tags()[0]);
    newPost.setUserId(userId);

    UploadDTO uploadDTO = new UploadDTO("post", newPost.getId());
    UploadResponseDTO uploadResponseDTO = this.uploadService.uploadImage(uploadDTO, image);
    newPost.setPostImage(uploadResponseDTO.path());

    return this.postRepository.save(newPost);
  }
}
