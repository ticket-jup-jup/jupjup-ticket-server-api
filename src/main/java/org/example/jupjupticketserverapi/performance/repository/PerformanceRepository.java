package org.example.jupjupticketserverapi.performance.repository;

import org.example.jupjupticketserverapi.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("""
                SELECT p
                FROM Performance p
                WHERE (:programId IS NULL OR p.program.id = :programId)
                ORDER BY p.id ASC
            """)
    List<Performance> findPerformanceList(Long programId);
}
