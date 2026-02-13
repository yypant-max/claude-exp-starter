package com.sms.controller;

import com.sms.dto.ProfileResponse;
import com.sms.dto.ProfileUpdateRequest;
import com.sms.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(studentService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(Authentication auth,
                                                          @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateProfile(auth.getName(), request));
    }
}
