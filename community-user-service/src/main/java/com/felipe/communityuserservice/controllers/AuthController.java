package com.felipe.communityuserservice.controllers;

import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.services.UserService;
import com.felipe.communityuserservice.utils.response.CustomResponseBody;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomResponseBody<UserResponseDTO> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
    User createdUser = this.userService.register(userRegisterDTO);
    UserResponseDTO userResponseDTO = new UserResponseDTO(createdUser);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.CREATED);
    response.setMessage("Usuário criado com sucesso");
    response.setData(userResponseDTO);
    return response;
  }
}
