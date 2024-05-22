package com.felipe.communityuploadservice.services;

import com.felipe.communityuploadservice.dtos.UploadDTO;
import com.felipe.communityuploadservice.exceptions.InvalidFileTypeException;
import com.felipe.communityuploadservice.models.Image;
import com.felipe.communityuploadservice.repositories.UploadRepository;
import com.felipe.communityuploadservice.system.config.StorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Service
public class UploadService {

  private final UploadRepository uploadRepository;
  private final Path rootUploadPath;

  public UploadService(UploadRepository uploadRepository, StorageProperties storageProperties) {
    this.uploadRepository = uploadRepository;
    this.rootUploadPath = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();
  }

  public Image save(UploadDTO uploadDTO, MultipartFile image) {
    if(this.isFileContentTypeInvalid(image)) {
      throw new InvalidFileTypeException("Arquivo inválido! Por favor, envie apenas PNG ou JPEG");
    }

    Image newImage = new Image();
    newImage.setName(this.generateImageName(image));
    newImage.setSize(image.getSize());

    if(uploadDTO.target().equals("user")) {
      newImage.setUserId(uploadDTO.postOrUserId());
    } else {
      newImage.setPostId(uploadDTO.postOrUserId());
    }

    try {
      Path uploadLocation = Paths.get(this.rootUploadPath.toString(), uploadDTO.target()).normalize();
      Files.copy(image.getInputStream(), uploadLocation.resolve(newImage.getName()));
      return this.uploadRepository.save(newImage);
    } catch(IOException e) {
      // TODO: tratar as exceções
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private String generateImageName(MultipartFile image) {
    String originalName = image.getOriginalFilename();
    long epochMilliseconds = Instant.now().toEpochMilli();
    return epochMilliseconds + "-" + originalName;
  }

  private boolean isFileContentTypeInvalid(MultipartFile file) {
    String png = MimeTypeUtils.IMAGE_PNG_VALUE;
    String jpeg = MimeTypeUtils.IMAGE_JPEG_VALUE;
    String fileContentType = file.getContentType();
    if(fileContentType == null) return false;
    return !fileContentType.equals(png) && !fileContentType.equals(jpeg);
  }
}
