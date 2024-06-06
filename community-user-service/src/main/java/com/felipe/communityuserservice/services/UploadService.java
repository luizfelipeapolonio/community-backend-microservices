package com.felipe.communityuserservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.communityuserservice.clients.UploadClient;
import com.felipe.communityuserservice.dtos.UploadDTO;
import com.felipe.communityuserservice.dtos.UploadResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

  private final UploadClient uploadClient;
  private final ObjectMapper objectMapper;

  public UploadService(UploadClient uploadClient, ObjectMapper objectMapper) {
    this.uploadClient = uploadClient;
    this.objectMapper = objectMapper;
  }

  public UploadResponseDTO upload(UploadDTO uploadDTO, MultipartFile image) {
    try {
      String jsonUploadDTO = this.objectMapper.writeValueAsString(uploadDTO);
      return this.uploadClient.uploadImage(jsonUploadDTO, image);
    } catch(JsonProcessingException e) {
      // TODO: trocar por uma exceção personalizada
      throw new RuntimeException("Não foi possível converter para JSON");
    }
  }

  public void delete(String profileImage) {
    if(profileImage == null) return;
    String imageId = profileImage.split("#")[0];
    this.uploadClient.deleteImage(imageId);
  }

  public <T> T convertJsonStringToObject(String jsonString, Class<T> targetClass) {
    try {
      return this.objectMapper.readValue(jsonString, targetClass);
    } catch(JsonProcessingException e) {
      // TODO: criar uma exceção personalizada
      throw new RuntimeException("Não foi possível processar os dados da requisição");
    }
  }
}
