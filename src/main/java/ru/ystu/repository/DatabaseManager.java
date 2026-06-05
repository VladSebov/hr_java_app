package ru.ystu.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger log = LogManager.getLogger(DatabaseManager.class);

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "5432");
        String name = System.getenv().getOrDefault("DB_NAME", "hr_database");

        URL = String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
        USER = System.getenv().getOrDefault("DB_USER", "hr_user");
        PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "hr_password");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL Driver not found in classpath!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}