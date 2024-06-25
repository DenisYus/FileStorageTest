package com.example.util;

import com.example.BaseIntegrationTest;
import com.example.MockKafkaBeans;
import com.example.dao.FileRepository;
import com.example.dao.UserRepository;
import com.example.dto.FileFilterDto;
import com.example.model.FileEntity;
import com.example.model.UserEntity;
import com.example.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@MockKafkaBeans
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileSpecificationTest extends BaseIntegrationTest {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        fileRepository.deleteAll();
        userRepository.deleteAll();

        testUser = UserEntity.builder()
                .email("test1@mail.ru")
                .password("123")
                .status(UserStatus.ACTIVE)
                .build();
        testUser = userRepository.save(testUser);


        fileRepository.save(FileEntity.builder()
                .name("file1.png")
                .size(1000L)
                .uploadDate(LocalDateTime.now().minusDays(1).withNano(0))
                .user(testUser)
                .build());

        fileRepository.save(FileEntity.builder()
                .name("file2.png")
                .size(2000L)
                .uploadDate(LocalDateTime.now().minusDays(2).withNano(0))
                .user(testUser)
                .build());

        fileRepository.save(FileEntity.builder()
                .name("file3.png")
                .size(3000L)
                .uploadDate(LocalDateTime.now().minusDays(3).withNano(0))
                .user(testUser)
                .build());
    }

    @Test
    public void testGetFilesByFilterUploadDateFrom() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setUploadDateFrom(LocalDateTime.now().minusDays(2).withNano(0));

        // when
        List<FileEntity> filteredFiles = fileRepository.findAll(FileSpecification.getFilesByFilter(filterDto));

        // then
        assertEquals(2, filteredFiles.size());
    }

    @Test
    public void testGetFilesByFilterUploadDateTo() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setUploadDateTo(LocalDateTime.now().minusDays(2));

        // when
        List<FileEntity> filteredFiles = fileRepository.findAll(FileSpecification.getFilesByFilter(filterDto));

        // then
        assertEquals(2, filteredFiles.size());
    }

    @Test
    public void testGetFilesByFilterSizeMin() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setSizeMin(2000L);

        // when
        List<FileEntity> filteredFiles = fileRepository.findAll(FileSpecification.getFilesByFilter(filterDto));

        // then
        assertEquals(2, filteredFiles.size());
    }

    @Test
    public void testGetFilesByFilterSizeMax() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setSizeMax(2000L);

        // when
        List<FileEntity> filteredFiles = fileRepository.findAll(FileSpecification.getFilesByFilter(filterDto));

        // then
        assertEquals(2, filteredFiles.size());
    }

    @Test
    public void testGetFilesByFilterCombined() {
        // given
        FileFilterDto filterDto = new FileFilterDto();
        filterDto.setUploadDateFrom(LocalDateTime.now().minusDays(3));
        filterDto.setUploadDateTo(LocalDateTime.now().minusDays(1));
        filterDto.setSizeMin(1500L);
        filterDto.setSizeMax(2500L);

        // when
        List<FileEntity> filteredFiles = fileRepository.findAll(FileSpecification.getFilesByFilter(filterDto));

        // then
        assertEquals(1, filteredFiles.size());
    }
}