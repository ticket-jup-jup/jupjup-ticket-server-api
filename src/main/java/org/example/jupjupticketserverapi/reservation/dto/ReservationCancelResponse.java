package org.example.jupjupticketserverapi.reservation.dto;

import org.example.jupjupticketserverapi.reservation.entity.Reservation;

public record ReservationCancelResponse(
        Long reservationId,
        String status,
        Long ticketId
) {
    public static ReservationCancelResponse from(Reservation reservation){
        return new ReservationCancelResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getTicket().getId()
        );
    }
}
