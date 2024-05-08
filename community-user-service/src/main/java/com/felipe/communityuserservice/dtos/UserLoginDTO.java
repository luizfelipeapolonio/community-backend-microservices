package com.felipe.communityuserservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginDTO(
  @NotNull(message = "O e-mail não deve ser nulo")
  @NotBlank(message = "O e-mail não deve estar em branco")
  String email,

  @NotNull(message = "A senha não deve ser nula")
  @NotBlank(message = "A senha não deve estar em branco")
  String password
) {}
