package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.models.LikeDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeDislikeRepository extends JpaRepository<LikeDislike, Long> {

  @Query(value = "SELECT ld FROM LikeDislike ld WHERE ld.post.id = :postId AND ld.userId = :userId AND ld.type = :type")
  Optional<LikeDislike> findByPostIdAndUserIdAndType(
    @Param("postId") String postId,
    @Param("userId") String userId,
    @Param("type") String type
  );

  @Query(value = "SELECT ld FROM LikeDislike ld WHERE ld.post.id = :postId AND ld.userId = :userId")
  Optional<LikeDislike> findByPostIdAndUserId(@Param("postId") String postId, @Param("userId") String userId);

  List<LikeDislike> findAllByPostId(String postId);
}
