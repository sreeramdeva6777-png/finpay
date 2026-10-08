package com.sreeram.finpay.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_success() throws Exception {
        String email = "register-test-" + System.currentTimeMillis() + "@test.com";

        String requestBody = """
                {
                    "name": "Test User",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email);

        mockMvc.perform(post("/users/register")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());
    }
}