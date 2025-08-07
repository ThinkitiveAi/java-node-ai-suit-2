package com.thinkitive.thinkemr.service;

import com.thinkitive.thinkemr.entity.User;
import com.thinkitive.thinkemr.dto.UserCreateRequest;

import java.util.List;

public interface UserService {
    User createUser(UserCreateRequest request);
    User getUserById(Long id);
    List<User> getAllUsers();
} 