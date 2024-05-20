package com.felipe.communityuploadservice.system;

import com.felipe.communityuploadservice.system.config.StorageConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StorageInitializer implements CommandLineRunner {

  private final StorageConfiguration storageConfiguration;

  public StorageInitializer(StorageConfiguration storageConfiguration) {
    this.storageConfiguration = storageConfiguration;
  }

  @Override
  public void run(String... args) throws Exception {
   this.storageConfiguration.init();
  }
}
