package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.ProfileResponse;
import com.sms.dto.ProfileUpdateRequest;
import com.sms.security.JwtAuthenticationFilter;
import com.sms.security.JwtTokenProvider;
import com.sms.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfileResponse sampleProfile() {
        return new ProfileResponse(1L, "student@sms.com", "Student", "A student",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "student@sms.com", roles = "STUDENT")
    void getProfile_shouldReturn200() throws Exception {
        when(studentService.getProfile("student@sms.com")).thenReturn(sampleProfile());

        mockMvc.perform(get("/api/student/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student@sms.com"))
                .andExpect(jsonPath("$.name").value("Student"));
    }

    @Test
    @WithMockUser(username = "student@sms.com", roles = "STUDENT")
    void updateProfile_shouldReturn200() throws Exception {
        when(studentService.updateProfile(eq("student@sms.com"), any(ProfileUpdateRequest.class)))
                .thenReturn(sampleProfile());

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Updated Student");
        request.setDescription("Updated desc");

        mockMvc.perform(put("/api/student/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // --- Authorization tests ---

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void studentEndpoint_withAdminRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/student/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentEndpoint_withoutAuth_shouldReturn401Or403() throws Exception {
        mockMvc.perform(get("/api/student/profile"))
                .andExpect(status().isUnauthorized().or(status().isForbidden()));
    }
}
