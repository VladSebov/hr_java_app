package ru.ystu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import ru.ystu.dto.User;
import ru.ystu.repository.UserRepository;

public class DatabaseSeeder {
    private static final Logger log = LogManager.getLogger(DatabaseSeeder.class);

    public static void seedAdmin(UserRepository userRepository) {
        String adminUsername = "admin";

        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            log.info("Admin user not found. Starting automatic database seeding...");

            User defaultAdmin = new User();
            defaultAdmin.setUsername(adminUsername);

            String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
            defaultAdmin.setPasswordHash(hashedPassword);

            defaultAdmin.setRole("ADMIN");

            userRepository.save(defaultAdmin);

            log.info("Database successfully seeded! Default admin created (username: 'admin', password: 'admin123').");
        } else {
            log.info("Database verification completed: Admin user already exists.");
        }
    }
}