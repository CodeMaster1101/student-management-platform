package com.student.management.platform.global.persistence;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class ImagePersistenceService {

  private static final String STORAGE_PATH = "/app/resources/";

  @SneakyThrows
  public void mountImageToVolume(MultipartFile image, EntityType entityType, String entityName) {
    String entityTypeValue = entityType.name().toLowerCase();
    String filename = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
    Path uploadPath = Paths.get(STORAGE_PATH + entityTypeValue + "/" + entityName);
    Path pathWithImage = Paths.get(STORAGE_PATH + entityTypeValue + "/" + entityName + "/" + filename);

    if (Files.exists(uploadPath)) {
      removeDirectory(uploadPath.toString());
    }

    Files.createDirectories(uploadPath);
    try (InputStream inputStream = image.getInputStream()) {
      Files.copy(inputStream, pathWithImage);
    }
  }


  public void unmountImageFromVolume(EntityType entityType, String entityName) {
    Path directoryPath = Paths.get(STORAGE_PATH + entityType.name().toLowerCase() + "/" + entityName);
    removeDirectory(directoryPath.toString());
  }

  @SneakyThrows
  private void removeDirectory(String directoryPath) {
    File directory = new File(directoryPath);
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            removeDirectory(file.getAbsolutePath());
          } else {
            Files.delete(file.toPath());
          }
        }
      }
      Files.delete(directory.toPath());
    }
  }

}
