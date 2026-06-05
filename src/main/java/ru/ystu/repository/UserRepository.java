package ru.ystu.repository;

import ru.ystu.dto.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}