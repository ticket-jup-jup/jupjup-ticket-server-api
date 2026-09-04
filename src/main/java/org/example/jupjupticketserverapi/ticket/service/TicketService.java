package org.example.jupjupticketserverapi.ticket.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.performance.entity.Performance;
import org.example.jupjupticketserverapi.performance.exception.PerformanceNotFoundException;
import org.example.jupjupticketserverapi.performance.repository.PerformanceRepository;
import org.example.jupjupticketserverapi.program.entity.Program;
import org.example.jupjupticketserverapi.program.exception.ProgramNotFoundException;
import org.example.jupjupticketserverapi.program.repository.ProgramRepository;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.seat.entity.Seat;
import org.example.jupjupticketserverapi.ticket.dto.TicketGetResponse;
import org.example.jupjupticketserverapi.ticket.dto.TicketInternalGetResponse;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.ticket.entity.TicketStatus;
import org.example.jupjupticketserverapi.ticket.exception.InvalidTicketStatusException;
import org.example.jupjupticketserverapi.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PerformanceRepository performanceRepository;
    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)

    public List<TicketGetResponse> getTickets(
            Long programId,
            Long performanceId
    ) {
        // programId 유효성 검사
        if (programId != null
                && !programRepository.existsById(programId)) {
            throw new ProgramNotFoundException("존재하지 않는 프로그램입니다.");
        }

        // performanceId 유효성 검사
        if (performanceId != null
                && !performanceRepository.existsById(performanceId)) {
            throw new PerformanceNotFoundException("존재하지 않는 회차입니다.");
        }

        return ticketRepository.findTicketList(programId, performanceId)
                .stream()
                .map(row -> {
                    Reservation reservation = (Reservation) row[6];

                    return new TicketGetResponse(
                            (Long) row[0],
                            (Long) row[1],
                            (Long) row[2],
                            (String) row[3],
                            (String) row[4],
                            (BigDecimal) row[5],
                            calculateTicketStatus(reservation)
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketInternalGetResponse> getInternalTickets(
            Long performanceId,
            String status
    ) {

        // performanceId 유효성 검사
        if (performanceId != null
                && !performanceRepository.existsById(performanceId)) {
            throw new PerformanceNotFoundException("존재하지 않는 회차입니다.");
        }

        // status 유효성 검사
        TicketStatus ticketStatus = parseTicketStatus(status);

        return ticketRepository.findInternalTicketList(performanceId)
                .stream()
                .map(row -> {

                    Ticket ticket = (Ticket) row[0];
                    Performance performance = (Performance) row[1];
                    Program program = (Program) row[2];
                    Seat seat = (Seat) row[3];
                    Reservation reservation = (Reservation) row[4];

                    TicketStatus calculatedStatus = calculateTicketStatus(reservation);

                    if (ticketStatus != null
                            && calculatedStatus != ticketStatus) {
                        return null;
                    }

                    return new TicketInternalGetResponse(
                            ticket.getId(),
                            performance.getId(),
                            program.getName(),
                            performance.getStartAt(),
                            performance.getEndAt(),
                            performance.getVenue(),
                            seat.getSection(),
                            seat.getSeatRow(),
                            seat.getSeatNumber(),
                            ticket.getPrice(),
                            calculatedStatus,
                            ticket.getCreatedAt(),
                            ticket.getUpdatedAt()
                    );
                })
                .filter(response -> response != null)
                .toList();
    }

    private TicketStatus calculateTicketStatus(
            Reservation reservation
    ) {

        if (reservation == null) {
            return TicketStatus.AVAILABLE;
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return TicketStatus.SOLD;
        }

        if (reservation.getStatus() == ReservationStatus.PENDING
                && reservation.getExpiresAt().isAfter(LocalDateTime.now())) {
            return TicketStatus.RESERVED;
        }

        return TicketStatus.AVAILABLE;
    }

    private TicketStatus parseTicketStatus(String status) {
        if (status == null) {
            return null;
        }

        try {
            return TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTicketStatusException("status는 AVAILABLE, RESERVED, SOLD만 조회 가능합니다.");
        }
    }
}
