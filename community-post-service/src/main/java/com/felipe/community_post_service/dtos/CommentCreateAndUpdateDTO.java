package com.felipe.community_post_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CommentCreateAndUpdateDTO(
  @NotNull(message = "O conteúdo do comentário é obrigatório")
  @NotBlank(message = "O conteúdo do comentário não pode estar em branco")
  @Length(max = 255, message = "O comentário deve ter no máximo 255 caracteres")
  String content
){}
