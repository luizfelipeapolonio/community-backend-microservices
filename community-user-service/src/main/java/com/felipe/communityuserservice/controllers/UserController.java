package com.felipe.communityuserservice.controllers;

import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.dtos.UserUpdateDTO;
import com.felipe.communityuserservice.dtos.mappers.UserMapper;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.services.UploadService;
import com.felipe.communityuserservice.services.UserService;
import com.felipe.communityuserservice.utils.response.CustomResponseBody;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UploadService uploadService;
  private final UserMapper userMapper;

  public UserController(UserService userService, UploadService uploadService, UserMapper userMapper) {
    this.userService = userService;
    this.uploadService = uploadService;
    this.userMapper = userMapper;
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> getAuthenticatedUserProfile() {
    User user = this.userService.getAuthenticatedUserProfile();
    UserResponseDTO userResponseDTO = this.userMapper.toDTO(user);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário autenticado");
    response.setData(userResponseDTO);
    return response;
  }

  @DeleteMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<Map<String, UserResponseDTO>> deleteAuthenticatedUserProfile() {
    User deletedUser = this.userService.deleteAuthenticatedUserProfile();
    UserResponseDTO userResponseDTO = new UserResponseDTO(deletedUser);

    Map<String, UserResponseDTO> deletedUserMap = new HashMap<>(1);
    deletedUserMap.put("deletedUser", userResponseDTO);

    CustomResponseBody<Map<String, UserResponseDTO>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário excluído com sucesso");
    response.setData(deletedUserMap);
    return response;
  }

  @GetMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> getProfile(@PathVariable String userId) {
    User foundUser = this.userService.getProfile(userId);
    UserResponseDTO userResponseDTO = this.userMapper.toDTO(foundUser);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário encontrado");
    response.setData(userResponseDTO);
    return response;
  }

  @PatchMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> update(
    @PathVariable String userId,
    @RequestPart("data") String updateDTO,
    @RequestPart(name = "image", required = false) MultipartFile image
  ) {
    UserUpdateDTO userUpdateDTO  = this.uploadService.convertJsonStringToObject(updateDTO, UserUpdateDTO.class);
    User updatedUser = this.userService.update(userId, userUpdateDTO, image);
    UserResponseDTO userResponseDTO = this.userMapper.toDTO(updatedUser);

    CustomResponseBody<UserResponseDTO> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.OK);
    response.setMessage("Usuário atualizado com sucesso");
    response.setData(userResponseDTO);
    return response;
  }
}
