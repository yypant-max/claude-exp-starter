package com.sms.service;

import com.sms.dto.ProfileResponse;
import com.sms.dto.ProfileUpdateRequest;
import com.sms.entity.Student;
import com.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public ProfileResponse getProfile(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return new ProfileResponse(student.getId(), student.getEmail(), student.getName(),
                student.getDescription(), student.getCreatedAt(), student.getUpdatedAt());
    }

    public ProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (request.getName() != null) student.setName(request.getName());
        if (request.getDescription() != null) student.setDescription(request.getDescription());
        student = studentRepository.save(student);
        return new ProfileResponse(student.getId(), student.getEmail(), student.getName(),
                student.getDescription(), student.getCreatedAt(), student.getUpdatedAt());
    }
}
