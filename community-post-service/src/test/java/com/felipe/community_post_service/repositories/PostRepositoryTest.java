package com.felipe.community_post_service.repositories;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.models.LikeDislike;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles(value = "test")
public class PostRepositoryTest {

  @Autowired
  EntityManager entityManager;

  @Autowired
  PostRepository postRepository;

  private final GenerateMocks mockData = new GenerateMocks();

  @Test
  @DisplayName("findAllByTitleOrTags - Should successfully return all posts containing in title the given query string")
  void findAllByTitleOrTagsLikeSearchingByTitle() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");
    post1.setTitle("Post 1 test");

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");
    post2.setTitle("Post 2 test");

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);

    Pageable pagination = PageRequest.of(0, 10);

    Page<Post> foundPostsPage = this.postRepository.findAllByTitleOrTagsLike("2 test", pagination);

    assertThat(foundPostsPage.getTotalPages()).isEqualTo(1);
    assertThat(foundPostsPage.getContent()).allSatisfy(post -> {
      assertThat(post.getId()).isEqualTo(post2.getId());
      assertThat(post.getTitle()).isEqualTo(post2.getTitle());
      assertThat(post.getContent()).isEqualTo(post2.getContent());
      assertThat(post.getTags()).isEqualTo(post2.getTags());
      assertThat(post.getOwnerId()).isEqualTo(post2.getOwnerId());
      assertThat(post.getPostImage()).isEqualTo(post2.getPostImage());
      assertThat(post.getCreatedAt()).isEqualTo(post2.getCreatedAt());
      assertThat(post.getUpdatedAt()).isEqualTo(post2.getUpdatedAt());
      assertThat(post.getComments().size()).isEqualTo(post2.getComments().size());
      assertThat(post.getLikeDislike().size()).isEqualTo(post2.getLikeDislike().size());
    });
  }

  @Test
  @DisplayName("findAllByTitleOrTagsLike - Should successfully return all posts containing in tags the given query string")
  void findAllByTitleOrTagsLikeSearchingByTags() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");
    post1.setTags(new String[]{"post", "java"});

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");
    post2.setTags(new String[]{"post", "golang"});

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);

    Pageable pagination = PageRequest.of(0, 10);

    Page<Post> foundPostsPage = this.postRepository.findAllByTitleOrTagsLike("java", pagination);

    assertThat(foundPostsPage.getTotalPages()).isEqualTo(1);
    assertThat(foundPostsPage.getContent()).allSatisfy(post -> {
      assertThat(post.getId()).isEqualTo(post1.getId());
      assertThat(post.getTitle()).isEqualTo(post1.getTitle());
      assertThat(post.getContent()).isEqualTo(post1.getContent());
      assertThat(post.getTags()).isEqualTo(post1.getTags());
      assertThat(post.getOwnerId()).isEqualTo(post1.getOwnerId());
      assertThat(post.getPostImage()).isEqualTo(post1.getPostImage());
      assertThat(post.getCreatedAt()).isEqualTo(post1.getCreatedAt());
      assertThat(post.getUpdatedAt()).isEqualTo(post1.getUpdatedAt());
      assertThat(post.getComments().size()).isEqualTo(post1.getComments().size());
      assertThat(post.getLikeDislike().size()).isEqualTo(post1.getLikeDislike().size());
    });
  }

  @Test
  @DisplayName("findAllUserLikedPosts - Should successfully return all user liked posts")
  void findAllUserLikedPostsSuccess() {
    Post post1 = this.mockData.getPosts().get(0);
    post1.setId("01");

    Post post2 = this.mockData.getPosts().get(1);
    post2.setId("02");

    Post post3 = this.mockData.getPosts().get(2);
    post3.setId("03");

    LikeDislike like1 = this.mockData.getLikesDislikes().get(0);
    like1.setId(null);
    like1.setPost(post1);
    like1.setUserId("01");

    LikeDislike like2 = this.mockData.getLikesDislikes().get(1);
    like2.setId(null);
    like2.setPost(post2);
    like2.setUserId("01");

    LikeDislike dislike1 = this.mockData.getLikesDislikes().get(2);
    dislike1.setId(null);
    dislike1.setPost(post3);
    dislike1.setUserId("01");

    post1.getLikeDislike().add(like1);
    post2.getLikeDislike().add(like2);
    post3.getLikeDislike().add(dislike1);

    this.entityManager.persist(post1);
    this.entityManager.persist(post2);
    this.entityManager.persist(post3);
    this.entityManager.persist(like1);
    this.entityManager.persist(like2);
    this.entityManager.persist(dislike1);

    Pageable pagination = PageRequest.of(0, 10);

    Page<Post> allUserLikedPosts = this.postRepository.findAllUserLikedPosts("01", pagination);

    assertThat(allUserLikedPosts.getTotalElements()).isEqualTo(2);
    assertThat(allUserLikedPosts.getContent()).allSatisfy(post -> {
      assertThat(post.getLikeDislike().stream().map(LikeDislike::getUserId).toList()).containsOnly("01");
      assertThat(post.getLikeDislike().stream().map(LikeDislike::getType).toList()).containsOnly("like");
    });
  }
}
