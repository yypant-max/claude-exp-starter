package com.sms.controller;

import com.sms.dto.*;
import com.sms.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --- Admin profile ---

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(adminService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(Authentication auth,
                                                          @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateProfile(auth.getName(), request));
    }

    // --- Student management ---

    @GetMapping("/students")
    public ResponseEntity<List<ProfileResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @PostMapping("/students")
    public ResponseEntity<ProfileResponse> addStudent(@Valid @RequestBody StudentCreateRequest request) {
        return ResponseEntity.ok(adminService.addStudent(request));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<ProfileResponse> updateStudent(@PathVariable Long id,
                                                          @Valid @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateStudent(id, request));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> removeStudent(@PathVariable Long id) {
        adminService.removeStudent(id);
        return ResponseEntity.noContent().build();
    }

    // --- Admin management ---

    @GetMapping("/admins")
    public ResponseEntity<List<ProfileResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PostMapping("/admins")
    public ResponseEntity<ProfileResponse> addAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.ok(adminService.addAdmin(request));
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<Void> removeAdmin(@PathVariable Long id, Authentication auth) {
        adminService.removeAdmin(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
