package com.example.controller;

import com.example.BaseIntegrationTest;
import com.example.MockKafkaBeans;
import com.example.dao.RoleRepository;
import com.example.dao.UserRepository;
import com.example.dto.AuthenticationRequest;
import com.example.dto.AuthenticationResponse;
import com.example.dto.FullUserDto;
import com.example.dto.RoleDto;
import com.example.mapers.RoleMapper;
import com.example.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@MockKafkaBeans
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;


    @Test
    public void Register() throws Exception {
        //given
        FullUserDto user = new FullUserDto();
        user.setEmail("test@mail.ru");
        user.setPassword("password");

        Set<RoleDto> roleDtos = new HashSet<>();
        roleDtos.add(RoleMapper.INSTANCE.toDto(roleService.getRoleById(1)));
        user.setRoles(roleDtos);
        //when
        URI uri = serverUrl("/auth/register");
        ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(uri, user, AuthenticationResponse.class);
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
        Set<RoleDto> roleDtos = new HashSet<>();
        roleDtos.add(RoleMapper.INSTANCE.toDto(roleService.getRoleById(1)));
        userDto.setRoles(roleDtos);
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
        userDto.setEmail("test1@example.com");
        userDto.setPassword("password");
        Set<RoleDto> roleDtos = new HashSet<>();
        roleDtos.add(RoleMapper.INSTANCE.toDto(roleService.getRoleById(1)));
        userDto.setRoles(roleDtos);
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
        Set<RoleDto> roleDtos = new HashSet<>();
        roleDtos.add(RoleMapper.INSTANCE.toDto(roleService.getRoleById(1)));
        userDto.setRoles(roleDtos);
        URI registerUri = serverUrl("/auth/register");
        restTemplate.postForEntity(registerUri, userDto, AuthenticationResponse.class);
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("bad-password");
        URI authUri = serverUrl("/auth/authentication");
        //when
        ResponseEntity<String> response = restTemplate.postForEntity(authUri, request, String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
