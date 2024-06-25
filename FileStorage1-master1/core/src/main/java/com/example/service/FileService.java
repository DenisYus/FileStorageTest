package com.example.service;

import com.example.dto.FileFilterDto;
import com.example.model.FileEntity;
import com.example.model.UserEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    InputStreamResource downloadFile(String fileName, UserEntity user) throws IOException;
    void uploadFiles(String email, List<MultipartFile> files);
    List<FileEntity> getUserFiles(UserEntity user);
    List<FileEntity> getAllFiles();
    List<FileEntity> getFilterAndSortedFiles(FileFilterDto filterDto);
    List<FileEntity> getFilterAndSortedFiles(UserEntity user, FileFilterDto filterDto);

}
