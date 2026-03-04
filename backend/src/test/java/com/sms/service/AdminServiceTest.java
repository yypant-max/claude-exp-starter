package com.sms.service;

import com.sms.dto.*;
import com.sms.entity.Admin;
import com.sms.entity.Student;
import com.sms.repository.AdminRepository;
import com.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;
    private Student student;

    @BeforeEach
    void setUp() {
        admin = new Admin("admin@sms.com", "encodedPassword", "Admin User");
        admin.setId(1L);

        student = new Student("student@sms.com", "encodedPassword", "Student User");
        student.setId(1L);
    }

    // --- Profile tests ---

    @Test
    void getProfile_shouldReturnProfile() {
        when(adminRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(admin));

        ProfileResponse response = adminService.getProfile("admin@sms.com");

        assertEquals("admin@sms.com", response.getEmail());
        assertEquals("Admin User", response.getName());
    }

    @Test
    void getProfile_withUnknownEmail_shouldThrow() {
        when(adminRepository.findByEmail("unknown@sms.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getProfile("unknown@sms.com"));
    }

    @Test
    void updateProfile_shouldUpdateNameAndDescription() {
        when(adminRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Desc");

        ProfileResponse response = adminService.updateProfile("admin@sms.com", request);

        assertEquals("Updated Name", admin.getName());
        assertEquals("Updated Desc", admin.getDescription());
        verify(adminRepository).save(admin);
    }

    @Test
    void updateProfile_withNullFields_shouldNotUpdate() {
        when(adminRepository.findByEmail("admin@sms.com")).thenReturn(Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        ProfileUpdateRequest request = new ProfileUpdateRequest();

        adminService.updateProfile("admin@sms.com", request);

        assertEquals("Admin User", admin.getName());
        assertNull(admin.getDescription());
    }

    // --- Student management tests ---

    @Test
    void getAllStudents_shouldReturnAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<ProfileResponse> students = adminService.getAllStudents();

        assertEquals(1, students.size());
        assertEquals("student@sms.com", students.get(0).getEmail());
    }

    @Test
    void addStudent_shouldCreateStudent() {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setEmail("new@sms.com");
        request.setPassword("password123");
        request.setName("New Student");

        when(studentRepository.existsByEmail("new@sms.com")).thenReturn(false);
        when(adminRepository.existsByEmail("new@sms.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(2L);
            return s;
        });

        ProfileResponse response = adminService.addStudent(request);

        assertEquals("new@sms.com", response.getEmail());
        assertEquals("New Student", response.getName());
    }

    @Test
    void addStudent_withDuplicateStudentEmail_shouldThrow() {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setEmail("student@sms.com");
        request.setPassword("password123");

        when(studentRepository.existsByEmail("student@sms.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> adminService.addStudent(request));
        assertEquals("Student with this email already exists", ex.getMessage());
    }

    @Test
    void addStudent_withAdminEmail_shouldThrow() {
        StudentCreateRequest request = new StudentCreateRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("password123");

        when(studentRepository.existsByEmail("admin@sms.com")).thenReturn(false);
        when(adminRepository.existsByEmail("admin@sms.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> adminService.addStudent(request));
        assertEquals("This email is already used by an admin", ex.getMessage());
    }

    @Test
    void updateStudent_shouldUpdateEmail() {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setEmail("updated@sms.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmail("updated@sms.com")).thenReturn(false);
        when(adminRepository.existsByEmail("updated@sms.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        adminService.updateStudent(1L, request);

        assertEquals("updated@sms.com", student.getEmail());
    }

    @Test
    void updateStudent_withDuplicateEmail_shouldThrow() {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setEmail("existing@sms.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmail("existing@sms.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminService.updateStudent(1L, request));
    }

    @Test
    void updateStudent_withSameEmail_shouldNotThrow() {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setEmail("student@sms.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        assertDoesNotThrow(() -> adminService.updateStudent(1L, request));
    }

    @Test
    void updateStudent_withPassword_shouldEncodePassword() {
        StudentUpdateRequest request = new StudentUpdateRequest();
        request.setPassword("newPassword");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncoded");
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        adminService.updateStudent(1L, request);

        assertEquals("newEncoded", student.getPassword());
    }

    @Test
    void removeStudent_shouldDeleteStudent() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        adminService.removeStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void removeStudent_withNonExistentId_shouldThrow() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.removeStudent(99L));
    }

    // --- Admin management tests ---

    @Test
    void getAllAdmins_shouldReturnAllAdmins() {
        when(adminRepository.findAll()).thenReturn(List.of(admin));

        List<ProfileResponse> admins = adminService.getAllAdmins();

        assertEquals(1, admins.size());
        assertEquals("admin@sms.com", admins.get(0).getEmail());
    }

    @Test
    void addAdmin_shouldCreateAdmin() {
        AdminCreateRequest request = new AdminCreateRequest();
        request.setEmail("newadmin@sms.com");
        request.setPassword("password123");
        request.setName("New Admin");

        when(adminRepository.existsByEmail("newadmin@sms.com")).thenReturn(false);
        when(studentRepository.existsByEmail("newadmin@sms.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> {
            Admin a = inv.getArgument(0);
            a.setId(2L);
            return a;
        });

        ProfileResponse response = adminService.addAdmin(request);

        assertEquals("newadmin@sms.com", response.getEmail());
    }

    @Test
    void addAdmin_withDuplicateEmail_shouldThrow() {
        AdminCreateRequest request = new AdminCreateRequest();
        request.setEmail("admin@sms.com");
        request.setPassword("password123");

        when(adminRepository.existsByEmail("admin@sms.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminService.addAdmin(request));
    }

    @Test
    void removeAdmin_shouldDeleteAdmin() {
        Admin targetAdmin = new Admin("other@sms.com", "encoded", "Other Admin");
        targetAdmin.setId(2L);

        when(adminRepository.findById(2L)).thenReturn(Optional.of(targetAdmin));

        adminService.removeAdmin(2L, "admin@sms.com");

        verify(adminRepository).deleteById(2L);
    }

    @Test
    void removeAdmin_self_shouldThrow() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.removeAdmin(1L, "admin@sms.com"));
        assertEquals("Cannot remove yourself", ex.getMessage());
    }
}
