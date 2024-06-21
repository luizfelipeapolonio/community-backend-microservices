package com.felipe.communityuserservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.dtos.UserUpdateDTO;
import com.felipe.communityuserservice.dtos.mappers.UserMapper;
import com.felipe.communityuserservice.exceptions.RecordNotFoundException;
import com.felipe.communityuserservice.models.User;
import com.felipe.communityuserservice.services.UploadService;
import com.felipe.communityuserservice.services.UserService;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  UserService userService;

  @MockBean
  UploadService uploadService;

  @MockBean
  UserMapper userMapper;

  private User user;

  private final String BASE_URL = "/api/users";

  @BeforeEach
  void setUp() {
    LocalDateTime mockDateTime = LocalDateTime.parse("2024-01-01T12:00:00.123456");

    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("user1@email.com");
    user.setPassword("123456");
    user.setBio("This is the bio");
    user.setProfileImage("ProfileImagePath");
    user.setCreatedAt(mockDateTime);
    user.setUpdatedAt(mockDateTime);

    this.user = user;
  }

  @Test
  @DisplayName("getAuthenticatedUserProfile - Should return a success response with Ok status code and the authenticated user info")
  void getAuthenticatedUserProfileSuccess() throws Exception {
    User user = this.user;
    UserResponseDTO userResponseDTO = new UserResponseDTO(user);

    when(this.userService.getAuthenticatedUserProfile()).thenReturn(user);
    when(this.userMapper.toDTO(user)).thenReturn(userResponseDTO);

    this.mockMvc.perform(get(BASE_URL + "/me").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Usuário autenticado"))
      .andExpect(jsonPath("$.data.id").value(userResponseDTO.id()))
      .andExpect(jsonPath("$.data.name").value(userResponseDTO.name()))
      .andExpect(jsonPath("$.data.email").value(userResponseDTO.email()))
      .andExpect(jsonPath("$.data.password").doesNotExist())
      .andExpect(jsonPath("$.data.bio").value(userResponseDTO.bio()))
      .andExpect(jsonPath("$.data.profileImage").value(userResponseDTO.profileImage()))
      .andExpect(jsonPath("$.data.createdAt").value(userResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(userResponseDTO.updatedAt().toString()));

    verify(this.userService, times(1)).getAuthenticatedUserProfile();
    verify(this.userMapper, times(1)).toDTO(user);
  }

  @Test
  @DisplayName("getProfile - Should return a success response with Ok status code and the found user profile")
  void getProfileSuccess() throws Exception {
    User user = this.user;
    UserResponseDTO userResponseDTO = new UserResponseDTO(user);

    when(this.userService.getProfile("01")).thenReturn(user);
    when(this.userMapper.toDTO(user)).thenReturn(userResponseDTO);

    this.mockMvc.perform(get(BASE_URL + "/01").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Usuário encontrado"))
      .andExpect(jsonPath("$.data.id").value(userResponseDTO.id()))
      .andExpect(jsonPath("$.data.name").value(userResponseDTO.name()))
      .andExpect(jsonPath("$.data.email").value(userResponseDTO.email()))
      .andExpect(jsonPath("$.data.password").doesNotExist())
      .andExpect(jsonPath("$.data.bio").value(userResponseDTO.bio()))
      .andExpect(jsonPath("$.data.profileImage").value(userResponseDTO.profileImage()))
      .andExpect(jsonPath("$.data.createdAt").value(userResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(userResponseDTO.updatedAt().toString()));

    verify(this.userService, times(1)).getProfile("01");
    verify(this.userMapper, times(1)).toDTO(user);
  }

  @Test
  @DisplayName("getProfile - Should return an error response with not found status code")
  void getProfileFailsByUserNotFound() throws Exception {
    when(this.userService.getProfile("01"))
      .thenThrow(new RecordNotFoundException("Usuário de id '01' não encontrado"));

    this.mockMvc.perform(get(BASE_URL + "/01").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Usuário de id '01' não encontrado"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.userService, times(1)).getProfile("01");
    verify(this.userMapper, never()).toDTO(any(User.class));
  }

  @Test
  @DisplayName("update - Should return a success response with Ok status code and the updated user")
  void updateSuccess() throws Exception {
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO("Updated name", "123456", "Something meaningful");
    String jsonBody = this.objectMapper.writeValueAsString(userUpdateDTO);
    MockMultipartFile imageMultipart = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );

    User updatedUser = this.user;
    updatedUser.setName(userUpdateDTO.name());
    updatedUser.setBio(userUpdateDTO.bio());

    UserResponseDTO userResponseDTO = new UserResponseDTO(updatedUser);

    when(this.uploadService.convertJsonStringToObject(jsonBody, UserUpdateDTO.class)).thenReturn(userUpdateDTO);
    when(this.userService.update(eq("01"), eq(userUpdateDTO), any(MockMultipartFile.class))).thenReturn(updatedUser);
    when(this.userMapper.toDTO(updatedUser)).thenReturn(userResponseDTO);

    this.mockMvc.perform(multipart(HttpMethod.PATCH, BASE_URL + "/01")
      .file("data", jsonBody.getBytes())
      .file("image", imageMultipart.getBytes())
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Usuário atualizado com sucesso"))
      .andExpect(jsonPath("$.data.id").value(userResponseDTO.id()))
      .andExpect(jsonPath("$.data.name").value(userResponseDTO.name()))
      .andExpect(jsonPath("$.data.email").value(userResponseDTO.email()))
      .andExpect(jsonPath("$.data.password").doesNotExist())
      .andExpect(jsonPath("$.data.bio").value(userResponseDTO.bio()))
      .andExpect(jsonPath("$.data.profileImage").value(userResponseDTO.profileImage()))
      .andExpect(jsonPath("$.data.createdAt").value(userResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(userResponseDTO.updatedAt().toString()));

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, UserUpdateDTO.class);
    verify(this.userService, times(1)).update(eq("01"), eq(userUpdateDTO), any(MockMultipartFile.class));
    verify(this.userMapper, times(1)).toDTO(updatedUser);
  }

  @Test
  @DisplayName("update - Should return an error response with forbidden status code")
  void updateFailsByAccessDenied() throws Exception {
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO("Updated name", "123456", "Something meaningful");
    String jsonBody = this.objectMapper.writeValueAsString(userUpdateDTO);
    MockMultipartFile imageMultipart = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );

    when(this.uploadService.convertJsonStringToObject(jsonBody, UserUpdateDTO.class)).thenReturn(userUpdateDTO);
    when(this.userService.update(eq("01"), eq(userUpdateDTO), any(MockMultipartFile.class)))
      .thenThrow(new AccessDeniedException("Acesso negado: Você não tem permissão para modificar este recurso"));

    this.mockMvc.perform(multipart(HttpMethod.PATCH, BASE_URL + "/01")
      .file("data", jsonBody.getBytes())
      .file("image", imageMultipart.getBytes())
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Acesso negado: Você não tem permissão para modificar este recurso"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, UserUpdateDTO.class);
    verify(this.userService, times(1)).update(eq("01"), eq(userUpdateDTO), any(MockMultipartFile.class));
    verify(this.userMapper, never()).toDTO(any(User.class));
  }

  @Test
  @DisplayName("update - Should return an error response with not found status code")
  void updateFailsByUserNotFound() throws Exception {
    UserUpdateDTO userUpdateDTO = new UserUpdateDTO("Updated name", "123456", "Something meaningful");
    String jsonBody = this.objectMapper.writeValueAsString(userUpdateDTO);
    MockMultipartFile imageMultipart = new MockMultipartFile(
      "image",
      "image.jpg",
      "text/plain",
      "Pretending to be an image".getBytes()
    );

    when(this.uploadService.convertJsonStringToObject(jsonBody, UserUpdateDTO.class)).thenReturn(userUpdateDTO);
    when(this.userService.update(eq("02"), eq(userUpdateDTO), any(MockMultipartFile.class)))
      .thenThrow(new RecordNotFoundException("Usuário de id '02' não encontrado"));

    this.mockMvc.perform(multipart(HttpMethod.PATCH, BASE_URL + "/02")
      .file("data", jsonBody.getBytes())
      .file("image", imageMultipart.getBytes())
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Usuário de id '02' não encontrado"))
      .andExpect(jsonPath("$.data").doesNotExist());

    verify(this.uploadService, times(1)).convertJsonStringToObject(jsonBody, UserUpdateDTO.class);
    verify(this.userService, times(1)).update(eq("02"), eq(userUpdateDTO), any(MockMultipartFile.class));
    verify(this.userMapper, never()).toDTO(any(User.class));
  }

  @Test
  @DisplayName("delete - Should return a success response with Ok status code and the deleted user")
  void deleteAuthenticatedUserProfileSuccess() throws Exception {
    User user = this.user;
    UserResponseDTO userResponseDTO = new UserResponseDTO(user);

    Map<String, UserResponseDTO> deletedUserMap = new HashMap<>(1);
    deletedUserMap.put("deletedUser", userResponseDTO);

    when(this.userService.deleteAuthenticatedUserProfile()).thenReturn(user);

    this.mockMvc.perform(delete(BASE_URL + "/me").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Usuário excluído com sucesso"))
      .andExpect(jsonPath("$.data.deletedUser.id").value(deletedUserMap.get("deletedUser").id()))
      .andExpect(jsonPath("$.data.deletedUser.name").value(deletedUserMap.get("deletedUser").name()))
      .andExpect(jsonPath("$.data.deletedUser.email").value(deletedUserMap.get("deletedUser").email()))
      .andExpect(jsonPath("$.data.deletedUser.password").doesNotExist())
      .andExpect(jsonPath("$.data.deletedUser.bio").value(deletedUserMap.get("deletedUser").bio()))
      .andExpect(jsonPath("$.data.deletedUser.profileImage").value(deletedUserMap.get("deletedUser").profileImage()))
      .andExpect(jsonPath("$.data.deletedUser.createdAt").value(deletedUserMap.get("deletedUser").createdAt().toString()))
      .andExpect(jsonPath("$.data.deletedUser.updatedAt").value(deletedUserMap.get("deletedUser").updatedAt().toString()));

    verify(this.userService, times(1)).deleteAuthenticatedUserProfile();
  }

  @Test
  @DisplayName("getUserInfos - Should return a success response with Ok status code and a map with user infos")
  void getUserInfosSuccess() throws Exception {
    User user = this.user;
    UserResponseDTO userResponseDTO = new UserResponseDTO(
      user.getId(),
      user.getName(),
      user.getEmail(),
      user.getBio(),
      "http://localhost:8080/images/uploads/post/image.jpg",
      user.getCreatedAt(),
      user.getUpdatedAt()
    );

    when(this.userService.getProfile("01")).thenReturn(user);
    when(this.userMapper.toDTO(user)).thenReturn(userResponseDTO);

    this.mockMvc.perform(get(BASE_URL + "/infos/01").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("username").value(userResponseDTO.name()))
      .andExpect(jsonPath("profileImage").value(userResponseDTO.profileImage()));

    verify(this.userService, times(1)).getProfile("01");
    verify(this.userMapper, times(1)).toDTO(user);
  }
}
