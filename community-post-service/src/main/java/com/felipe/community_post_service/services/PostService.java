package com.felipe.community_post_service.services;

import com.felipe.community_post_service.dtos.PostCreateDTO;
import com.felipe.community_post_service.dtos.PostUpdateDTO;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.exceptions.AccessDeniedException;
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

import java.util.List;
import java.util.Optional;
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

  public Page<Post> getAllPosts(String query, int pageNumber) {
    Pageable pagination = PageRequest.of(pageNumber, 10);
    if(query != null) {
      return this.postRepository.findAllByTitleOrTagsLike(query, pagination);
    }
    return this.postRepository.findAll(pagination);
  }

  public Post getById(String postId) {
    return this.postRepository.findById(postId)
      .orElseThrow(() -> new RecordNotFoundException("Post de id: '" + postId + "' não encontrado"));
  }

  public Page<Post> getAllUserPosts(String userId, int pageNumber) {
    Pageable pagination = PageRequest.of(pageNumber, 10);
    return this.postRepository.findAllByOwnerId(userId, pagination);
  }

  public Post update(
    String postId,
    String userId,
    @Valid PostUpdateDTO postUpdateDTO,
    MultipartFile image
  ) {
    return this.postRepository.findById(postId)
      .map(foundPost -> {
        if(!foundPost.getOwnerId().equals(userId)) {
          throw new AccessDeniedException("Você não tem permissão para alterar este recurso");
        }
        if(postUpdateDTO.title() != null) {
          foundPost.setTitle(postUpdateDTO.title());
        }
        if(postUpdateDTO.content() != null) {
          foundPost.setContent(postUpdateDTO.content());
        }
        if(postUpdateDTO.tags() != null) {
          foundPost.setTags(postUpdateDTO.tags());
        }
        if(image != null && !image.isEmpty()) {
          this.uploadService.deleteImage(foundPost.getPostImage());
          foundPost.setPostImage(null);

          UploadDTO uploadDTO = new UploadDTO("post", foundPost.getId());
          UploadResponseDTO uploadResponseDTO = this.uploadService.uploadImage(uploadDTO, image);
          foundPost.setPostImage(uploadResponseDTO.id() + "#" + uploadResponseDTO.path());
        }
        return this.postRepository.save(foundPost);
      })
      .orElseThrow(() -> new RecordNotFoundException("Post de id: '" + postId + "' não encontrado"));
  }

  public Post delete(String postId, String userId) {
    Post foundPost = this.getById(postId);

    if(!foundPost.getOwnerId().equals(userId)) {
      throw new AccessDeniedException("Você não tem permissão para remover este recurso");
    }

    this.postRepository.deleteById(foundPost.getId());
    this.uploadService.deleteImage(foundPost.getPostImage());
    return foundPost;
  }

  public List<Post> deleteAllFromUser(String userId) {
    List<Post> allUserPosts = this.postRepository.findAllByOwnerId(userId);
    allUserPosts.forEach(post -> {
      this.postRepository.deleteById(post.getId());
      this.uploadService.deleteImage(post.getPostImage());
    });
    return allUserPosts;
  }

}
