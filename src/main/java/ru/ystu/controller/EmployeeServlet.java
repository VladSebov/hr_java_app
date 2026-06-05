package ru.ystu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ystu.dto.Employee;
import ru.ystu.repository.EmployeeRepository;

import java.io.IOException;
import java.util.List;

public class EmployeeServlet extends HttpServlet {

    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmployeeServlet(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // GET /api/employees/list
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        List<Employee> employees = employeeRepository.findAll();
        objectMapper.writeValue(resp.getWriter(), employees);
    }

    // POST /api/employees/create
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Employee employee = objectMapper.readValue(req.getInputStream(), Employee.class);
        employeeRepository.save(employee);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"message\": \"Employee successfully created\"}");
    }
}