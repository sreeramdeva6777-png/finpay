package com.sreeram.finpay.controller;

import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.entity.Wallet;
import com.sreeram.finpay.repository.UserRepository;
import com.sreeram.finpay.repository.WalletRepository;
import com.sreeram.finpay.service.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void getBalance_success() throws Exception {

        String email = "wallet-test-" + System.currentTimeMillis() + "@test.com";

        User user = new User();
        user.setName("Integration User");
        user.setEmail(email);
        user.setPassword(
                new BCryptPasswordEncoder().encode("password123")
        );

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("1000.00"));
        wallet.setUser(savedUser);

        walletRepository.save(wallet);

        String token = jwtService.generateToken(email);

        mockMvc.perform(
                get("/wallets/balance")
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk());
    }
}