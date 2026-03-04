package com.sms.service;

import com.sms.dto.LoginRequest;
import com.sms.dto.LoginResponse;
import com.sms.entity.Admin;
import com.sms.entity.Student;
import com.sms.repository.AdminRepository;
import com.sms.repository.StudentRepository;
import com.sms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private Admin admin;
    private Student student;

    @BeforeEach
    void setUp() {
        admin = new Admin("admin@sms.com", "encodedPassword", "Admin");
        admin.setId(1L);

        student = new Student("student@sms.com", "encodedPassword", "Student");
        student.setId(2L);
    }

    @Test
    void login_asAdmin_shouldReturnAdminResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("admin123");

        when(adminRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);
        when(tokenProvider.generateToken(1L, "admin@sms.com", "ADMIN")).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("ADMIN", response.getRole());
        assertEquals(1L, response.getId());
        assertEquals("admin@sms.com", response.getEmail());
        assertEquals("Admin", response.getName());
    }

    @Test
    void login_asStudent_shouldReturnStudentResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@sms.com");
        request.setPassword("password");

        when(adminRepository.findByEmail("student@sms.com")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("student@sms.com")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(tokenProvider.generateToken(2L, "student@sms.com", "STUDENT")).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("STUDENT", response.getRole());
        assertEquals(2L, response.getId());
        assertEquals("student@sms.com", response.getEmail());
    }

    @Test
    void login_withInvalidEmail_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@sms.com");
        request.setPassword("password");

        when(adminRepository.findByEmail("unknown@sms.com")).thenReturn(Optional.empty());
        when(studentRepository.findByEmail("unknown@sms.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void login_withWrongPassword_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("wrongPassword");

        when(adminRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        when(studentRepository.findByEmail("admin@sms.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }
}
