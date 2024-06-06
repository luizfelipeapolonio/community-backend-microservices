package com.felipe.communityuserservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.communityuserservice.clients.UploadClient;
import com.felipe.communityuserservice.dtos.UploadDTO;
import com.felipe.communityuserservice.dtos.UploadResponseDTO;
import com.felipe.communityuserservice.exceptions.UnprocessableJsonException;
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
    } catch(JsonProcessingException e) {
      throw new UnprocessableJsonException("Não foi possível converter o objeto de upload para JSON", e);
    }
  }

  public void deleteImage(String profileImage) {
    if(profileImage == null) return;
    String imageId = profileImage.split("#")[0];
    this.uploadClient.deleteImage(imageId);
  }

  public <T> T convertJsonStringToObject(String jsonString, Class<T> targetClass) {
    try {
      return this.objectMapper.readValue(jsonString, targetClass);
    } catch(JsonProcessingException e) {
      throw new UnprocessableJsonException("Não foi possível converter JSON string em objeto", e);
    }
  }
}
