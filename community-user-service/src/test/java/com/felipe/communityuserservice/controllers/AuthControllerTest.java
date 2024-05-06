package com.felipe.communityuserservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.communityuserservice.dtos.UserRegisterDTO;
import com.felipe.communityuserservice.dtos.UserResponseDTO;
import com.felipe.communityuserservice.models.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  UserService userService;

  private List<User> users;

  private final String BASE_URL = "/api/auth";

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
  @DisplayName("register - Should return a success response with OK status code and the created user")
  void registerUserSuccess() throws Exception {
    UserRegisterDTO userRegisterDTO = new UserRegisterDTO("User 1", "user1@email.com", "123456");
    User createdUser = this.users.get(0);
    UserResponseDTO userResponseDTO = new UserResponseDTO(createdUser);
    String jsonBody = this.objectMapper.writeValueAsString(userRegisterDTO);

    when(this.userService.register(userRegisterDTO)).thenReturn(createdUser);

    this.mockMvc.perform(post(BASE_URL + "/register")
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.status").value(ResponseConditionStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
      .andExpect(jsonPath("$.message").value("Usuário criado com sucesso"))
      .andExpect(jsonPath("$.data.id").value(userResponseDTO.id()))
      .andExpect(jsonPath("$.data.name").value(userResponseDTO.name()))
      .andExpect(jsonPath("$.data.email").value(userResponseDTO.email()))
      .andExpect(jsonPath("$.data.createdAt").value(userResponseDTO.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(userResponseDTO.updatedAt().toString()));

    verify(this.userService, times(1)).register(userRegisterDTO);
  }
}
