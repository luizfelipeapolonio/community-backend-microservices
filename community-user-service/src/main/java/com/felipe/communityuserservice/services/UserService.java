package com.felipe.communityuserservice.services;

import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.exceptions.UserAlreadyExistsException;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(UserRegisterDTO userRegisterDTO) {
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
}
