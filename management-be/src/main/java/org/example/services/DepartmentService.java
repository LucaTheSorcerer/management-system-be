package org.example.services;

import org.example.entities.Department;
import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    Department updateDepartment(Long id, Department department) throws InterruptedException;

    Department getDepartmentById(Long id);

    List<Department> getAllDepartments();

    void deleteDepartment(Long id);

    Department updateDepartmentLostUpdate(Long id, String newName) throws InterruptedException;
}
