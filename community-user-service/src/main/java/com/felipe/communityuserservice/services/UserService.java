package com.felipe.communityuserservice.services;

import com.felipe.communityuserservice.dtos.UserLoginDTO;
import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.exceptions.UserAlreadyExistsException;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.repositories.UserRepository;
import com.felipe.communityuserservice.security.AuthService;
import com.felipe.communityuserservice.security.JwtService;
import com.felipe.communityuserservice.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final AuthService authService;

  public UserService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    AuthenticationManager authenticationManager,
    JwtService jwtService,
    AuthService authService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.authService = authService;
  }

  public User register(@Valid UserRegisterDTO userRegisterDTO) {
    Optional<User> existingUser = this.userRepository.findByEmail(userRegisterDTO.email());

    if(existingUser.isPresent()) {
      throw new UserAlreadyExistsException(userRegisterDTO.email());
    }

    User newUser = new User();
    newUser.setName(userRegisterDTO.name());
    newUser.setEmail(userRegisterDTO.email());
    newUser.setPassword(this.passwordEncoder.encode(userRegisterDTO.password()));

    return this.userRepository.save(newUser);
  }

  public Map<String, Object> login(@Valid UserLoginDTO userLoginDTO) {
    try {
      var auth = new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password());
      Authentication authentication = this.authenticationManager.authenticate(auth);
      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
      String token = this.jwtService.generateToken(userPrincipal);

      Map<String, Object> loginResponse = new HashMap<>(2);
      loginResponse.put("user", userPrincipal.getUser());
      loginResponse.put("token", token);

      return loginResponse;
    } catch(BadCredentialsException e) {
      throw new BadCredentialsException("Usuário ou senha inválidos", e);
    }
  }

  public Map<String, String> validateToken(String token) {
    return this.jwtService.validateToken(token);
  }

  public User getAuthenticatedUserProfile() {
    Authentication authentication = this.authService.getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    return userPrincipal.getUser();
  }
}
