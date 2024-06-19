package com.felipe.community_post_service.dtos;

import com.felipe.community_post_service.dtos.constraints.Creation;
import com.felipe.community_post_service.dtos.constraints.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record PostCreateAndUpdateDTO(
  @NotNull(message = "O título é obrigatório", groups = Creation.class)
  @NotBlank(message = "O título não deve estar em branco", groups = Creation.class)
  @Length(max = 80, message = "O título não deve ter mais de 80 caracteres", groups = {Creation.class, Update.class})
  String title,

  @NotNull(message = "O conteúdo é obrigatório", groups = Creation.class)
  @NotBlank(message = "O conteúdo não deve estar em branco", groups = Creation.class)
  String content,

  @NotNull(message = "As tags são obrigatórias", groups = Creation.class)
  @Size(min = 1, max = 10, message = "Deve ter pelo menos 1 tag e no máximo 10 tags", groups = {Creation.class, Update.class})
  String[] tags
) {}
