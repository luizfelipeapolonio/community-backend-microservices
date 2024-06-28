package com.felipe.community_post_service.services;

import com.felipe.community_post_service.models.LikeDislike;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.LikeDislikeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeDislikeService {

  private final LikeDislikeRepository likeDislikeRepository;
  private final PostService postService;

  public LikeDislikeService(LikeDislikeRepository likeDislikeRepository, PostService postService) {
    this.likeDislikeRepository = likeDislikeRepository;
    this.postService = postService;
  }

  public Optional<LikeDislike> like(String postId, String userId) {
    Post post = this.postService.getById(postId);
    Optional<LikeDislike> givenLikeOrDislike = this.likeDislikeRepository.findByPostIdAndUserId(postId, userId);

    if(givenLikeOrDislike.isPresent()) {
      // Remove like or dislike if it exists
      this.likeDislikeRepository.deleteById(givenLikeOrDislike.get().getId());

      // If like has already been given just return after removing it
      if(givenLikeOrDislike.get().getType().equals("like")) {
        return Optional.empty();
      }
    }

    LikeDislike newLike = new LikeDislike();
    newLike.setType("like");
    newLike.setUserId(userId);
    newLike.setPost(post);

    LikeDislike like = this.likeDislikeRepository.save(newLike);
    return Optional.of(like);
  }

  public Optional<LikeDislike> dislike(String postId, String userId) {
    Post post = this.postService.getById(postId);
    Optional<LikeDislike> givenLikeOrDislike = this.likeDislikeRepository.findByPostIdAndUserId(postId, userId);

    if(givenLikeOrDislike.isPresent()) {
      this.likeDislikeRepository.deleteById(givenLikeOrDislike.get().getId());

      if(givenLikeOrDislike.get().getType().equals("dislike")) {
        return Optional.empty();
      }
    }

    LikeDislike newDislike = new LikeDislike();
    newDislike.setType("dislike");
    newDislike.setUserId(userId);
    newDislike.setPost(post);

    LikeDislike dislike = this.likeDislikeRepository.save(newDislike);
    return Optional.of(dislike);
  }

  public Optional<LikeDislike> checkLikeOrDislike(String postId, String userId) {
    return this.likeDislikeRepository.findByPostIdAndUserId(postId, userId);
  }
}
