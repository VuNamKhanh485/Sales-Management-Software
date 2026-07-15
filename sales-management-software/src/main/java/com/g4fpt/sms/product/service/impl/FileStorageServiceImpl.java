package com.g4fpt.sms.product.service.impl;

import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.product.service.FileStorageService;
import com.g4fpt.sms.product.util.ValidationError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String saveFile(MultipartFile file) throws IOException {

        validate(file);

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

    @Override
    public void validate(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new ValidationException(
                    List.of(new ValidationError("imageFile", "Vui lòng chọn ảnh"))
            );
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ValidationException(
                    List.of(new ValidationError("imageFile", "Ảnh tối đa 5MB"))
            );
        }
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".") || filename.endsWith(".")) {
            throw new ValidationException(
                    List.of(new ValidationError("imageFile", "Tên file không hợp lệ"))
            );
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        List<String> allowed = List.of(
                "jpg",
                "jpeg",
                "png",
                "webp"
        );

        if (!allowed.contains(extension)) {
            throw new ValidationException(
                    List.of(new ValidationError("imageFile", "Chỉ chấp nhận JPG, JPEG, PNG hoặc WEBP"))
            );
        }

        if (ImageIO.read(file.getInputStream()) == null) {
            throw new ValidationException(
                    List.of(new ValidationError("imageFile", "File không phải ảnh hợp lệ"))
            );
        }
    }
}
