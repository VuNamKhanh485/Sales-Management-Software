package com.g4fpt.sms.product.service;

import com.g4fpt.sms.common.enums.UploadFolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String saveFile(MultipartFile file, UploadFolder folder) throws IOException;
    void deleteFile(String fileName, UploadFolder folder);
    void validate(MultipartFile file) throws IOException;
}
