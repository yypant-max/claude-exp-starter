package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.*;
import com.sms.security.JwtAuthenticationFilter;
import com.sms.security.JwtTokenProvider;
import com.sms.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfileResponse sampleProfile() {
        return new ProfileResponse(1L, "admin@sms.com", "Admin", "desc",
                LocalDateTime.now(), LocalDateTime.now());
    }

    private ProfileResponse sampleStudentProfile() {
        return new ProfileResponse(1L, "student@sms.com", "Student", null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    // --- Profile ---

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void getProfile_shouldReturn200() throws Exception {
        when(adminService.getProfile("admin@sms.com")).thenReturn(sampleProfile());

        mockMvc.perform(get("/api/admin/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@sms.com"));
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void updateProfile_shouldReturn200() throws Exception {
        when(adminService.updateProfile(eq("admin@sms.com"), any(ProfileUpdateRequest.class)))
                .thenReturn(sampleProfile());

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Updated");

        mockMvc.perform(put("/api/admin/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // --- Student management ---

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void getAllStudents_shouldReturn200() throws Exception {
        when(adminService.getAllStudents()).thenReturn(List.of(sampleStudentProfile()));

        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("student@sms.com"));
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void addStudent_shouldReturn200() throws Exception {
        when(adminService.addStudent(any(StudentCreateRequest.class))).thenReturn(sampleStudentProfile());

        StudentCreateRequest request = new StudentCreateRequest();
        request.setEmail("student@sms.com");
        request.setPassword("password123");
        request.setName("Student");

        mockMvc.perform(post("/api/admin/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student@sms.com"));
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void addStudent_withInvalidEmail_shouldReturn400() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/admin/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void addStudent_withShortPassword_shouldReturn400() throws Exception {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setEmail("test@sms.com");
        request.setPassword("123");

        mockMvc.perform(post("/api/admin/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void updateStudent_shouldReturn200() throws Exception {
        when(adminService.updateStudent(eq(1L), any(StudentUpdateRequest.class)))
                .thenReturn(sampleStudentProfile());

        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setEmail("updated@sms.com");

        mockMvc.perform(put("/api/admin/students/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void removeStudent_shouldReturn204() throws Exception {
        doNothing().when(adminService).removeStudent(1L);

        mockMvc.perform(delete("/api/admin/students/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    // --- Admin management ---

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void getAllAdmins_shouldReturn200() throws Exception {
        when(adminService.getAllAdmins()).thenReturn(List.of(sampleProfile()));

        mockMvc.perform(get("/api/admin/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@sms.com"));
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void addAdmin_shouldReturn200() throws Exception {
        when(adminService.addAdmin(any(AdminCreateRequest.class))).thenReturn(sampleProfile());

        AdminCreateRequest request = new AdminCreateRequest();
        request.setEmail("newadmin@sms.com");
        request.setPassword("password123");
        request.setName("New Admin");

        mockMvc.perform(post("/api/admin/admins")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@sms.com", roles = "ADMIN")
    void removeAdmin_shouldReturn204() throws Exception {
        doNothing().when(adminService).removeAdmin(eq(2L), eq("admin@sms.com"));

        mockMvc.perform(delete("/api/admin/admins/2").with(csrf()))
                .andExpect(status().isNoContent());
    }

    // --- Authorization tests ---

    @Test
    @WithMockUser(username = "student@sms.com", roles = "STUDENT")
    void adminEndpoint_withStudentRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_withoutAuth_shouldReturn401Or403() throws Exception {
        mockMvc.perform(get("/api/admin/profile"))
                .andExpect(status().isUnauthorized().or(status().isForbidden()));
    }
}
