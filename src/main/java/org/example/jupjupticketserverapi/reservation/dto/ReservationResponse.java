package org.example.jupjupticketserverapi.reservation.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationResponse {

    private final Long reservationId;
    private final Long userId;
    private final Long ticketId;
    private final String status;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReservationResponse(Long reservationId, Long userId, Long ticketId, String status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.ticketId = ticketId;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
