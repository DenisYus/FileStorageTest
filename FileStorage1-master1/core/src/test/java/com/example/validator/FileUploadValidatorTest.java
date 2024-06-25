package com.example.validator;

import com.example.BaseIntegrationTest;
import com.example.MockKafkaBeans;
import com.example.exception.FileExtensionException;
import com.example.exception.FileNameException;
import com.example.exception.FileSizeException;
import lombok.Value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@MockKafkaBeans
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "file.allow.extensions=png,jpg",
        "file.max.size=10485760"
})
public class FileUploadValidatorTest extends BaseIntegrationTest {

    private FileUploadValidator fileUploadValidator;
    @Autowired
    private Environment environment;

    @BeforeEach
    public void setUp() {
        String allowedExtensions = environment.getProperty("file.allow.extensions");
        long maxFileSize = Long.parseLong(environment.getProperty("file.max.size"));
        fileUploadValidator = new FileUploadValidator(allowedExtensions, maxFileSize);
        fileUploadValidator.init();
    }

    @Test
    public void testValidateWithEmptyFileName() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "", "image/jpeg", new byte[10]);
        //then
        assertThrows(FileNameException.class, () -> fileUploadValidator.validate(file));
    }

    @Test
    public void testValidateWithUnsupportedExtension() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "file.unsupported", "application/octet-stream", new byte[10]);
        //then
        assertThrows(FileExtensionException.class, () -> fileUploadValidator.validate(file));
    }

    @Test
    public void testValidateWithExceedingFileSize() {
        //given
        byte[] content = new byte[10485761];
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg", "image/jpeg", content);
        //then
        assertThrows(FileSizeException.class, () -> fileUploadValidator.validate(file));
    }

    @Test
    public void testValidateWithValidFile() {
        //given
        byte[] content = new byte[10485760];
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg", "image/jpeg", content);
        //then
        assertDoesNotThrow(() -> fileUploadValidator.validate(file));
    }

    @Test
    public void testValidateWithNoExtension() {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "file", "image/jpeg", new byte[10]);
        //then
        assertThrows(FileExtensionException.class, () -> fileUploadValidator.validate(file));
    }
}