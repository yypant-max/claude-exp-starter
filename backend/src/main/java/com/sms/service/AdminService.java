package com.sms.service;

import com.sms.dto.*;
import com.sms.entity.Admin;
import com.sms.entity.Student;
import com.sms.repository.AdminRepository;
import com.sms.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        StudentRepository studentRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Admin profile ---

    public ProfileResponse getProfile(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return toProfileResponse(admin);
    }

    public ProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (request.getName() != null) admin.setName(request.getName());
        if (request.getDescription() != null) admin.setDescription(request.getDescription());
        admin = adminRepository.save(admin);
        return toProfileResponse(admin);
    }

    // --- Student management ---

    public List<ProfileResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toProfileResponse)
                .collect(Collectors.toList());
    }

    public ProfileResponse addStudent(StudentCreateRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Student with this email already exists");
        }
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("This email is already used by an admin");
        }

        Student student = new Student(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );
        student = studentRepository.save(student);
        return toProfileResponse(student);
    }

    public ProfileResponse updateStudent(Long studentId, StudentUpdateRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(student.getEmail()) &&
                    (studentRepository.existsByEmail(request.getEmail()) ||
                     adminRepository.existsByEmail(request.getEmail()))) {
                throw new RuntimeException("Email is already in use");
            }
            student.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        student = studentRepository.save(student);
        return toProfileResponse(student);
    }

    public void removeStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found");
        }
        studentRepository.deleteById(studentId);
    }

    // --- Admin management ---

    public List<ProfileResponse> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::toProfileResponse)
                .collect(Collectors.toList());
    }

    public ProfileResponse addAdmin(AdminCreateRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Admin with this email already exists");
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("This email is already used by a student");
        }

        Admin admin = new Admin(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName()
        );
        admin = adminRepository.save(admin);
        return toProfileResponse(admin);
    }

    public void removeAdmin(Long adminId, String currentAdminEmail) {
        Admin target = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (target.getEmail().equals(currentAdminEmail)) {
            throw new RuntimeException("Cannot remove yourself");
        }
        adminRepository.deleteById(adminId);
    }

    // --- Helpers ---

    private ProfileResponse toProfileResponse(Admin admin) {
        return new ProfileResponse(admin.getId(), admin.getEmail(), admin.getName(),
                admin.getDescription(), admin.getCreatedAt(), admin.getUpdatedAt());
    }

    private ProfileResponse toProfileResponse(Student student) {
        return new ProfileResponse(student.getId(), student.getEmail(), student.getName(),
                student.getDescription(), student.getCreatedAt(), student.getUpdatedAt());
    }
}
