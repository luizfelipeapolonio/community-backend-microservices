package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.LikeDislike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeDislikeRepository extends JpaRepository<LikeDislike, Long> {
}
