package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.models.Comment;
import com.felipe.community_post_service.models.Post;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(value = "test")
public class CommentRepositoryTest {

  @Autowired
  EntityManager entityManager;

  @Autowired
  CommentRepository commentRepository;

  private final GenerateMocks mockData = new GenerateMocks();

  @Test
  @DisplayName("findAllByPostId - Should successfully return all comments from a post as a Page")
  void findAllByPostIdSuccess() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");

    Comment comment1 = this.mockData.getComments().get(0);
    comment1.setId(null);
    comment1.setPost(post1);

    Comment comment2 = this.mockData.getComments().get(1);
    comment2.setId(null);
    comment2.setPost(post1);

    Comment comment3 = this.mockData.getComments().get(2);
    comment3.setId(null);
    comment3.setPost(post2);

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);
    this.entityManager.persist(comment1);
    this.entityManager.persist(comment2);
    this.entityManager.persist(comment3);

    Pageable pagination = PageRequest.of(0, 10);

    Page<Comment> allCommentsPage = this.commentRepository.findAllByPostId(post1.getId(), pagination);

    assertThat(allCommentsPage.getContent())
      .allSatisfy(comment -> assertThat(comment.getPost().getId()).isEqualTo(post1.getId()))
      .hasSize(2);
  }

  @Test
  @DisplayName("findByIdAndPostId - Should successfully return a comment from a post")
  void findByIdAndPostIdSuccess() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");

    Comment comment1 = this.mockData.getComments().get(0);
    comment1.setId(null);
    comment1.setPost(post1);

    Comment comment2 = this.mockData.getComments().get(1);
    comment2.setId(null);
    comment2.setPost(post1);

    Comment comment3 = this.mockData.getComments().get(2);
    comment3.setId(null);
    comment3.setPost(post2);

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);
    this.entityManager.persist(comment1);
    this.entityManager.persist(comment2);
    this.entityManager.persist(comment3);

    Optional<Comment> foundComment = this.commentRepository.findByIdAndPostId(comment1.getId(), post1.getId());

    assertThat(foundComment.isPresent()).isTrue();
    assertThat(foundComment.get().getId()).isEqualTo(comment1.getId());
    assertThat(foundComment.get().getContent()).isEqualTo(comment1.getContent());
    assertThat(foundComment.get().getUsername()).isEqualTo(comment1.getUsername());
    assertThat(foundComment.get().getUserId()).isEqualTo(comment1.getUserId());
    assertThat(foundComment.get().getProfileImage()).isEqualTo(comment1.getProfileImage());
    assertThat(foundComment.get().getPost().getId()).isEqualTo(post1.getId());
    assertThat(foundComment.get().getCreatedAt()).isEqualTo(comment1.getCreatedAt());
    assertThat(foundComment.get().getUpdatedAt()).isEqualTo(comment1.getUpdatedAt());
  }
}
