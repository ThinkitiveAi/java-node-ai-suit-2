package com.thinkitive.thinkemr.service.impl;

import com.thinkitive.thinkemr.entity.User;
import com.thinkitive.thinkemr.repository.UserRepository;
import com.thinkitive.thinkemr.service.UserService;
import com.thinkitive.thinkemr.dto.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(UserCreateRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(passwordEncoder.encode(request.getPassword()))
            .createdAt(Instant.now())
            .build();
        return userRepository.save(user);
    }
} 