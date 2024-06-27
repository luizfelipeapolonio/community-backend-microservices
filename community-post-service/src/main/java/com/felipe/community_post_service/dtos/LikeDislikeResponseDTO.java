package com.felipe.community_post_service.dtos;

import com.felipe.community_post_service.models.LikeDislike;

import java.time.LocalDateTime;

public record LikeDislikeResponseDTO(
  long id,
  String type,
  String postId,
  String userId,
  LocalDateTime givenAt
) {
  public LikeDislikeResponseDTO(LikeDislike likeDislike) {
    this(
      likeDislike.getId(),
      likeDislike.getType(),
      likeDislike.getPost().getId(),
      likeDislike.getUserId(),
      likeDislike.getGivenAt()
    );
  }
}
