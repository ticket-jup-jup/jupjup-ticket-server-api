package org.example.jupjupticketserverapi.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.ticket.dto.TicketGetResponse;
import org.example.jupjupticketserverapi.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<TicketGetResponse> getTickets(
            Long programId,
            Long performanceId
    ) {
        return ticketRepository.findTicketList(programId, performanceId)
                .stream()
                .map(row -> {

                    ReservationStatus reservationStatus =
                            (ReservationStatus) row[6];

                    String status;

                    if (reservationStatus == null) {
                        status = "AVAILABLE";
                    } else if (reservationStatus == ReservationStatus.PENDING) {
                        status = "RESERVED";
                    } else if (reservationStatus == ReservationStatus.CONFIRMED) {
                        status = "SOLD";
                    } else {
                        status = "AVAILABLE";
                    }

                    return new TicketGetResponse(
                            (Long) row[0],
                            (Long) row[1],
                            (Long) row[2],
                            (String) row[3],
                            (String) row[4],
                            (BigDecimal) row[5],
                            status
                    );
                })
                .toList();
    }
}
