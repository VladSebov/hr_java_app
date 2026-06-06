package ru.ystu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import ru.ystu.dto.User;
import ru.ystu.repository.UserRepository;
import ru.ystu.util.PathParser;

import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserServlet(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // GET /api/users
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPasswordHash(null));

        objectMapper.writeValue(resp.getWriter(), users);
    }

    // POST /api/users
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        User user = objectMapper.readValue(req.getInputStream(), User.class);

        if (user.getUsername() == null || user.getPasswordHash() == null || user.getRole() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Not all required fields are filled\"}");
            return;
        }

        String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
        user.setPasswordHash(hashedPassword);
        User savedUser = userRepository.save(user);
        savedUser.setPasswordHash(null);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), savedUser);
    }

    // PUT /api/users/{id}
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Long id = PathParser.extractId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"User ID invalid\"}");
            return;
        }

        User existingUser = userRepository.findById(id).orElse(null); // Вам нужно добавить метод findById в интерфейс
        if (existingUser == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"User not found\"}");
            return;
        }

        User updatedData = objectMapper.readValue(req.getInputStream(), User.class);

        if (updatedData.getPasswordHash() != null && !updatedData.getPasswordHash().isBlank()) {
            String hashedPassword = BCrypt.hashpw(updatedData.getPasswordHash(), BCrypt.gensalt());
            updatedData.setPasswordHash(hashedPassword);
        } else {
            updatedData.setPasswordHash(existingUser.getPasswordHash());
        }

        userRepository.update(id, updatedData);
        resp.getWriter().write("{\"message\": \"User successfully updated\"}");
    }

    // DELETE /api/users/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Long id = PathParser.extractId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"User ID invalid\"}");
            return;
        }
        userRepository.delete(id);
        resp.getWriter().write("{\"message\": \"User successfully deleted\"}");
    }
}