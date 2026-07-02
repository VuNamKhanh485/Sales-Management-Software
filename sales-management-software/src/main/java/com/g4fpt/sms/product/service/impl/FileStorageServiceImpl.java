package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.product.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String saveFile(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return null;
        }

        // tạo folder nếu chưa có
        Path uploadDir = Paths.get(uploadPath);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // đổi tên file
        String fileName = UUID.randomUUID() + extension;

        Path destination = uploadDir.resolve(fileName);

        Files.copy(file.getInputStream(), destination);

        return fileName;
    }

    public void deleteFile(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(uploadPath, fileName));
        } catch (IOException e) {
            throw new RuntimeException("Không thế xóa file " + fileName, e);
        }
    }
}
