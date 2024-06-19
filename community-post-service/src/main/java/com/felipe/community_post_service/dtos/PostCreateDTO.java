package com.felipe.community_post_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record PostCreateDTO(
  @NotNull(message = "O título é obrigatório")
  @NotBlank(message = "O título não deve estar em branco")
  @Length(max = 80, message = "O título não deve ter mais de 80 caracteres")
  String title,

  @NotNull(message = "O conteúdo é obrigatório")
  @NotBlank(message = "O conteúdo não deve estar em branco")
  String content,

  @NotNull(message = "As tags são obrigatórias")
  @Size(min = 1, max = 10, message = "Deve ter pelo menos 1 tag e no máximo 10 tags")
  String[] tags
) {}
