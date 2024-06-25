package com.example.service;

import com.example.BaseIntegrationTest;
import com.example.MockKafkaBeans;
import com.example.dao.UserRepository;
import com.example.model.UserEntity;
import com.example.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@MockKafkaBeans
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceImplTest extends BaseIntegrationTest {
    @Autowired
    private UserService userService;


    @Test
    void registerUser() {
        //given
        var user = UserEntity.builder().email("denis@mail.ru").password("123").status(UserStatus.ACTIVE).build();
        //when
        userService.registerUser(user);
        //then
        var saved = userService.loadUserByUsername("denis@mail.ru");
        assertEquals(user.getEmail(), saved.getEmail());
        assertEquals(user.getPassword(), saved.getPassword());
        assertEquals(user.getStatus(), saved.getStatus());
    }

    @Test
    void changeUserStatus() {
        //given
        var getUser = userService.getUserById(1);
        //when
        userService.changeUserStatus(1, UserStatus.BLOCKED);
        //then
        var getUpdateUser = userService.getUserById(1);
        assertNotEquals(getUser.getStatus(), getUpdateUser.getStatus());

    }

    @Test
    void getUserById() {
        //given
        var user = UserEntity.builder().email("denis1@mail.ru").password("123").status(UserStatus.ACTIVE).build();
        userService.registerUser(user);
        //when
        var getUser = userService.getUserById(5);
        //then
        var saved = userService.loadUserByUsername("denis1@mail.ru");
        assertEquals(saved.getId(), getUser.getId());
    }

    @Test
    void loadUserByUsername() {
        //given
        var user = UserEntity.builder().email("denis2@mail.ru").password("123").status(UserStatus.ACTIVE).build();
        userService.registerUser(user);
        //when
        var saved = userService.loadUserByUsername("denis2@mail.ru");
        //then
        assertEquals(user.getEmail(), saved.getEmail());
        assertEquals(user.getPassword(), saved.getPassword());
        assertEquals(user.getStatus(), saved.getStatus());
    }
}