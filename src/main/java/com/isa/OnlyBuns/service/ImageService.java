package com.isa.OnlyBuns.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageService
{
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
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

    //@Scheduled(cron = "0 * * * * *") // Pokreće se svakog minuta za testiranje
    @Scheduled(cron = "0 0 0 * * *")  // Pokreće se svakog dana u ponoć
    public void compressOldImages() throws IOException {
        Path imagesPath = Paths.get(uploadDir, "images");

        if (Files.exists(imagesPath)) {
            Files.walk(imagesPath)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            if (isOlderThanOneMonth(filePath) && !isAlreadyCompressed(filePath)) {
                                compressImage(filePath);
                            }
                        } catch (IOException e) {
                            logger.error("Greška prilikom kompresije slike: " + filePath.toString(), e);
                        }
                    });
        } else {
            logger.warn("Direktorijum za slike ne postoji: " + imagesPath);
        }
    }

    private boolean isOlderThanOneMonth(Path filePath) throws IOException {
        FileTime fileTime = Files.getLastModifiedTime(filePath);
        LocalDateTime fileDate = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        boolean isOlder = fileDate.isBefore(LocalDateTime.now().minusMonths(1)); // Proverava starost od mesec dana
        logger.info("Provera starosti fajla: " + filePath.toString() + " - Stariji od mesec dana: " + isOlder);
        return isOlder;
    }

    private boolean isAlreadyCompressed(Path filePath) {
        String fileName = filePath.toFile().getName();
        return fileName.contains("_compressed");
    }

    private void compressImage(Path filePath) throws IOException {
        File file = filePath.toFile();
        String compressedFileName = filePath.toString().replace(".png", "_compressed.jpg");

        Thumbnails.of(file)
                .scale(1.0)
                .outputQuality(0.3)
                .toFile(compressedFileName);

        logger.info("Kompresovana slika sačuvana: " + compressedFileName);
    }

}





