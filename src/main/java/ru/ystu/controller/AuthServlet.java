package ru.ystu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import ru.ystu.dto.LoginRequest;
import ru.ystu.dto.TokenResponse;
import ru.ystu.dto.User;
import ru.ystu.repository.UserRepository;
import ru.ystu.util.JwtUtil;

import java.io.IOException;
import java.util.Optional;

public class AuthServlet extends HttpServlet {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthServlet(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        LoginRequest loginReq = objectMapper.readValue(req.getInputStream(), LoginRequest.class);

        Optional<User> userOpt = userRepository.findByUsername(loginReq.getUsername());

        if (userOpt.isEmpty() || !BCrypt.checkpw(loginReq.getPassword(), userOpt.get().getPasswordHash())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Invalid login or Password\"}");
            return;
        }

        User user = userOpt.get();

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole());

        TokenResponse tokenResponse = new TokenResponse(token, user.getRole());
        objectMapper.writeValue(resp.getWriter(), tokenResponse);
    }
}