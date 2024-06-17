package com.felipe.community_post_service.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Table(name = "post")
public class Post {

  @Id
  private String id;

  @Column(nullable = false, length = 80)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private String tags;

  @Column(nullable = false)
  private String ownerId;

  @Column(nullable = false)
  private String postImage;

  @CreationTimestamp
  @Column(name = "createdAt", columnDefinition = "TIMESTAMP(2)", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updatedAt", columnDefinition = "TIMESTAMP(2)", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "post_id", nullable = false)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "post_id", nullable = false)
  private List<LikeDislike> likeDislike = new ArrayList<>();

  public Post() {}

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String[] getTags() {
    return this.tags.split(" ");
  }

  public void setTags(String[] tags) {
    StringBuilder stringTags = new StringBuilder();
    Stream.of(tags).forEach(tag -> stringTags.append(tag).append(" "));
    this.tags = stringTags.toString().trim();
  }

  public String getOwnerId() {
    return this.ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getPostImage() {
    return this.postImage;
  }

  public void setPostImage(String postImage) {
    this.postImage = postImage;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<Comment> getComments() {
    return this.comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public List<LikeDislike> getLikeDislike() {
    return this.likeDislike;
  }

  public void setLikeDislike(List<LikeDislike> likeDislike) {
    this.likeDislike = likeDislike;
  }
}
