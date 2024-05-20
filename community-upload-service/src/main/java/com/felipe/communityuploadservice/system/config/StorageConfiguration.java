package com.felipe.communityuploadservice.system.config;

import com.felipe.communityuploadservice.exceptions.UploadDirectoryInitializationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StorageConfiguration {

  private final Path imageStorageLocation;

  public StorageConfiguration(StorageProperties storageProperties) {
    this.imageStorageLocation = Paths.get(storageProperties.getUploadDir())
      .toAbsolutePath().normalize();
  }

  public void init() {
    try {
      if(Files.exists(this.imageStorageLocation)) return;
      Path root = Files.createDirectory(this.imageStorageLocation);
      Files.createDirectory(Paths.get(root.toString(), "user"));
      Files.createDirectory(Paths.get(root.toString(), "post"));
    } catch(IOException e) {
      System.out.println("Erro ao criar diretório de upload -> " + e.getMessage());
      throw new UploadDirectoryInitializationException("Não foi possível criar diretório de upload", e);
    }
  }
}
