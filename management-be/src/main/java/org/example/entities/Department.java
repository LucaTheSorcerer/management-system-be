package org.example.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_name")
    private String departmentName;

    @Version
    private int version;
}
