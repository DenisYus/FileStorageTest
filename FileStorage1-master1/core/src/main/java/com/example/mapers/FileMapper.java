package com.example.mapers;

import com.example.dto.FileDto;
import com.example.model.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);
    FileDto toDto(FileEntity fileEntity);
    List<FileDto> toDtoList(List<FileEntity> fileEntities);
}
