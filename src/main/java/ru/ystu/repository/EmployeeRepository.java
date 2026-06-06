package ru.ystu.repository;

import ru.ystu.dto.Employee;
import java.util.List;

public interface EmployeeRepository {
    List<Employee> findAll();
    void save(Employee employee);
    void update(Long id, Employee employee);
    void delete(Long id);
}