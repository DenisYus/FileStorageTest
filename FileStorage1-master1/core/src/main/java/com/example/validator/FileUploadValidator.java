package com.example.validator;

import com.example.exception.FileExtensionException;
import com.example.exception.FileNameException;
import com.example.exception.FileSizeException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class FileUploadValidator {

    private String allowedExtensions;

    private long maxFileSize;
    private List<String> allowExtensionsList;

    public FileUploadValidator(@Value("${file.allow.extensions}") String allowedExtensions,
                               @Value("${file.max.size}") long maxFileSize) {
        this.allowedExtensions = allowedExtensions;
        this.maxFileSize = maxFileSize;
    }

    @PostConstruct
    public void init() {
        allowExtensionsList = Arrays.asList(allowedExtensions.split(","));
    }


    public void validate(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new FileNameException("File name should not be empty");
        }
        String fileExtension = getFileExtensive(fileName);
        if (!allowExtensionsList.contains(fileExtension)) {
            throw new FileExtensionException(String.format("File extension should be %s ", allowedExtensions));
        }
        if (file.getSize() > maxFileSize) {
            throw new FileSizeException(String.format("File size should not be more than %d ", maxFileSize)); //FIXME преобразовать в человекочитаемый формат
        }


    }

    private String getFileExtensive(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }

}
