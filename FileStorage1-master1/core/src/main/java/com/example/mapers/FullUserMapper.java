package com.example.mapers;

import com.example.dto.FullUserDto;
import com.example.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FullUserMapper {
    FullUserMapper INSTANCE = Mappers.getMapper(FullUserMapper.class);
    UserEntity toEntity(FullUserDto argDto);
    FullUserDto toDto(UserEntity argEntity);
}
