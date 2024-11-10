package com.isa.OnlyBuns.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

   @Scheduled(cron = "0 0 0 * * ?") // Pokreće se svakog dana u ponoć
   //@Scheduled(cron = "0 * * * * ?")
    public void compressOldImages() {
       logger.info("Zadatak za kompresiju slika je pokrenut.");
        Path imagesPath = Paths.get(uploadDir, "images");
        if (Files.exists(imagesPath)) {
            try {
                Files.walk(imagesPath)
                        .filter(Files::isRegularFile)
                        .filter(this::isOlderThanOneMonth)
                        .filter(path -> !isAlreadyCompressed(path))
                        .forEach(this::compressImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isAlreadyCompressed(Path path) {
        String compressedFileName = path.toString().replace(".png", "_compressed.jpg");
        boolean exists = Files.exists(Paths.get(compressedFileName));
        logger.info("Provera da li je slika već kompresovana: " + compressedFileName + " - Postoji: " + exists);
        return exists;
    }

    private boolean isOlderThanOneMonth(Path path) {
        try {
            FileTime fileTime = Files.getLastModifiedTime(path);
            LocalDateTime fileDate = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            boolean isOlder = fileDate.isBefore(LocalDateTime.now().minusMonths(1));
            logger.info("Provera starosti fajla: " + path.toString() + " - Stariji od mesec dana: " + isOlder);
            return isOlder;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void compressImage(Path path) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            if (image != null) {

                String compressedFileName = path.toString().replace(".png", "_compressed.jpg");
                File output = new File(compressedFileName);

                // Snima kompresovanu verziju slike kao novi fajl
                ImageIO.write(image, "jpg", output);
                logger.info("Kompresovana slika sačuvana: " + compressedFileName);
            } else {
                logger.warn("Slika nije mogla biti učitana: " + path.toString());
            }
        } catch (IOException e) {
            logger.error("Greška prilikom kompresije slike: " + path.toString(), e);
        }
    }

}





