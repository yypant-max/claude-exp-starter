package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.LoginRequest;
import com.sms.dto.LoginResponse;
import com.sms.security.JwtAuthenticationFilter;
import com.sms.security.JwtTokenProvider;
import com.sms.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_withValidCredentials_shouldReturn200() throws Exception {
        LoginResponse loginResponse = new LoginResponse("jwt-token", "ADMIN", 1L, "admin@sms.com", "Admin");
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.email").value("admin@sms.com"));
    }

    @Test
    void login_withInvalidCredentials_shouldReturn400() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid email or password"));

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankEmail_shouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withInvalidEmailFormat_shouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("not-an-email");
        request.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankPassword_shouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
