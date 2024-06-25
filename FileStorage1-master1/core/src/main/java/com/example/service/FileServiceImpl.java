package com.example.service;

import com.example.dao.FileRepository;
import com.example.dao.UserRepository;
import com.example.dto.FileFilterDto;
import com.example.exception.FileNotFoundException;
import com.example.exception.FileSaveException;
import com.example.model.FileEntity;
import com.example.model.UserEntity;
import com.example.util.FileSpecification;
import com.example.validator.FileUploadValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private String fileStorageLocation;
    @Value("${upload.Directory}")
    private String uploadDir;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileUploadValidator fileUploadValidator;
    private final KafkaProducerService kafkaProducerService;

    public FileServiceImpl(FileRepository fileRepository, UserRepository userRepository, FileUploadValidator fileUploadValidator, @Value("${file.storage.location}") String fileStorageLocation, KafkaProducerService kafkaProducerService) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.fileUploadValidator = fileUploadValidator;
        this.fileStorageLocation = fileStorageLocation;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public InputStreamResource downloadFile(String fileName, UserEntity user) throws IOException {
        String userDirectory = fileStorageLocation + File.separator + user.getId();
        Path filePath = Paths.get(userDirectory, fileName);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found");
        }
        kafkaProducerService.sendMailMessage(user.getEmail(), "Files downloaded", "You have downloaded"+fileName);
        return new InputStreamResource(Files.newInputStream(filePath));
    }

    @Override
    @Transactional
    public void uploadFiles(String email, List<MultipartFile> files) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        for (MultipartFile file : files) {
            fileUploadValidator.validate(file);
            saveFile(file, user);
            kafkaProducerService.sendMailMessage(email,"Files Uploaded","You have uploaded"+files.size());
        }
    }

    public void saveFile(MultipartFile file, UserEntity user) {
        String fileName = file.getOriginalFilename();
        FileEntity fileEntity = FileEntity.builder()
                .name(fileName)
                .size(file.getSize())
                .uploadDate(LocalDateTime.now())
                .user(user)
                .build();
        fileRepository.save(fileEntity);
        Path filePath = Paths.get(uploadDir + fileName);
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new FileSaveException("Failed to save file"+ e.getMessage());
        }
    }

    @Override
    public List<FileEntity> getUserFiles(UserEntity user) {
        return fileRepository.findByUser(user);
    }

    @Override
    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    @Override
    public List<FileEntity> getFilterAndSortedFiles(UserEntity user, FileFilterDto filterDto) {
        Specification<FileEntity> spec = FileSpecification.getFilesByFilter(filterDto).and((root, query, cb) -> cb.equal(root.get("user"), user));
        return fileRepository.findAll(spec);
}
    @Override
    public List<FileEntity> getFilterAndSortedFiles(FileFilterDto filterDto) {
        Specification<FileEntity> spec = FileSpecification.getFilesByFilter(filterDto);
        return fileRepository.findAll(spec);
    }
}
