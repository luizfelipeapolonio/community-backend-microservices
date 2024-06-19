package com.felipe.community_post_service.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record PostUpdateDTO(
  @Nullable
  @Length(max = 80, message = "O título não deve ter mais de 80 caracteres")
  String title,

  @Nullable
  String content,

  @Nullable
  @Size(min = 1, max = 10, message = "Deve ter pelo menos 1 tag e no máximo 10 tags")
  String[] tags
) {
}
