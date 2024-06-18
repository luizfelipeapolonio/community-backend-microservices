package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, String> {
  Page<Post> findAllByOwnerId(String ownerId, Pageable pagination);
}
