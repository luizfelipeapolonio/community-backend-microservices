package com.felipe.communityuserservice.clients;

import com.felipe.communityuserservice.dtos.UploadResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "COMMUNITY-UPLOAD-SERVICE", path = "/api/uploads")
public interface UploadClient {

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  UploadResponseDTO uploadImage(@RequestPart("data") String uploadDTO, @RequestPart("image") MultipartFile image);

  @GetMapping("/properties")
  String getUploadProperties();
}
