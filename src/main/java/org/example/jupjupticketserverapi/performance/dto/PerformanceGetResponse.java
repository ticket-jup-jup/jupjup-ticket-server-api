package org.example.jupjupticketserverapi.performance.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.performance.entity.PerformanceStatus;

import java.time.LocalDateTime;

@Getter
public class PerformanceGetResponse {

    private final Long id;
    private final Long programId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String venue;
    private final PerformanceStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PerformanceGetResponse(Long id, Long programId, LocalDateTime startAt, LocalDateTime endAt, String venue, PerformanceStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.programId = programId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.venue = venue;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
