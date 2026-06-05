package ru.ystu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ystu.dto.Employee;
import ru.ystu.repository.EmployeeRepository;
import ru.ystu.util.PathParser;

import java.io.IOException;
import java.util.List;

public class EmployeeServlet extends HttpServlet {

    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmployeeServlet(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // GET /api/employees
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        List<Employee> employees = employeeRepository.findAll();
        objectMapper.writeValue(resp.getWriter(), employees);
    }

    // POST /api/employees
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Employee employee = objectMapper.readValue(req.getInputStream(), Employee.class);
        employeeRepository.save(employee);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"message\": \"Employee successfully created\"}");
    }

    // PUT /api/employees/{id}
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long id = PathParser.extractId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Employee ID not given\"}");
            return;
        }
        Employee employee = objectMapper.readValue(req.getInputStream(), Employee.class);
        employeeRepository.update(id, employee);
        resp.getWriter().write("{\"message\": \"Employee successfully updated\"}");
    }

    // DELETE /api/employees/{id}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Long id = PathParser.extractId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Employee ID not given\"}");
            return;
        }

        employeeRepository.delete(id);
        resp.getWriter().write("{\"message\": \"Employee successfully deleted\"}");
    }
}