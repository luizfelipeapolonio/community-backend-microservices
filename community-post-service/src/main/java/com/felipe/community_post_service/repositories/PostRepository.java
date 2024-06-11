package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, String> {
}
