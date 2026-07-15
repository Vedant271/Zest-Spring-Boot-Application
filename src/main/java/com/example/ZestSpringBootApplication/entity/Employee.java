package com.example.ZestSpringBootApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String department;
    @Column(nullable = false)
    private String position;
    @Column(nullable = false)
    @Builder.Default
    private Double salary = 0.0;
    @Column(nullable = false)
    private LocalDate dateOfJoining;
}
