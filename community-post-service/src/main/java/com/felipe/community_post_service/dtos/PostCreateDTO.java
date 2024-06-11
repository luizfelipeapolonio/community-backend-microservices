package com.felipe.community_post_service.dtos;

public record PostCreateDTO(String title, String content, String[] tags) {
}
