package com.example.service;

import com.example.model.RoleEntity;

import java.util.List;

public interface RoleService {


    RoleEntity getRole(String userRole);

    RoleEntity getRoleById(Integer id);

    List<RoleEntity> allRoles();

    void addRole(RoleEntity role);
}
