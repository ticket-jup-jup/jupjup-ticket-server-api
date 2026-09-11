package org.example.jupjupticketserverapi.program.repository;

import org.example.jupjupticketserverapi.program.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findAllByDeletedAtIsNull();
}
