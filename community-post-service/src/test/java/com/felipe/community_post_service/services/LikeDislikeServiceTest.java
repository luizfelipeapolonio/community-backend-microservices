package com.felipe.community_post_service.services;

import com.felipe.community_post_service.GenerateMocks;
import com.felipe.community_post_service.models.LikeDislike;
import com.felipe.community_post_service.models.Post;
import com.felipe.community_post_service.repositories.LikeDislikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LikeDislikeServiceTest {

  @InjectMocks
  LikeDislikeService likeDislikeService;

  @Mock
  LikeDislikeRepository likeDislikeRepository;

  @Mock
  PostService postService;

  private GenerateMocks mockData;

  @BeforeEach
  void setUp() {
    this.mockData = new GenerateMocks();
  }

  @Test
  @DisplayName("like - Should successfully like a post that had never been liked before")
  void likeWithoutLikeOrDislikeGivenBefore() {
    Post post = this.mockData.getPosts().get(0);
    LikeDislike like = this.mockData.getLikesDislikes().get(0);

    when(this.postService.getById("01")).thenReturn(post);
    when(this.likeDislikeRepository.findByPostIdAndUserId("01", "01")).thenReturn(Optional.empty());
    when(this.likeDislikeRepository.save(any(LikeDislike.class))).thenReturn(like);

    Optional<LikeDislike> givenLike = this.likeDislikeService.like("01", "01");

    assertThat(givenLike.isPresent()).isTrue();
    assertThat(givenLike.get().getId()).isEqualTo(like.getId());
    assertThat(givenLike.get().getType()).isEqualTo(like.getType());
    assertThat(givenLike.get().getUserId()).isEqualTo(like.getUserId());
    assertThat(givenLike.get().getPost().getId()).isEqualTo(like.getPost().getId());
    assertThat(givenLike.get().getGivenAt()).isEqualTo(like.getGivenAt());

    verify(this.postService, times(1)).getById("01");
    verify(this.likeDislikeRepository, times(1)).findByPostIdAndUserId("01", "01");
    verify(this.likeDislikeRepository, times(1)).save(any(LikeDislike.class));
    verify(this.likeDislikeRepository, never()).deleteById(anyLong());
  }

  @Test
  @DisplayName("like - Should successfully remove the dislike and give the like in a post")
  void likeRemovingGivenDislike() {
    Post post = this.mockData.getPosts().get(0);
    LikeDislike like = this.mockData.getLikesDislikes().get(0);
    LikeDislike dislike = this.mockData.getLikesDislikes().get(2);

    when(this.postService.getById("01")).thenReturn(post);
    when(this.likeDislikeRepository.findByPostIdAndUserId("01", "01")).thenReturn(Optional.of(dislike));
    doNothing().when(this.likeDislikeRepository).deleteById(dislike.getId());
    when(this.likeDislikeRepository.save(any(LikeDislike.class))).thenReturn(like);

    Optional<LikeDislike> givenLike = this.likeDislikeService.like("01", "01");

    assertThat(givenLike.isPresent()).isTrue();
    assertThat(givenLike.get().getId()).isEqualTo(like.getId());
    assertThat(givenLike.get().getType()).isEqualTo("like");
    assertThat(givenLike.get().getUserId()).isEqualTo(like.getUserId());
    assertThat(givenLike.get().getPost().getId()).isEqualTo(like.getPost().getId());
    assertThat(givenLike.get().getGivenAt()).isEqualTo(like.getGivenAt());

    verify(this.postService, times(1)).getById("01");
    verify(this.likeDislikeRepository, times(1)).findByPostIdAndUserId("01", "01");
    verify(this.likeDislikeRepository, times(1)).deleteById(dislike.getId());
    verify(this.likeDislikeRepository, times(1)).save(any(LikeDislike.class));
  }

  @Test
  @DisplayName("like - Should successfully remove the like if it was given")
  void likeRemovingIfItsGiven() {
    Post post = this.mockData.getPosts().get(0);
    LikeDislike like = this.mockData.getLikesDislikes().get(0);

    when(this.postService.getById("01")).thenReturn(post);
    when(this.likeDislikeRepository.findByPostIdAndUserId("01", "01")).thenReturn(Optional.of(like));
    doNothing().when(this.likeDislikeRepository).deleteById(like.getId());

    Optional<LikeDislike> givenLike = this.likeDislikeService.like("01", "01");

    assertThat(givenLike.isEmpty()).isTrue();

    verify(this.postService, times(1)).getById("01");
    verify(this.likeDislikeRepository, times(1)).findByPostIdAndUserId("01", "01");
    verify(this.likeDislikeRepository, times(1)).deleteById(like.getId());
    verify(this.likeDislikeRepository, never()).save(any(LikeDislike.class));
  }

  @Test
  @DisplayName("dislike - Should successfully dislike a post that had never been disliked before")
  void dislikeWithoutLikeOrDislikeGivenBefore() {
    Post post = this.mockData.getPosts().get(1);
    LikeDislike dislike = this.mockData.getLikesDislikes().get(2);

    when(this.postService.getById("02")).thenReturn(post);
    when(this.likeDislikeRepository.findByPostIdAndUserId("02", "01")).thenReturn(Optional.empty());
    when(this.likeDislikeRepository.save(any(LikeDislike.class))).thenReturn(dislike);

    Optional<LikeDislike> givenDislike = this.likeDislikeService.dislike("02", "01");

    assertThat(givenDislike.isPresent()).isTrue();
    assertThat(givenDislike.get().getId()).isEqualTo(dislike.getId());
    assertThat(givenDislike.get().getType()).isEqualTo(dislike.getType());
    assertThat(givenDislike.get().getUserId()).isEqualTo(dislike.getUserId());
    assertThat(givenDislike.get().getPost().getId()).isEqualTo(dislike.getPost().getId());
    assertThat(givenDislike.get().getGivenAt()).isEqualTo(dislike.getGivenAt());

    verify(this.postService, times(1)).getById("02");
    verify(this.likeDislikeRepository, times(1)).findByPostIdAndUserId("02", "01");
    verify(this.likeDislikeRepository, times(1)).save(any(LikeDislike.class));
    verify(this.likeDislikeRepository, never()).deleteById(anyLong());
  }

  @Test
  @DisplayName("dislike - Should successfully remove the like and give the dislike in a post")
  void dislikeRemovingGivenDislike() {
    Post post = this.mockData.getPosts().get(1);
    LikeDislike like = this.mockData.getLikesDislikes().get(0);
    LikeDislike dislike = this.mockData.getLikesDislikes().get(2);

    when(this.postService.getById("02")).thenReturn(post);
    when(this.likeDislikeRepository.findByPostIdAndUserId("02", "01")).thenReturn(Optional.of(like));
    doNothing().when(this.likeDislikeRepository).deleteById(like.getId());
    when(this.likeDislikeRepository.save(any(LikeDislike.class))).thenReturn(dislike);

    Optional<LikeDislike> givenDislike = this.likeDislikeService.dislike("02", "01");

    assertThat(givenDislike.isPresent()).isTrue();
    assertThat(givenDislike.get().getId()).isEqualTo(dislike.getId());
    assertThat(givenDislike.get().getType()).isEqualTo("dislike");
    assertThat(givenDislike.get().getUserId()).isEqualTo(dislike.getUserId());
    assertThat(givenDislike.get().getPost().getId()).isEqualTo(dislike.getPost().getId());
    assertThat(givenDislike.get().getGivenAt()).isEqualTo(dislike.getGivenAt());

    verify(this.postService, times(1)).getById("02");
    verify(this.likeDislikeRepository, times(1)).findByPostIdAndUserId("02", "01");
    verify(this.likeDislikeRepository, times(1)).deleteById(like.getId());
    verify(this.likeDislikeRepository, times(1)).save(any(LikeDislike.class));
  }

  @Test
  @DisplayName("dislike - Should successfully remove the dislike if it was given")
  void dislikeRemovingIfItsGiven() {
    Post post = this.mockData.getPosts().get(1);
    LikeDislike dislike = this.mockData.getLikesDislikes().get(2);

    when(this.postService.getById("02")).thenReturn(post);
    when(this.likeDislikeRepository.findByPostIdAndUserId("02", "01")).thenReturn(Optional.of(dislike));
    doNothing().when(this.likeDislikeRepository).deleteById(dislike.getId());

    Optional<LikeDislike> givenDislike = this.likeDislikeService.dislike("02", "01");

    assertThat(givenDislike.isEmpty()).isTrue();

    verify(this.postService, times(1)).getById("02");
    verify(this.likeDislikeRepository, times(1)).findByPostIdAndUserId("02", "01");
    verify(this.likeDislikeRepository, times(1)).deleteById(dislike.getId());
    verify(this.likeDislikeRepository, never()).save(any(LikeDislike.class));
  }
}
