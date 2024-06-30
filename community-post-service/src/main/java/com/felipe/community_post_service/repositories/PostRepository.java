package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
  Page<Post> findAllByOwnerId(String ownerId, Pageable pagination);
  List<Post> findAllByOwnerId(String ownerId);

  @Query(value = "SELECT p FROM Post p WHERE p.title LIKE %:query% OR p.tags LIKE %:query%")
  Page<Post> findAllByTitleOrTagsLike(@Param("query") String query, Pageable pageable);
}
