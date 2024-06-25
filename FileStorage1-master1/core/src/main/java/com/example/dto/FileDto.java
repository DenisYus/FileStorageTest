package com.example.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private Long id;

    private String name;

    private Long size;

    private LocalDateTime uploadDate;
}
