package org.example.jupjupticketserverapi.ticket.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.ticket.entity.TicketStatus;

import java.math.BigDecimal;

@Getter
public class TicketGetResponse {

    private final Long id;
    private final Long programId;
    private final Long performanceId;
    private final String section;
    private final String seat;
    private final BigDecimal price;
    private final TicketStatus status;

    public TicketGetResponse(Long id, Long programId, Long performanceId, String section, String seat, BigDecimal price, TicketStatus status) {
        this.id = id;
        this.programId = programId;
        this.performanceId = performanceId;
        this.section = section;
        this.seat = seat;
        this.price = price;
        this.status = status;
    }
}