package com.felipe.community_post_service.dtos;

import com.felipe.community_post_service.models.Post;

import java.time.LocalDateTime;

public record PostResponseDTO(
  String id,
  String title,
  String content,
  String ownerId,
  String[] tags,
  String postImage,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
  public PostResponseDTO(Post post) {
    this(
      post.getId(),
      post.getTitle(),
      post.getContent(),
      post.getOwnerId(),
      post.getTags(),
      post.getPostImage(),
      post.getCreatedAt(),
      post.getUpdatedAt()
    );
  }
}
