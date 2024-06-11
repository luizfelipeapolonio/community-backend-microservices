package com.felipe.community_post_service.dtos;

import java.time.LocalDateTime;

public record UploadResponseDTO(
  String id,
  String name,
  String path,
  long size,
  String postId,
  String userId,
  LocalDateTime createdAt
) {}
