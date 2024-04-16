package org.example.services;

import org.example.entities.Department;
import org.example.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department createDepartment(Department department) {
        // Validate if department name is not null or empty
        if (department.getDepartmentName() == null || department.getDepartmentName().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        // Check if department with the same name already exists
        if (departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new IllegalArgumentException("Department with the same name already exists");
        }
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, Department department) {
        // Validate if department name is not null or empty
        if (department.getDepartmentName() == null || department.getDepartmentName().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        // Check if department with the same name already exists excluding the current department being updated
        Optional<Department> existingDepartment = departmentRepository.findById(id);
        if (existingDepartment.isEmpty()) {
            throw new IllegalArgumentException("Department not found");
        }
        Department existing = existingDepartment.get();
        if (!existing.getDepartmentName().equals(department.getDepartmentName())
                && departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new IllegalArgumentException("Department with the same name already exists");
        }
        department.setId(id);
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Department not found");
        }
        departmentRepository.deleteById(id);
    }
}
