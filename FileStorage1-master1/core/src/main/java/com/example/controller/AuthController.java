package com.example.controller;

import com.example.dto.AuthenticationRequest;
import com.example.dto.AuthenticationResponse;
import com.example.dto.FullUserDto;
import com.example.mapers.FullUserMapper;
import com.example.model.UserEntity;
import com.example.security.JwtService;
import com.example.service.AuthenticationUserService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationUserService authenticationUserService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody FullUserDto user) {
        UserEntity userEntity = FullUserMapper.INSTANCE.toEntity(user);
        userService.registerUser(userEntity);
        var jwtToken = authenticationUserService.generateToken(user.getEmail());
        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).build());
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = authenticationUserService.loadUserByUsername(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).build());
    }
}
