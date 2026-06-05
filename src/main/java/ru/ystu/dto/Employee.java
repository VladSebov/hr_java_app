package ru.ystu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Employee {
    private Long id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String position;
    private String department;
    private String hireDate;
}