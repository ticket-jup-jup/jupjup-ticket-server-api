package org.example.jupjupticketserverapi.performance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.program.entity.Program;

import java.time.LocalDateTime;

@Entity
@Table(name = "performances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false, length = 255)
    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerformanceStatus performanceStatus;

    public Performance(Program program, LocalDateTime startAt, LocalDateTime endAt, String venue, PerformanceStatus performanceStatus) {
        this.program = program;
        this.startAt = startAt;
        this.endAt = endAt;
        this.venue = venue;
        this.performanceStatus = performanceStatus;
    }
}
