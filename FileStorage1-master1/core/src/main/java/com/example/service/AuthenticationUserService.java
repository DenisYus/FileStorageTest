package com.example.service;

import com.example.dao.UserRepository;
import com.example.exception.UserHasBeenBannedException;
import com.example.model.UserStatus;
import com.example.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final KafkaProducerService kafkaProducerService;
    private final JwtService jwtService;

    @Lazy
    public AuthenticationUserService(UserRepository userRepository, KafkaProducerService kafkaProducerService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.jwtService = jwtService;
    }
    @Transactional
    public String generateToken(String email){
        UserDetails userDetails = loadUserByUsername(email);
        return jwtService.generateToken(userDetails);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        } else if (user.getStatus().equals(UserStatus.BLOCKED)) {
            throw new UserHasBeenBannedException("User has been banned");
        }
        kafkaProducerService.sendMailMessage(email, "Login", "You have logged in");

        return new User(user.getEmail(), user.getPassword(), user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getUserRole())).toList());
    }
}
