package com.example.controller;

import com.example.model.UserStatus;
import com.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PostMapping("/change-status/{userId}")
    public ResponseEntity<Void> changeUserStatus(@PathVariable Integer userId, @RequestParam UserStatus status){
        userService.changeUserStatus(userId, status);
        return ResponseEntity.ok().build();
    }
}
