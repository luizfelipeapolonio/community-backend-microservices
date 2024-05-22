package com.felipe.communityuploadservice.repositories;

import com.felipe.communityuploadservice.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadRepository extends JpaRepository<Image, String> {
}
