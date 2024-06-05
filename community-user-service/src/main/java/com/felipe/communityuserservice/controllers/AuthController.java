package com.felipe.communityuserservice.controllers;

import com.felipe.communityuserservice.dtos.UserLoginDTO;
import com.felipe.communityuserservice.dtos.UserLoginResponseDTO;
import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.dtos.mappers.UserMapper;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.services.UserService;
import com.felipe.communityuserservice.utils.response.CustomResponseBody;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final UserMapper userMapper;

  public AuthController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
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

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserLoginResponseDTO> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
    Map<String, Object> loginMap = this.userService.login(userLoginDTO);
    UserResponseDTO userResponseDTO = this.userMapper.toDTO((User) loginMap.get("user"));
    UserLoginResponseDTO loginResponseDTO = new UserLoginResponseDTO(userResponseDTO, loginMap.get("token").toString());

    CustomResponseBody<UserLoginResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário logado");
    response.setData(loginResponseDTO);
    return response;
  }

  @GetMapping("/validate")
  @ResponseStatus(HttpStatus.OK)
  public Map<String, String> validate(@RequestHeader("accessToken") String token) {
    return this.userService.validateToken(token);
  }
}
