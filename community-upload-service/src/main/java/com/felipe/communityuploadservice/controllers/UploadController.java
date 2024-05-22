package com.felipe.communityuploadservice.controllers;

import com.felipe.communityuploadservice.dtos.UploadDTO;
import com.felipe.communityuploadservice.models.Image;
import com.felipe.communityuploadservice.services.UploadService;
import com.felipe.communityuploadservice.utils.response.CustomResponseBody;
import com.felipe.communityuploadservice.utils.response.ResponseConditionStatus;
import org.springframework.http.HttpStatus;
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

  public UploadController(UploadService uploadService) {
    this.uploadService = uploadService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomResponseBody<Image> save(
    @RequestPart("image") MultipartFile image,
    @RequestPart("data") UploadDTO uploadDTO
  ) {
    Image uploadedImage = this.uploadService.save(uploadDTO, image);

    CustomResponseBody<Image> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.SUCCESS);
    response.setCode(HttpStatus.CREATED);
    response.setMessage("Image salva com successo");
    response.setData(uploadedImage);
    return response;
  }
}
