package com.example.controller;

import com.example.BaseIntegrationTest;
import com.example.model.UserStatus;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpStatus.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest extends BaseIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean(name = "userService")
    private UserService userService;
    private String basUrl;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        basUrl = String.format("http://localhost:%s/api/users", port);
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"ROLE_MODERATOR"})
    public void ChangeUserStatus(){
        doNothing().when(userService).changeUserStatus(anyInt(), any(UserStatus.class));
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                basUrl+"/change-status/{userId}?status={status}",
                HttpMethod.POST, entity, Void.class, 1, UserStatus.ACTIVE);
        assertEquals(OK, response.getStatusCode(), "Status code");

    }
    @Test
    @WithMockUser(roles = "ROLE_USER")
    public void ChangeUserStatusForbidden(){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                basUrl+"/change-status/{userId}?status={status}",
                HttpMethod.POST, entity, Void.class, 1, UserStatus.ACTIVE);
        assertEquals(FORBIDDEN, response.getStatusCode(), "Status code");
    }
    @Test
    public void ChangeUserStatusUnauthorized(){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(
                basUrl+"/change-status/{userId}?status={status}",
                HttpMethod.POST, entity, Void.class, 1, UserStatus.ACTIVE);
        assertEquals(UNAUTHORIZED, response.getStatusCode(), "Status code");
    }

}