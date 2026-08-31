package org.example.jupjupticketserverapi.program.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "programs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Program extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProgramType programType;

    @Column(nullable = false, length = 255)
    private String description;

    private LocalDateTime deletedAt;

    public Program(String name, ProgramType programType, String description) {
        this.name = name;
        this.programType = programType;
        this.description = description;
    }
}
