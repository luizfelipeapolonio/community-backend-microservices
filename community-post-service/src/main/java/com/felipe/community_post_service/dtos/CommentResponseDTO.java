package com.felipe.community_post_service.dtos;

import com.felipe.community_post_service.models.Comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
  String id,
  String content,
  String username,
  String profileImage,
  String userId,
  String postId,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
  public CommentResponseDTO(Comment comment) {
    this(
      comment.getId(),
      comment.getContent(),
      comment.getUsername(),
      comment.getProfileImage(),
      comment.getUserId(),
      comment.getPost().getId(),
      comment.getCreatedAt(),
      comment.getUpdatedAt()
    );
  }
}
