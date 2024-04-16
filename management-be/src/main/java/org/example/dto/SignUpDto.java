package org.example.dto;

import org.example.enums.UserRole;
import org.example.entities.Department;

public record SignUpDto(
        String login,
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        Department department
) {}
