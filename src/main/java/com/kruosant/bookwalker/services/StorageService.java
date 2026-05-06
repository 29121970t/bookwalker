package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {
  private final Path uploadDir;

  public StorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
    this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    Files.createDirectories(this.uploadDir);
  }

  public String storeCover(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException();
    }
    String filename = UUID.randomUUID() + "-" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
    try {
      Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
      return filename;
    } catch (IOException e) {
      throw new StorageException("Failed to store file", e);
    }
  }
}
