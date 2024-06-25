package com.example.controller;

import com.example.BaseIntegrationTest;
import com.example.dto.AuthenticationRequest;
import com.example.dto.AuthenticationResponse;
import com.example.dto.FullUserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest extends BaseIntegrationTest {

    @Test
    public void Register() throws Exception {
        //given
        FullUserDto userDto = new FullUserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        //when
        URI uri = serverUrl("/auth/register");
        ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(uri, userDto, AuthenticationResponse.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
    }

    @Test
    public void RegisterUserAlreadyExists() throws Exception {
        //given
        FullUserDto userDto = new FullUserDto();
        userDto.setEmail("existing@example.com");
        userDto.setPassword("password");
        URI uri = serverUrl("/auth/register");
        restTemplate.postForEntity(uri, userDto, AuthenticationResponse.class);

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(uri, userDto, String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void Authenticate() throws Exception {
        //given
        FullUserDto userDto = new FullUserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        URI registerUri = serverUrl("/auth/register");
        restTemplate.postForEntity(registerUri, userDto, AuthenticationResponse.class);
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        URI authUri = serverUrl("/auth/authentication");
        //when
        ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(authUri, request, AuthenticationResponse.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
    }

    @Test
    public void AuthenticateInvalidCredentials() throws Exception {
        //given
        FullUserDto userDto = new FullUserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        URI registerUri = serverUrl("/auth/register");
        restTemplate.postForEntity(registerUri, userDto, AuthenticationResponse.class);
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("bad-password");
        URI authUri = serverUrl("/auth/authentication");
        //when
        ResponseEntity<String> response = restTemplate.postForEntity(authUri, request, String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void AuthenticateUserNotFound() throws Exception {
        //given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password");
        URI authUri = serverUrl("/auth/authentication");
        //when
        ResponseEntity<String> response = restTemplate.postForEntity(authUri, request, String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
