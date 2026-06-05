package ru.ystu.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ystu.dto.User;

import java.sql.*;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LogManager.getLogger(UserRepositoryImpl.class);

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            log.debug("Executing query: {} with parameter: {}", sql, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            log.error("Error occurred while finding user by username: {}", username, e);
            throw new RuntimeException("Database error during user search", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?) RETURNING id, created_at";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());

            log.debug("Executing insert: {}", sql);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    log.info("User successfully saved with ID: {}", user.getId());
                    return user;
                }
            }
        } catch (SQLException e) {
            log.error("Error occurred while saving user: {}", user.getUsername(), e);
            throw new RuntimeException("Database error during user creation", e);
        }
        throw new RuntimeException("Failed to save user, no ID returned");
    }
}