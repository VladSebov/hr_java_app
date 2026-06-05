package ru.ystu.repository;

import ru.ystu.dto.Employee;
import java.util.List;

public interface EmployeeRepository {
    List<Employee> findAll();
    void save(Employee employee);
}