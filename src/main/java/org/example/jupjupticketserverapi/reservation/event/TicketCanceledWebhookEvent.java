package org.example.jupjupticketserverapi.reservation.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketCanceledWebhookEvent(
        Long ticketId,
        Long performanceId,
        Long seatId,
        BigDecimal price,
        LocalDateTime canceledAt
) {
}