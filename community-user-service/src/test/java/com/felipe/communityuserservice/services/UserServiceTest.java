package com.felipe.communityuserservice.services;

import com.felipe.communityuserservice.dtos.UserLoginDTO;
import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.exceptions.RecordNotFoundException;
import com.felipe.communityuserservice.exceptions.UserAlreadyExistsException;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.repositories.UserRepository;
import com.felipe.communityuserservice.security.AuthService;
import com.felipe.communityuserservice.security.JwtService;
import com.felipe.communityuserservice.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  UserService userService;

  @Mock
  UserRepository userRepository;

  @Mock
  JwtService jwtService;

  @Mock
  AuthService authService;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  AuthenticationManager authenticationManager;

  @Mock
  Authentication authentication;

  private User user;

  @BeforeEach
  void setUp() {
    LocalDateTime mockDateTime = LocalDateTime.parse("2024-01-01T12:00:00.123456");

    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("user1@email.com");
    user.setPassword("123456");
    user.setCreatedAt(mockDateTime);
    user.setUpdatedAt(mockDateTime);

    this.user = user;
  }

  @Test
  @DisplayName("register - Should successfully create a user and return it")
  void registerUserSuccess() {
    UserRegisterDTO userDTO = new UserRegisterDTO("User 1", "user1@email.com", "123456");
    User user = this.user;

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

  @Test
  @DisplayName("register - Should throw a UserAlreadyExistsException if user already exists")
  void registerUserFailsByExistingUser() {
    UserRegisterDTO userRegisterDTO = new UserRegisterDTO("User 1", "user1@email.com", "123456");
    User user = this.user;

    when(this.userRepository.findByEmail(userRegisterDTO.email())).thenReturn(Optional.of(user));

    Exception thrown = catchException(() -> this.userService.register(userRegisterDTO));

    assertThat(thrown)
      .isExactlyInstanceOf(UserAlreadyExistsException.class)
      .hasMessage("Usuário de email 'user1@email.com' já cadastrado");

    verify(this.userRepository, times(1)).findByEmail(userRegisterDTO.email());
    verify(this.passwordEncoder, never()).encode(anyString());
    verify(this.userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("login - Should successfully log the user in, generate access token, and return the user info and the token")
  void loginSuccess() {
    UserLoginDTO userLoginDTO = new UserLoginDTO("user1@email.com", "123456");
    var auth = new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password());
    User user = this.user;
    UserPrincipal userPrincipal = new UserPrincipal(user);

    when(this.authenticationManager.authenticate(auth)).thenReturn(this.authentication);
    when(this.authentication.getPrincipal()).thenReturn(userPrincipal);
    when(this.jwtService.generateToken(userPrincipal)).thenReturn("Access Token");

    Map<String, Object> loginResponse = this.userService.login(userLoginDTO);

    assertThat(loginResponse.containsKey("user")).isTrue();
    assertThat(loginResponse.containsKey("token")).isTrue();
    assertThat(loginResponse.get("user"))
      .extracting("id", "name", "email", "password", "createdAt", "updatedAt")
      .contains(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPassword(),
        user.getCreatedAt(),
        user.getUpdatedAt()
      );
    assertThat(loginResponse.get("token")).isEqualTo("Access Token");

    verify(this.authenticationManager, times(1)).authenticate(auth);
    verify(this.authentication, times(1)).getPrincipal();
    verify(this.jwtService, times(1)).generateToken(userPrincipal);
  }

  @Test
  @DisplayName("login - Should throw a BadCredentialsException if the given credentials is invalid")
  void loginFailsByInvalidCredentials() {
    UserLoginDTO userLoginDTO = new UserLoginDTO("user1@email.com", "123456");
    var auth = new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password());

    when(this.authenticationManager.authenticate(auth)).thenThrow(BadCredentialsException.class);

    Exception thrown = catchException(() -> this.userService.login(userLoginDTO));

    assertThat(thrown)
      .isExactlyInstanceOf(BadCredentialsException.class)
      .hasMessage("Usuário ou senha inválidos");
  }

  @Test
  @DisplayName("validateToken - Should successfully validate the access token and return a map object with email and user id")
  void validateTokenSuccess() {
    String token = "Access Token";
    Map<String, String> claims = new HashMap<>(2);
    claims.put("email", "user1@email.com");
    claims.put("userId", "01");

    when(this.jwtService.validateToken(token)).thenReturn(claims);

    Map<String, String> extractedClaims = this.userService.validateToken(token);

    assertThat(extractedClaims.containsKey("email")).isTrue();
    assertThat(extractedClaims.containsKey("userId")).isTrue();
    assertThat(extractedClaims.get("email")).isEqualTo(claims.get("email"));
    assertThat(extractedClaims.get("userId")).isEqualTo(claims.get("userId"));

    verify(this.jwtService, times(1)).validateToken(token);
  }

  @Test
  @DisplayName("getAuthenticatedUserProfile - Should successfully return the authenticated user")
  void getAuthenticatedUserProfileSuccess() {
    User user = this.user;
    UserPrincipal userPrincipal = new UserPrincipal(user);

    when(this.authService.getAuthentication()).thenReturn(this.authentication);
    when(this.authentication.getPrincipal()).thenReturn(userPrincipal);

    User authenticatedUser = this.userService.getAuthenticatedUserProfile();

    assertThat(authenticatedUser).isEqualTo(user);

    verify(this.authService, times(1)).getAuthentication();
    verify(this.authentication, times(1)).getPrincipal();
  }

  @Test
  @DisplayName("getProfile - Should successfully get a user by id and return it")
  void getProfileSuccess() {
    User user = this.user;

    when(this.userRepository.findById("01")).thenReturn(Optional.of(user));

    User foundUser = this.userService.getProfile("01");

    assertThat(foundUser).isEqualTo(user);
    verify(this.userRepository, times(1)).findById("01");
  }

  @Test
  @DisplayName("getProfile - Should throw a RecordNotFoundException if the user is not found")
  void getProfileFailsByUserNotFound() {
    when(this.userRepository.findById("01")).thenReturn(Optional.empty());

    Exception thrown = catchException(() -> this.userService.getProfile("01"));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Usuário de id '01' não encontrado");

    verify(this.userRepository, times(1)).findById("01");
  }
}
