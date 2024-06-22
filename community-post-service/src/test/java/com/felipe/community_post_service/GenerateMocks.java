package com.felipe.community_post_service;

import com.felipe.community_post_service.models.Comment;
import com.felipe.community_post_service.models.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GenerateMocks {

  private final List<Post> posts;
  private final List<Comment> comments;

  public GenerateMocks() {
    this.posts = this.generatePosts();
    this.comments = this.generateComments();
  }

  public List<Post> getPosts() {
    return this.posts;
  }

  public List<Comment> getComments() {
    return this.comments;
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

  private List<Comment> generateComments() {
    List<Comment> comments = new ArrayList<>();
    LocalDateTime mockDateTime = LocalDateTime.parse("2024-01-01T12:00:00.123456");
    Post post1 = this.posts.get(0);
    Post post2 = this.posts.get(1);

    Comment comment1 = new Comment();
    comment1.setId("01");
    comment1.setContent("A great comment");
    comment1.setUsername("User 1");
    comment1.setProfileImage("http://localhost:8080/images/uploads/user/image.jpg");
    comment1.setUserId("02");
    comment1.setCreatedAt(mockDateTime);
    comment1.setUpdatedAt(mockDateTime);
    comment1.setPost(post1);

    Comment comment2 = new Comment();
    comment2.setId("02");
    comment2.setContent("A great comment");
    comment2.setUsername("User 1");
    comment2.setProfileImage("http://localhost:8080/images/uploads/user/image.jpg");
    comment2.setUserId("02");
    comment2.setCreatedAt(mockDateTime);
    comment2.setUpdatedAt(mockDateTime);
    comment2.setPost(post1);

    Comment comment3 = new Comment();
    comment3.setId("02");
    comment3.setContent("A great comment");
    comment3.setUsername("User 2");
    comment3.setProfileImage("http://localhost:8080/images/uploads/user/image.jpg");
    comment3.setUserId("03");
    comment3.setCreatedAt(mockDateTime);
    comment3.setUpdatedAt(mockDateTime);
    comment3.setPost(post2);

    comments.add(comment1);
    comments.add(comment2);
    comments.add(comment3);

    return comments;
  }
}
