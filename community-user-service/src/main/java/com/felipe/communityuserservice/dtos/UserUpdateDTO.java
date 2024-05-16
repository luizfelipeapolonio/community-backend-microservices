package com.felipe.communityuserservice.dtos;

import jakarta.annotation.Nullable;
import org.hibernate.validator.constraints.Length;

public record UserUpdateDTO(
  @Nullable
  @Length(max = 100, message = "O nome não deve ter mais de 100 caracteres")
  String name,

  @Nullable
  @Length(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
  String password,

  @Nullable
  @Length(max = 150, message = "A bio não deve ter mais de 150 caracteres")
  String bio
) {}
