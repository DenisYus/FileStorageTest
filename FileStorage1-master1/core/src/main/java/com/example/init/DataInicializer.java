package com.example.init;

import com.example.model.RoleEntity;
import com.example.model.UserEntity;
import com.example.service.RoleService;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.Set;


@Component
public class DataInicializer {
    private final UserService userService;
    private final RoleService roleService;

    public DataInicializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @PostConstruct
    private void dataBase() {
        RoleEntity roleModerator = RoleEntity.builder().userRole("ROLE_MODERATOR").build();
        RoleEntity roleUser = RoleEntity.builder().userRole("ROLE_USER").build();
        Set<RoleEntity> adminSet = new HashSet<>();
        Set<RoleEntity> userSet = new HashSet<>();

        roleService.addRole(roleModerator);
        roleService.addRole(roleUser);
        adminSet.add(roleModerator);
        userSet.add(roleUser);
        UserEntity newUser = UserEntity.builder().email("user@mail.com")
                .password("123").roles(userSet)
                .build();
        UserEntity admin = UserEntity.builder().email("moderator@mail.com")
                .password("123").roles(adminSet)
                .build();
        userService.registerUser(newUser);
        userService.registerUser(admin);
    }

}
