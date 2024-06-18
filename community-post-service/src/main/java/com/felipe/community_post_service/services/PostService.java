package com.felipe.community_post_service.services;

import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.exceptions.RecordNotFoundException;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.PostRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Validated
@Service
public class PostService {

  private final PostRepository postRepository;
  private final UploadService uploadService;

  public PostService(PostRepository postRepository, UploadService uploadService) {
    this.postRepository = postRepository;
    this.uploadService = uploadService;
  }

  public Post create(String userId, @Valid PostCreateDTO postCreateDTO, MultipartFile image) {
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

  public Page<Post> getAllPosts(int pageNumber) {
    Pageable pagination = PageRequest.of(pageNumber, 10);
    return this.postRepository.findAll(pagination);
  }

  public Post getById(String postId) {
    return this.postRepository.findById(postId)
      .orElseThrow(() -> new RecordNotFoundException("Post de id: '" + postId + "' não encontrado"));
  }
}
