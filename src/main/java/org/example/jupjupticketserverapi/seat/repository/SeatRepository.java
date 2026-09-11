package org.example.jupjupticketserverapi.seat.repository;

import org.example.jupjupticketserverapi.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("""
            SELECT 
                s, 
                t.performance.id
            FROM 
                Seat s
            JOIN 
                Ticket t 
            ON 
                t.seat.id = s.id
            WHERE 
                (:performanceId IS NULL OR t.performance.id = :performanceId)
            ORDER BY 
                t.performance.id,
                s.section,
                s.seatRow,
                s.seatNumber
            """)
    List<Object[]> findAllByPerformanceId(Long performanceId);

}
