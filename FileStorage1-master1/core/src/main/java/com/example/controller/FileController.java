package com.example.controller;

import com.example.dto.FileDto;
import com.example.dto.FileFilterDto;
import com.example.mapers.FileMapper;
import com.example.model.FileEntity;
import com.example.model.UserEntity;
import com.example.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private static final String HEADER_VALUES = "attachment; filename=\"%s\"";

    public final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@AuthenticationPrincipal UserEntity user,
                                             @RequestParam("files")List<MultipartFile> files){
        fileService.uploadFiles(user.getEmail(), files);
        return ResponseEntity.ok("File uploaded");
    }
    @GetMapping("/user-files")
    public ResponseEntity<List<FileDto>> getUserFiles(@AuthenticationPrincipal UserEntity user,
                                                      @RequestBody FileFilterDto filterDto){
        List<FileDto> fileDtos = FileMapper.INSTANCE.toDtoList(fileService.getFilterAndSortedFiles(user, filterDto));
        return ResponseEntity.ok(fileDtos);
    }
    @GetMapping("/all-files")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<List<FileDto>> getAllFiles (@RequestBody FileFilterDto filterDto){
        List<FileDto> fileDtos = FileMapper.INSTANCE.toDtoList(fileService.getFilterAndSortedFiles(filterDto));
        return new ResponseEntity<>(fileDtos, HttpStatus.OK);
    }

    // FIXME: вообще лучше дробить файл на мелкие чанки, чтобы в будущем поддерживать загрузку больших файлов, но не успел
    @GetMapping("/download/{fileName}")
    public  ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName, @AuthenticationPrincipal UserEntity user) throws IOException {

        InputStreamResource resource = fileService.downloadFile(fileName, user);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format(HEADER_VALUES,fileName))
                .body(resource);

    }
}
