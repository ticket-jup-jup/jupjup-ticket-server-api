package org.example.jupjupticketserverapi.webhook.event;



public record TicketAvailableEvent(
        Long externalTicketId,
        Long performanceId
) {
}