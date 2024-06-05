package com.felipe.communityuserservice.dtos;

import com.felipe.communityuserservice.models.User;

import java.time.LocalDateTime;

public record UserResponseDTO(
  String id,
  String name,
  String email,
  String bio,
  String profileImage,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
  public UserResponseDTO(User user) {
    this(
      user.getId(),
      user.getName(),
      user.getEmail(),
      user.getBio(),
      user.getProfileImage(),
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }
}
