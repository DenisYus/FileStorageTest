package com.example.service;

import com.example.model.UserEntity;
import com.example.model.UserStatus;

public interface UserService {
    void registerUser(UserEntity user);

    void changeUserStatus(Integer id, UserStatus status);

    UserEntity getUserById(Integer id);
    UserEntity loadUserByUsername(String email);

}
