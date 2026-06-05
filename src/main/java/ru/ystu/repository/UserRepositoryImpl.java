package ru.ystu.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ystu.dto.User;
import ru.ystu.util.UserRowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LogManager.getLogger(UserRepositoryImpl.class);

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding user: {}", username, e);
            throw new RuntimeException("Database error during user search", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding user by ID: {}", id, e);
            throw new RuntimeException("Database error", e);
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

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setCreatedAt(rs.getString("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            log.error("Error saving user: {}", user.getUsername(), e);
            throw new RuntimeException("Database error during user creation", e);
        }
        throw new RuntimeException("Failed to save user, no data returned");
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Error fetching all users", e);
            throw new RuntimeException("Database error during fetching users", e);
        }
        return users;
    }

    @Override
    public void update(Long id, User user) {
        boolean hasPassword = user.getPasswordHash() != null && !user.getPasswordHash().isBlank();
        String sql = hasPassword
                ? "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?"
                : "UPDATE users SET username = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            if (hasPassword) {
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole());
                stmt.setLong(4, id);
            } else {
                stmt.setString(2, user.getRole());
                stmt.setLong(3, id);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating user ID: {}", id, e);
            throw new RuntimeException("Database error during user update", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error deleting user ID: {}", id, e);
            throw new RuntimeException("Database error during user deletion", e);
        }
    }
}