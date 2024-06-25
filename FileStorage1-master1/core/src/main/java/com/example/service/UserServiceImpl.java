package com.example.service;

import com.example.dao.UserRepository;
import com.example.exception.UserAlreadyExistsException;
import com.example.model.UserEntity;
import com.example.model.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerUser(UserEntity user) {
        if (!(userRepository.findByEmail(user.getEmail()) == null)){
            throw new UserAlreadyExistsException("User email"+ user.getEmail() +"already exist");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeUserStatus(Integer id, UserStatus status) {
        UserEntity user = getUserById(id);
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public UserEntity getUserById(Integer id) {
        return userRepository.findById(id).get();
    }

    @Override
    @Transactional
    public UserEntity loadUserByUsername(String email) {
        return userRepository.findByEmail(email);
    }


}
