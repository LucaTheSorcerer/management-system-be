package org.example.services;

import org.example.entities.Skill;
import org.example.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public Skill createSkill(Skill skill) {
        // Validate if skill name is not null or empty
        if (skill.getSkillName() == null || skill.getSkillName().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be null or empty");
        }
        // Check if skill with the same name already exists
        if (skillRepository.existsBySkillName(skill.getSkillName())) {
            throw new IllegalArgumentException("Skill with the same name already exists");
        }
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Long id, Skill skill) {
        // Validate if skill name is not null or empty
        if (skill.getSkillName() == null || skill.getSkillName().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be null or empty");
        }
        // Check if skill with the same name already exists excluding the current skill being updated
        Optional<Skill> existingSkill = skillRepository.findById(id);
        if (existingSkill.isEmpty()) {
            throw new IllegalArgumentException("Skill not found");
        }
        Skill existing = existingSkill.get();
        if (!existing.getSkillName().equals(skill.getSkillName())
                && skillRepository.existsBySkillName(skill.getSkillName())) {
            throw new IllegalArgumentException("Skill with the same name already exists");
        }
        skill.setId(id);
        return skillRepository.save(skill);
    }

    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new IllegalArgumentException("Skill not found");
        }
        skillRepository.deleteById(id);
    }
}
