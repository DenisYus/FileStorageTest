package com.example.dao;


import com.example.model.FileEntity;
import com.example.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Integer>, JpaSpecificationExecutor<FileEntity> {
    List<FileEntity> findByUser(UserEntity user);

}
