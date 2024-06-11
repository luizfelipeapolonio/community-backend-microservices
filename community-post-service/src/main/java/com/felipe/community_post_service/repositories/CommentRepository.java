package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {
}
