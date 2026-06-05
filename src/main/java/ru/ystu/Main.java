package ru.ystu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

@Log4j2
public class Main {

    public static void main(String[] args) {
        log.info("=== Starting environment verification ===");

        try {
            TestUser user = new TestUser();
            user.setId(1L);
            user.setName("John Doe");
            user.setCreatedAt(LocalDateTime.now());
            log.info("Lombok verification success. Object created: {}", user);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(user);
            log.info("Jackson verification success. JSON output: {}", json);

        } catch (Exception e) {
            log.error("Error during Lombok or Jackson verification: ", e);
        }

        String dbHost = getEnvOrDefault("DB_HOST", "localhost");
        String dbPort = getEnvOrDefault("DB_PORT", "5432");
        String dbName = getEnvOrDefault("DB_NAME", "hr_database");
        String dbUser = getEnvOrDefault("DB_USER", "hr_user");
        String dbPassword = getEnvOrDefault("DB_PASSWORD", "hr_password");

        String url = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);

        log.info("Attempting connection to PostgreSQL database at: {}", url);
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            if (connection != null && !connection.isClosed()) {
                log.info("Success! PostgreSQL driver is active and database connection established.");
            }
        } catch (Exception e) {
            log.error("Failed to connect to the database. Verify Docker container status or env variables.");
            log.error("Reason: {}", e.getMessage());
        }

        log.info("=== Verification finished ===");
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    @Data
    static class TestUser {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
    }
}