package org.example.services;

import jakarta.transaction.Transactional;
import org.example.entities.Department;
import org.example.repositories.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


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

    @Transactional
    @Override
    public Department updateDepartment(Long id, Department department) throws InterruptedException {
        // Validate if department name is not null or empty
        if (department.getDepartmentName() == null || department.getDepartmentName().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        // Check if department with the same name already exists excluding the current department being updated
        Optional<Department> existingDepartment = departmentRepository.findById(id);
        log.info("Received request to update department: {}", id);
        if (existingDepartment.isEmpty()) {
            throw new IllegalArgumentException("Department not found");
        }
        // THIS IS FOR SHIT AND GIGGLES
        log.info("Simulating delay...");
        Thread.sleep(5000); // 5 seconds delay

        Department existing = existingDepartment.get();
        if (!existing.getDepartmentName().equals(department.getDepartmentName())
                && departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new IllegalArgumentException("Department with the same name already exists");
        }
        department.setId(id);
        log.info("Updating department: {}", id);
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
