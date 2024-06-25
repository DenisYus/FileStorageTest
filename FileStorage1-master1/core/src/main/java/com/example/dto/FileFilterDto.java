package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileFilterDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime uploadDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime uploadDateTo;
    private Long fileIdMin;
    private Long fileIdMax;
    private Long sizeMin;
    private Long sizeMax;
    private String sortBy;
    private String sortDirection;
}
