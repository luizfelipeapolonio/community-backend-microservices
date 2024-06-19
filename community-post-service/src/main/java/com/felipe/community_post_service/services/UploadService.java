package com.felipe.community_post_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.community_post_service.clients.UploadClient;
import com.felipe.community_post_service.dtos.UploadDTO;
import com.felipe.community_post_service.dtos.UploadResponseDTO;
import com.felipe.community_post_service.exceptions.UnprocessableJsonException;
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

  public UploadResponseDTO uploadImage(UploadDTO uploadDTO, MultipartFile image) {
    try {
      String jsonUploadDTO = this.objectMapper.writeValueAsString(uploadDTO);
      return this.uploadClient.uploadImage(jsonUploadDTO, image);
    } catch (JsonProcessingException e) {
      throw new UnprocessableJsonException("Não foi possível converter DTO para JSON", e);
    }
  }

  public void deleteImage(String postImagePath) {
    String imageId = postImagePath.split("#")[0];
    this.uploadClient.deleteImage(imageId);
  }

  public <T> T convertJsonStringToObject(String jsonString, Class<T> targetClass) {
    try {
      return this.objectMapper.readValue(jsonString, targetClass);
    } catch(JsonProcessingException e) {
      throw new UnprocessableJsonException("Não foi possível converter JSON em objeto", e);
    }
  }
}
