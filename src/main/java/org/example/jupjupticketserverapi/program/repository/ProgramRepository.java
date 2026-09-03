package org.example.jupjupticketserverapi.program.repository;

import org.example.jupjupticketserverapi.program.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
