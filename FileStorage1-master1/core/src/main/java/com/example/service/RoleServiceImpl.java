package com.example.service;

import com.example.dao.RoleRepository;
import com.example.model.RoleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleDAO) {
        this.roleRepository = roleDAO;
    }

    @Override
    public RoleEntity getRole(String userRole) {
        return roleRepository.findByUserRole(userRole);
    }

    @Override
    public RoleEntity getRoleById(Integer id) {
        return roleRepository.findById(id).get();
    }

    @Override
    public List<RoleEntity> allRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void addRole(RoleEntity role) {
        roleRepository.save(role);
    }
}
