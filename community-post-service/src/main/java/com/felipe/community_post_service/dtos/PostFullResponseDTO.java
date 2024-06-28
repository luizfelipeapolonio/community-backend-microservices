package com.felipe.community_post_service.dtos;

public record PostFullResponseDTO(
  PostResponseDTO post,
  CommentPageResponseDTO postComments,
  PostLikeDislikeResponseDTO likeDislike
) {}
