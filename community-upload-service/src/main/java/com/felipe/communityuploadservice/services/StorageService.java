package com.felipe.communityuploadservice.services;

import com.felipe.communityuploadservice.exceptions.UploadDirectoryInitializationException;
import com.felipe.communityuploadservice.system.config.StorageProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

  private final Path imageStorageLocation;

  public StorageService(StorageProperties storageProperties) {
    this.imageStorageLocation = Paths.get(storageProperties.getUploadDir())
      .toAbsolutePath().normalize();
  }

  public void init() {
    try {
      if(Files.exists(this.imageStorageLocation)) return;
      Files.createDirectory(this.imageStorageLocation);
    } catch(IOException e) {
      System.out.println("Erro ao criar diretório de upload -> " + e.getMessage());
      throw new UploadDirectoryInitializationException("Não foi possível criar diretório de upload", e);
    }
  }
}
