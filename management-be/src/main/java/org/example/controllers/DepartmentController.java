package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ErrorResponseDto;
import org.example.entities.Department;
import org.example.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Endpoints for managing departments")
@Validated
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<?> createDepartment(@Valid @RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update an existing department")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") Long id, @Valid @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, department);
            return ResponseEntity.ok(updatedDepartment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getMessage()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/dirty-read/{id}")
    @Operation(summary = "Get department by ID with potential dirty read")
    public ResponseEntity<?> getDepartmentByIdDirtyRead(@PathVariable("id") Long id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }



//    @GetMapping
//    @Operation(summary = "Get all departments")
//    public ResponseEntity<List<Department>> getAllDepartments() {
//        List<Department> departments = departmentService.getAllDepartments();
//        return ResponseEntity.ok(departments);
//    }

    @GetMapping("/all")
    @Operation(summary = "Get all departments")
    public ResponseEntity<?> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/phantom-read")
    @Operation(summary = "Get all departments twice with delay to illustrate phantom read")
    public ResponseEntity<?> getPhantomRead() {
        try {
            List<Department> initialDepartments = departmentService.getAllDepartments();
            Thread.sleep(5000); // 5 seconds delay to simulate a concurrent insert
            List<Department> updatedDepartments = departmentService.getAllDepartments();

            Map<String, List<Department>> result = new HashMap<>();
            result.put("initial", initialDepartments);
            result.put("updated", updatedDepartments);

            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto("Interrupted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department by ID")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }


    // Add a new endpoint to simulate lost update scenario
    @GetMapping("/lost-update/{id}")
    @Operation(summary = "Simulate lost update scenario")
    public ResponseEntity<?> simulateLostUpdate(@PathVariable("id") Long id) {
        try {
            // Simulate initial update
            Department initialUpdate = departmentService.updateDepartmentLostUpdate(id, "Initial Update");
            Thread.sleep(5000); // 5 seconds delay to simulate concurrent update

            // Simulate concurrent update
            Department concurrentUpdate = departmentService.updateDepartmentLostUpdate(id, "Concurrent Update");

            Map<String, Department> result = new HashMap<>();
            result.put("initialUpdate", initialUpdate);
            result.put("concurrentUpdate", concurrentUpdate);

            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto("Interrupted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
        }
    }

//    @PutMapping("/concurrent/{id}")
//    public ResponseEntity<Department> updateDepartmentConcurrently(@PathVariable Long id, @RequestBody Department department) throws InterruptedException {
//        Department existing = departmentService.getDepartmentById(id);
//        existing.setDepartmentName("Java New Name 1");
//        departmentService.updateDepartment(id, existing);
//
//        // Simulate delay to create a concurrency scenario
//        Thread.sleep(5000);
//
//        existing.setDepartmentName("Java New Name 2");
//        Department updatedDepartment = departmentService.updateDepartment(id, existing);
//        return ResponseEntity.ok(updatedDepartment);
//    }


}
