package com.felipe.community_post_service.dtos;

import java.util.List;

public record PostPageResponseDTO(List<PostResponseDTO> posts, long totalElements, int totalPages) {
}
