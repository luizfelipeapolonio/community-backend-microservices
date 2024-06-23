package com.felipe.community_post_service.services;

import com.felipe.community_post_service.clients.UserClient;
import com.felipe.community_post_service.dtos.CommentCreateAndUpdateDTO;
import com.felipe.community_post_service.models.Comment;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostService postService;
  private final UserClient userClient;

  public CommentService(CommentRepository commentRepository, PostService postService, UserClient userClient) {
    this.commentRepository = commentRepository;
    this.postService = postService;
    this.userClient = userClient;
  }

  public Comment insertComment(String userId, String postId, CommentCreateAndUpdateDTO commentDTO) {
    Post post = this.postService.getById(postId);
    Map<String, String> userInfos = this.userClient.getUserInfos(userId);

    Comment newComment = new Comment();
    newComment.setUserId(userId);
    newComment.setUsername(userInfos.get("username"));
    newComment.setProfileImage(userInfos.get("profileImage"));
    newComment.setContent(commentDTO.content());
    newComment.setPost(post);

    return this.commentRepository.save(newComment);
  }

  public Page<Comment> getAllPostComments(String postId, int pageNumber) {
    Post foundPost = this.postService.getById(postId);
    Pageable pagination = PageRequest.of(pageNumber, 10);
    return this.commentRepository.findAllByPostId(foundPost.getId(), pagination);
  }
}
