package com.felipe.community_post_service.dtos;

import java.util.List;

public record CommentPageResponseDTO(List<CommentResponseDTO> comments, long totalElements, int totalPages) {
}
