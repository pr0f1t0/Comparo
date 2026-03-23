package com.pr0f1t.comparo.catalogservice.service.filestorage;

import org.springframework.web.multipart.MultipartFile;


public interface FileStorageService {
    String upload(MultipartFile file);
    void deleteFile(String fileUrl);
}
