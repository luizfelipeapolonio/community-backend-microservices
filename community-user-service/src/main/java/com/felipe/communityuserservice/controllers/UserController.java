package com.felipe.communityuserservice.controllers;

import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.dtos.UserUpdateDTO;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.services.UserService;
import com.felipe.communityuserservice.utils.response.CustomResponseBody;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> getAuthenticatedUserProfile() {
    User user = this.userService.getAuthenticatedUserProfile();
    UserResponseDTO userResponseDTO = new UserResponseDTO(user);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário autenticado");
    response.setData(userResponseDTO);
    return response;
  }

  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> getProfile(@PathVariable String userId) {
    User foundUser = this.userService.getProfile(userId);
    UserResponseDTO userResponseDTO = new UserResponseDTO(foundUser);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário encontrado");
    response.setData(userResponseDTO);
    return response;
  }

  @PatchMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> update(@PathVariable String userId, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
    User updatedUser = this.userService.update(userId, userUpdateDTO);
    UserResponseDTO userResponseDTO = new UserResponseDTO(updatedUser);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário atualizado com sucesso");
    response.setData(userResponseDTO);
    return response;
  }
}
