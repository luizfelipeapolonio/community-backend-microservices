package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.models.LikeDislike;
import com.felipe.community_post_service.models.Post;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(value = "test")
public class LikeDislikeRepositoryTest {

  @Autowired
  EntityManager entityManager;

  @Autowired
  LikeDislikeRepository likeDislikeRepository;

  private final GenerateMocks mockData = new GenerateMocks();

  @Test
  @DisplayName("findByPostIdAndUserIdAndType - Should return an Optional of LikeDislike according to postId, userId and type")
  void findByPostIdAndUserIdAndTypeSuccess() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");

    LikeDislike like1 = this.mockData.getLikesDislikes().get(0);
    like1.setId(null);
    like1.setPost(post1);

    LikeDislike like2 = this.mockData.getLikesDislikes().get(1);
    like2.setId(null);
    like2.setPost(post1);

    LikeDislike dislike1 = this.mockData.getLikesDislikes().get(2);
    dislike1.setId(null);
    dislike1.setPost(post2);

    LikeDislike dislike2 = this.mockData.getLikesDislikes().get(3);
    dislike2.setId(null);
    dislike2.setPost(post2);

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);
    this.entityManager.persist(like1);
    this.entityManager.persist(like2);
    this.entityManager.persist(dislike1);
    this.entityManager.persist(dislike2);

    Optional<LikeDislike> likeDislike = this.likeDislikeRepository.findByPostIdAndUserIdAndType("01", "01", "like");

    assertThat(likeDislike.isPresent()).isTrue();
    assertThat(likeDislike.get().getId()).isEqualTo(like1.getId());
    assertThat(likeDislike.get().getType()).isEqualTo(like1.getType());
    assertThat(likeDislike.get().getUserId()).isEqualTo(like1.getUserId());
    assertThat(likeDislike.get().getPost().getId()).isEqualTo(like1.getPost().getId());
    assertThat(likeDislike.get().getGivenAt()).isEqualTo(like1.getGivenAt());
  }

  @Test
  @DisplayName("findByPostIdAndUserId - Should successfully return a like or dislike given the post id and the user id")
  void findByPostIdAndUserIdSuccess() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");

    LikeDislike like1 = this.mockData.getLikesDislikes().get(0);
    like1.setId(null);
    like1.setPost(post1);

    LikeDislike like2 = this.mockData.getLikesDislikes().get(1);
    like2.setId(null);
    like2.setPost(post1);

    LikeDislike dislike1 = this.mockData.getLikesDislikes().get(2);
    dislike1.setId(null);
    dislike1.setPost(post2);

    LikeDislike dislike2 = this.mockData.getLikesDislikes().get(3);
    dislike2.setId(null);
    dislike2.setPost(post2);

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);
    this.entityManager.persist(like1);
    this.entityManager.persist(like2);
    this.entityManager.persist(dislike1);
    this.entityManager.persist(dislike2);

    Optional<LikeDislike> likeDislike = this.likeDislikeRepository.findByPostIdAndUserId("01", "01");

    assertThat(likeDislike.isPresent()).isTrue();
    assertThat(likeDislike.get().getId()).isEqualTo(like1.getId());
    assertThat(likeDislike.get().getType()).isEqualTo(like1.getType());
    assertThat(likeDislike.get().getUserId()).isEqualTo(like1.getUserId());
    assertThat(likeDislike.get().getPost().getId()).isEqualTo(like1.getPost().getId());
    assertThat(likeDislike.get().getGivenAt()).isEqualTo(like1.getGivenAt());
  }
}
