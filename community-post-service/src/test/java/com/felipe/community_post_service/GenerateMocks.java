package com.felipe.community_post_service;

import com.felipe.community_post_service.models.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GenerateMocks {

  private final List<Post> posts;

  public GenerateMocks() {
    this.posts = this.generatePosts();
  }

  public List<Post> getPosts() {
    return this.posts;
  }

  private List<Post> generatePosts() {
    List<Post> posts = new ArrayList<>();
    LocalDateTime mockDateTime = LocalDateTime.parse("2024-01-01T12:00:00.123456");

    Post post1 = new Post();
    post1.setId("01");
    post1.setTitle("Post 1");
    post1.setContent("A great content");
    post1.setPostImage("12345#post/image.jpg");
    post1.setTags(new String[]{"great", "post"});
    post1.setOwnerId("02");
    post1.setCreatedAt(mockDateTime);
    post1.setUpdatedAt(mockDateTime);

    Post post2 = new Post();
    post2.setId("02");
    post2.setTitle("Post 2");
    post2.setContent("A great content");
    post2.setPostImage("6789#post/image.jpg");
    post2.setTags(new String[]{"great", "post"});
    post2.setOwnerId("02");
    post2.setCreatedAt(mockDateTime);
    post2.setUpdatedAt(mockDateTime);

    posts.add(post1);
    posts.add(post2);
    return posts;
  }
}
