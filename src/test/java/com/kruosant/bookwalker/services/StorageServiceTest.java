package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageServiceTest {
  @TempDir
  private Path uploadDir;

  @Test
  void constructorCreatesUploadDirectory() throws IOException {
    Path nested = uploadDir.resolve("nested");

    new StorageService(nested.toString());

    assertTrue(Files.isDirectory(nested));
  }

  @Test
  void storeCoverSanitizesNameAndWritesFile() throws IOException {
    StorageService storageService = new StorageService(uploadDir.toString());
    MultipartFile file = new MockMultipartFile("cover", "bad name!.jpg", "image/jpeg", "image".getBytes());

    String storedName = storageService.storeCover(file);

    assertTrue(storedName.endsWith("-bad_name_.jpg"));
    assertEquals("image", Files.readString(uploadDir.resolve(storedName)));
  }

  @Test
  void storeCoverRejectsNullFile() throws IOException {
    StorageService storageService = new StorageService(uploadDir.toString());

    assertThrows(BadRequestException.class, () -> storageService.storeCover(null));
  }

  @Test
  void storeCoverRejectsEmptyFile() throws IOException {
    StorageService storageService = new StorageService(uploadDir.toString());
    MultipartFile file = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", new byte[0]);

    assertThrows(BadRequestException.class, () -> storageService.storeCover(file));
  }

  @Test
  void storeCoverWrapsIoFailures() throws IOException {
    StorageService storageService = new StorageService(uploadDir.toString());
    MultipartFile file = new MockMultipartFile("cover", "cover.jpg", "image/jpeg", "image".getBytes()) {
      @Override
      public java.io.InputStream getInputStream() throws IOException {
        throw new IOException("boom");
      }
    };

    assertThrows(RuntimeException.class, () -> storageService.storeCover(file));
  }
}
