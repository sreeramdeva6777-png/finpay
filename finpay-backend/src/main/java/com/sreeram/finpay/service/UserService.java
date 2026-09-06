package com.sreeram.finpay.service;

import org.springframework.stereotype.Service;

import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.repository.UserRepository;
import com.sreeram.finpay.exception.EmailAlreadyExistsException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.sreeram.finpay.exception.InvalidCredentialsException;
import com.sreeram.finpay.dto.UserResponse;
import com.sreeram.finpay.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

    public UserService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }	

    public UserResponse register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                null
        );
    }
    
    public UserResponse login(String email, String password) {
    	
    	authenticationManager.authenticate(
    	        new UsernamePasswordAuthenticationToken(
    	                email,
    	                password
    	        )
    	);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(email);
        System.out.println("JWT: " + token);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
    }	
}