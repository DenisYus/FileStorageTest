package com.example.service;

import com.example.dao.UserRepository;
import com.example.exception.UserHasBeenBannedException;
import com.example.model.RoleEntity;
import com.example.model.UserEntity;
import com.example.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthenticationUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationUserService authenticationUserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void LoadUserByUsername() {
        // given
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setUserRole("ROLE_USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleEntity);
        UserEntity user = new UserEntity();
        user.setEmail("test@mail.ru");
        user.setPassword("password");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(roles);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // when
        UserDetails userDetails = authenticationUserService.loadUserByUsername("test@mail.ru");

        // then
        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void LoadUserByUsername_UserNotFound() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        // then
        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationUserService.loadUserByUsername("nonexistent@mail.ru");
        });
    }

    @Test
    public void LoadUserByUsername_UserBlock() {
        // given
        RoleEntity role = new RoleEntity();
        role.setUserRole("ROLE_USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        UserEntity user = new UserEntity();
        user.setEmail("banned@mail.ru");
        user.setPassword("password");
        user.setStatus(UserStatus.BLOCKED);
        user.setRoles(roles);
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        // then
        assertThrows(UserHasBeenBannedException.class, () -> {
            authenticationUserService.loadUserByUsername("banned@mail.ru");
        });
    }
}