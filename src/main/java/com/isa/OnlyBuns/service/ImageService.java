package com.isa.OnlyBuns.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService
{
    @Value("${upload.path}")
    private String uploadDir;

    public String saveImage(String imageBase64) throws IOException {
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            String fileName = UUID.randomUUID() + ".png";

            Path folderPath = Paths.get(uploadDir, "images");
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            Path filePath = folderPath.resolve(fileName);
            Files.write(filePath, imageBytes);
            return "images/" + fileName;
        }
        throw new IllegalArgumentException("No picture data");
}
}

