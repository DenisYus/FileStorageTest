package com.example.mapers;

import com.example.dto.RoleDto;
import com.example.model.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleEntity toEntity(RoleDto argDto);

    RoleDto toDto(RoleEntity argEntity);

    List<RoleEntity> toEntity(List<RoleDto> argDto);

    List<RoleDto> toDto(List<RoleEntity> argEntity);
}
