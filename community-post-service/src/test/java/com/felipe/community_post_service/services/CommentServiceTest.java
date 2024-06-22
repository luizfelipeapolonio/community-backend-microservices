package com.felipe.community_post_service.services;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.clients.UserClient;
import com.felipe.community_post_service.dtos.CommentCreateAndUpdateDTO;
import com.felipe.community_post_service.models.Comment;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

  @InjectMocks
  CommentService commentService;

  @Mock
  PostService postService;

  @Mock
  UserClient userClient;

  @Mock
  CommentRepository commentRepository;

  private GenerateMocks mockData;

  @BeforeEach
  void setUp() {
    this.mockData = new GenerateMocks();
  }

  @Test
  @DisplayName("insertComment - Should successfully insert a comment in a post")
  void insertCommentSuccess() {
    Post post = this.mockData.getPosts().get(0);

    Comment comment = this.mockData.getComments().get(0);
    comment.setPost(post);

    CommentCreateAndUpdateDTO commentDTO = new CommentCreateAndUpdateDTO("A great comment");

    Map<String, String> userInfos = new HashMap<>(2);
    userInfos.put("username", "User 1");
    userInfos.put("profileImage", "http://localhost:8080/images/uploads/user/image.jpg");

    when(this.postService.getById("01")).thenReturn(post);
    when(this.userClient.getUserInfos("02")).thenReturn(userInfos);
    when(this.commentRepository.save(any(Comment.class))).thenReturn(comment);

    Comment createdComment = this.commentService.insertComment("02", "01", commentDTO);

    assertThat(createdComment.getId()).isEqualTo(comment.getId());
    assertThat(createdComment.getContent()).isEqualTo(comment.getContent());
    assertThat(createdComment.getUsername()).isEqualTo(comment.getUsername());
    assertThat(createdComment.getUserId()).isEqualTo(comment.getUserId());
    assertThat(createdComment.getProfileImage()).isEqualTo(comment.getProfileImage());
    assertThat(createdComment.getPost().getId()).isEqualTo(comment.getPost().getId());
    assertThat(createdComment.getCreatedAt()).isEqualTo(comment.getCreatedAt());
    assertThat(createdComment.getUpdatedAt()).isEqualTo(comment.getUpdatedAt());

    verify(this.postService, times(1)).getById("01");
    verify(this.userClient, times(1)).getUserInfos("02");
    verify(this.commentRepository, times(1)).save(any(Comment.class));
  }
}
