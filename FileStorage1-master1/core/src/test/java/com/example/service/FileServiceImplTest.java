package com.example.service;

import com.example.BaseIntegrationTest;
import com.example.dao.FileRepository;
import com.example.dao.UserRepository;
import com.example.dto.FileFilterDto;
import com.example.exception.FileNotFoundException;
import com.example.model.FileEntity;
import com.example.model.UserEntity;
import com.example.model.UserStatus;
import com.example.validator.FileUploadValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileServiceImplTest extends BaseIntegrationTest {
    @Autowired
    private FileServiceImpl fileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileUploadValidator fileUploadValidator;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    private UserEntity testUser;
    private UserEntity secondTestUser;

    @TempDir
    static Path tempDir;
    private String fileStorageLocation;

    @BeforeEach
    void setUp() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
        fileStorageLocation = tempDir.toString();
        testUser = userRepository.save(UserEntity.builder()
                .email("test@mail.ru").password("123").status(UserStatus.ACTIVE).build());
        secondTestUser = userRepository.save(UserEntity.builder()
                .email("test1@mail.ru").password("1234").status(UserStatus.ACTIVE).build());

        fileRepository.save(FileEntity.builder()
                .name("file4.png")
                .size(1000L)
                .uploadDate(LocalDateTime.now().minusDays(1).withNano(0))
                .user(secondTestUser)
                .build());

        fileRepository.save(FileEntity.builder()
                .name("file5.png")
                .size(2000L)
                .uploadDate(LocalDateTime.now().minusDays(2).withNano(0))
                .user(secondTestUser)
                .build());

        fileRepository.save(FileEntity.builder()
                .name("file6.png")
                .size(3000L)
                .uploadDate(LocalDateTime.now().minusDays(3).withNano(0))
                .user(secondTestUser)
                .build());
    }

    @Test
    public void UploadFiles() {
        // given
        fileRepository.deleteAll();
        List<MultipartFile> files = new ArrayList<>();
        files.add(createMockMultipartFile("file1.png", "Hello, World!".getBytes()));
        files.add(createMockMultipartFile("file2.png", "Testing uploadFiles method".getBytes()));

        // when
        fileService.uploadFiles(testUser.getEmail(), files);

        // then
        List<FileEntity> savedFiles = fileRepository.findAll();
        assertEquals(2, savedFiles.size());
        for (FileEntity file : savedFiles) {
            assertEquals(testUser.getId(), file.getUser().getId());
        }
    }

    @Test
    public void UploadFilesUserNotFound() {
        // given
        List<MultipartFile> files = new ArrayList<>();
        files.add(createMockMultipartFile("file1.png", "Hello, World!".getBytes()));

        // when
        assertThrows(UsernameNotFoundException.class, () -> {
            fileService.uploadFiles("nonexistent@mail.ru", files);
        });
    }

    @Test
    public void SaveFile() {
        // given
        fileRepository.deleteAll();
        String fileName = "test.png";
        MultipartFile multipartFile = createMockMultipartFile(fileName, "Test file content".getBytes());

        // when
        fileService.saveFile(multipartFile, testUser);

        // then
        List<FileEntity> savedFiles = fileRepository.findAll();
        assertEquals(1, savedFiles.size());
        FileEntity savedFile = savedFiles.get(0);
        assertEquals(fileName, savedFile.getName());
        assertEquals(testUser.getId(), savedFile.getUser().getId());
    }


    private MockMultipartFile createMockMultipartFile(String fileName, byte[] content) {
        return new MockMultipartFile("file", fileName, "image/png", content);
    }

    @Test
    public void DownloadFile() throws IOException {
        // given
        fileService = new FileServiceImpl(fileRepository, userRepository, fileUploadValidator, fileStorageLocation, kafkaProducerService );
        String fileName = "test.png";
        Path userDirectory = Paths.get(fileStorageLocation, String.valueOf(testUser.getId()));
        Files.createDirectories(userDirectory);
        Path filePath = Paths.get(userDirectory.toString(), fileName);
        Files.write(filePath, "Test file content".getBytes());

        // when
        InputStreamResource resource = fileService.downloadFile(fileName, testUser);

        // then
        assertNotNull(resource);
        assertTrue(resource.getInputStream().available() > 0);
        Files.deleteIfExists(filePath);
    }

    @Test
    public void DownloadFileFileNotFound() {
        // given
        String fileName = "nonexistent.png";
        // then
        assertThrows(FileNotFoundException.class, () -> {
            fileService.downloadFile(fileName, testUser);
        });
    }
    @Test
    public void GetUserFiles() {
        // given
        List<FileEntity> userFiles = fileService.getUserFiles(secondTestUser);

        // then
        assertEquals(3, userFiles.size());
        for (FileEntity file : userFiles) {
            assertEquals(secondTestUser.getId(), file.getUser().getId());
        }
    }

    @Test
    public void GetAllFiles() {
        // given
        List<FileEntity> allFiles = fileService.getAllFiles();

        // then
        assertEquals(3, allFiles.size());
    }
    @Test
    public void GetFilterAndSortedFilesByUser() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setSizeMin(1000L);
        filterDto.setSortBy("size");
        filterDto.setSortDirection("asc");

        // when
        List<FileEntity> filteredFiles = fileService.getFilterAndSortedFiles(secondTestUser, filterDto);

        // then
        assertEquals(3, filteredFiles.size());
        assertEquals(1000L, filteredFiles.get(0).getSize());
        assertEquals(2000L, filteredFiles.get(1).getSize());
        assertEquals(3000L, filteredFiles.get(2).getSize());
    }
    @Test
    public void GetFilterAndSortedFilesWithoutUser() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setUploadDateFrom(LocalDateTime.now().minusDays(3).withNano(0));
        filterDto.setUploadDateTo(LocalDateTime.now().minusDays(1).withNano(0));
        filterDto.setSortBy("uploadDate");
        filterDto.setSortDirection("desc");

        // when
        List<FileEntity> filteredFiles = fileService.getFilterAndSortedFiles(filterDto);

        // then
        assertEquals(3, filteredFiles.size());
        assertEquals("file4.png", filteredFiles.get(0).getName());
        assertEquals("file5.png", filteredFiles.get(1).getName());
        assertEquals("file6.png", filteredFiles.get(2).getName());
    }
}