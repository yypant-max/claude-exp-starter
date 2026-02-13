package com.sms.config;

import com.sms.entity.Admin;
import com.sms.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin defaultAdmin = new Admin(
                    "admin@sms.com",
                    passwordEncoder.encode("admin123"),
                    "Default Admin"
            );
            defaultAdmin.setDescription("System administrator");
            adminRepository.save(defaultAdmin);
            System.out.println("Default admin created: admin@sms.com / admin123");
        }
    }
}
