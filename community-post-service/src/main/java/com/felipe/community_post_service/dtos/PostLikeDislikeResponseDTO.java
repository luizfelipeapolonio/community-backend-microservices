package com.felipe.community_post_service.dtos;

public record PostLikeDislikeResponseDTO(
  boolean isLikedOrDisliked,
  String type,
  int likes,
  int dislikes
) {}
