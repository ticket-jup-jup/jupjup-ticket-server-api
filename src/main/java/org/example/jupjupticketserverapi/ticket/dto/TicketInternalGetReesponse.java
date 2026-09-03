package org.example.jupjupticketserverapi.ticket.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.ticket.entity.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TicketInternalGetReesponse {

    private final Long id;
    private final Long performanceId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final String venue;
    private final String section;
    private final String rowNumber;
    private final Integer seatNumber;
    private final BigDecimal price;
    private final TicketStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public TicketInternalGetReesponse(Long id, Long performanceId, LocalDateTime startAt, LocalDateTime endAt, String venue, String section, String rowNumber, Integer seatNumber, BigDecimal price, TicketStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.performanceId = performanceId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.venue = venue;
        this.section = section;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
