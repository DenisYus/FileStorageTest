package com.example.dao;

import com.example.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    RoleEntity findByUserRole(String userRole);

}
