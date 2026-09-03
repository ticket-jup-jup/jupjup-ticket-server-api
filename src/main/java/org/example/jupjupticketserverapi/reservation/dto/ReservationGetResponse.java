package org.example.jupjupticketserverapi.reservation.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

@Getter
public class ReservationGetResponse {

    private final Long id;
    private final Long userId;
    private final Long ticketId;
    private final ReservationStatus status;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReservationGetResponse(Long id, Long userId, Long ticketId, ReservationStatus status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.ticketId = ticketId;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
