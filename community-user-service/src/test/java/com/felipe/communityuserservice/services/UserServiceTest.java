package com.felipe.communityuserservice.services;

import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  private List<User> users;

  @BeforeEach
  void setUp() {
    LocalDateTime mockDateTime = LocalDateTime.parse("2024-01-01T12:00:00.123456");

    User user1 = new User();
    user1.setId("01");
    user1.setName("User 1");
    user1.setEmail("user1@email.com");
    user1.setPassword("123456");
    user1.setCreatedAt(mockDateTime);
    user1.setUpdatedAt(mockDateTime);

    this.users = new ArrayList<>();
    this.users.add(user1);
  }

  @Test
  @DisplayName("register - Should successfully create a user and return it")
  void registerUserSuccess() {
    UserRegisterDTO userDTO = new UserRegisterDTO("User 1", "user1@email.com", "123456");
    User user = this.users.get(0);

    when(this.userRepository.findByEmail(userDTO.email())).thenReturn(Optional.empty());
    when(this.passwordEncoder.encode(userDTO.password())).thenReturn("Encoded password");
    when(this.userRepository.save(any(User.class))).thenReturn(user);

    User createdUser = this.userService.register(userDTO);

    assertThat(createdUser.getId()).isEqualTo(user.getId());
    assertThat(createdUser.getName()).isEqualTo(user.getName());
    assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());
    assertThat(createdUser.getCreatedAt()).isEqualTo(user.getCreatedAt());
    assertThat(createdUser.getUpdatedAt()).isEqualTo(user.getUpdatedAt());

    verify(this.userRepository, times(1)).findByEmail(userDTO.email());
    verify(this.passwordEncoder, times(1)).encode(userDTO.password());
    verify(this.userRepository, times(1)).save(any(User.class));
  }
}
