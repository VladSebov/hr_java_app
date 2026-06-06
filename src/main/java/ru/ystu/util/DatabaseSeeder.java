package ru.ystu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import ru.ystu.dto.User;
import ru.ystu.repository.UserRepository;

public class DatabaseSeeder {
    private static final Logger log = LogManager.getLogger(DatabaseSeeder.class);

    public static void seedData(UserRepository userRepository) {
        String adminUsername = "admin";
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            log.info("Admin user not found. Seeding default admin...");
            User defaultAdmin = new User();
            defaultAdmin.setUsername(adminUsername);
            defaultAdmin.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            defaultAdmin.setRole("ADMIN");
            userRepository.save(defaultAdmin);
            log.info("Default admin created (username: 'admin', password: 'admin123').");
        }

        String employeeUsername = "worker1";
        if (userRepository.findByUsername(employeeUsername).isEmpty()) {
            log.info("Employee user not found. Seeding default employee...");
            User defaultEmployee = new User();
            defaultEmployee.setUsername(employeeUsername);
            defaultEmployee.setPasswordHash(BCrypt.hashpw("worker123", BCrypt.gensalt()));
            defaultEmployee.setRole("EMPLOYEE");
            userRepository.save(defaultEmployee);
            log.info("Default employee created (username: 'worker1', password: 'worker123').");
        }

        log.info("Database verification completed successfully.");
    }
}