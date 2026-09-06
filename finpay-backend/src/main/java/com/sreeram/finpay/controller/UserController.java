package com.sreeram.finpay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.service.UserService;
import jakarta.validation.Valid;
import com.sreeram.finpay.dto.UserResponse;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public UserResponse register(@Valid @RequestBody User user) {
        return userService.register(user);
    }
    
    @PostMapping("/users/login")
    public UserResponse login(@RequestBody User user) {
        return userService.login(user.getEmail(), user.getPassword());
    }
}