package com.sms.service;

import com.sms.dto.LoginRequest;
import com.sms.dto.LoginResponse;
import com.sms.entity.Admin;
import com.sms.entity.Student;
import com.sms.repository.AdminRepository;
import com.sms.repository.StudentRepository;
import com.sms.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AdminRepository adminRepository,
                       StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        // Try admin first
        var adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                String token = tokenProvider.generateToken(admin.getId(), admin.getEmail(), "ADMIN");
                return new LoginResponse(token, "ADMIN", admin.getId(), admin.getEmail(), admin.getName());
            }
        }

        // Try student
        var studentOpt = studentRepository.findByEmail(request.getEmail());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (passwordEncoder.matches(request.getPassword(), student.getPassword())) {
                String token = tokenProvider.generateToken(student.getId(), student.getEmail(), "STUDENT");
                return new LoginResponse(token, "STUDENT", student.getId(), student.getEmail(), student.getName());
            }
        }

        throw new RuntimeException("Invalid email or password");
    }
}
