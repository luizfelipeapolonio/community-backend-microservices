package com.felipe.communityuploadservice.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "image")
public class StorageProperties {

  private String uploadDir;

  public String getUploadDir() {
    return this.uploadDir;
  }

  public void setUploadDir(String uploadDir) {
    this.uploadDir = uploadDir;
  }
}
