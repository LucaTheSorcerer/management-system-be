package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ErrorResponseDto;
import org.example.entities.Skill;
import org.example.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Endpoints for managing skills")
@Validated
public class SkillController {

    @Autowired
    private SkillService skillService;

    @PostMapping
    @Operation(summary = "Create a new skill")
    public ResponseEntity<?> createSkill(@Valid @RequestBody Skill skill) {
        try {
            Skill createdSkill = skillService.createSkill(skill);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing skill")
    public ResponseEntity<?> updateSkill(@PathVariable Long id, @Valid @RequestBody Skill skill) {
        try {
            Skill updatedSkill = skillService.updateSkill(id, skill);
            return ResponseEntity.ok(updatedSkill);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill by ID")
    public ResponseEntity<?> getSkillById(@PathVariable Long id) {
        try {
            Skill skill = skillService.getSkillById(id);
            return ResponseEntity.ok(skill);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete skill by ID")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        try {
            skillService.deleteSkill(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }
}
