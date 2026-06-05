package ru.ystu.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;

    @JsonAlias("password")
    private String passwordHash;

    private String role;
    private String createdAt;
}