package com.felipe.communityuploadservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.communityuploadservice.dtos.UploadDTO;
import com.felipe.communityuploadservice.models.Image;
import com.felipe.communityuploadservice.services.UploadService;
import com.felipe.communityuploadservice.system.config.StorageProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

  private final UploadService uploadService;
  private final ObjectMapper objectMapper;
  private final StorageProperties storageProperties;

  public UploadController(UploadService uploadService, ObjectMapper objectMapper, StorageProperties storageProperties) {
    this.uploadService = uploadService;
    this.objectMapper = objectMapper;
    this.storageProperties = storageProperties;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Image save( @RequestPart("data") String jsonUploadDTO, @RequestPart("image") MultipartFile image) {
    try {
      UploadDTO uploadDTO = this.objectMapper.readValue(jsonUploadDTO, UploadDTO.class);
      return this.uploadService.save(uploadDTO, image);
    } catch(JsonProcessingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  @DeleteMapping("/{imageId}")
  @ResponseStatus(HttpStatus.OK)
  public Image delete(@PathVariable String imageId) {
    return this.uploadService.delete(imageId);
  }

  @GetMapping("/properties")
  @ResponseStatus(HttpStatus.OK)
  public String getUploadProperties() {
    return this.storageProperties.getUploadDir();
  }
}
