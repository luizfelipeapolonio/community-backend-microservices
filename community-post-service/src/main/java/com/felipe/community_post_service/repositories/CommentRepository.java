package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, String> {

  @Query(value = "SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
  Page<Comment> findAllByPostId(@Param("postId") String postId, Pageable pageable);
}
