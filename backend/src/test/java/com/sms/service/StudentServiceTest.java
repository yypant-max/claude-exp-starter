package com.sms.service;

import com.sms.dto.ProfileResponse;
import com.sms.dto.ProfileUpdateRequest;
import com.sms.entity.Student;
import com.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("student@sms.com", "encodedPassword", "Test Student");
        student.setId(1L);
    }

    @Test
    void getProfile_shouldReturnProfile() {
        when(studentRepository.findByEmail("student@sms.com")).thenReturn(Optional.of(student));

        ProfileResponse response = studentService.getProfile("student@sms.com");

        assertEquals("student@sms.com", response.getEmail());
        assertEquals("Test Student", response.getName());
    }

    @Test
    void getProfile_withUnknownEmail_shouldThrow() {
        when(studentRepository.findByEmail("unknown@sms.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getProfile("unknown@sms.com"));
    }

    @Test
    void updateProfile_shouldUpdateNameAndDescription() {
        when(studentRepository.findByEmail("student@sms.com")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Updated Name");
        request.setDescription("A description");

        ProfileResponse response = studentService.updateProfile("student@sms.com", request);

        assertEquals("Updated Name", student.getName());
        assertEquals("A description", student.getDescription());
        verify(studentRepository).save(student);
    }

    @Test
    void updateProfile_withNullFields_shouldNotUpdate() {
        when(studentRepository.findByEmail("student@sms.com")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        ProfileUpdateRequest request = new ProfileUpdateRequest();

        studentService.updateProfile("student@sms.com", request);

        assertEquals("Test Student", student.getName());
        assertNull(student.getDescription());
    }
}
