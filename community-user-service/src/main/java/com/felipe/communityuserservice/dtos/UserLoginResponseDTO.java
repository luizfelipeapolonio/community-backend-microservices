package com.felipe.communityuserservice.dtos;

public record UserLoginResponseDTO(UserResponseDTO userInfo, String token) {
}
