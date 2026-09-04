package org.example.jupjupticketserverapi.ticket.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void 전체_티켓_조회() {
        // given
        Reservation confirmedReservation = mock(Reservation.class);
        when(confirmedReservation.getStatus()).thenReturn(ReservationStatus.CONFIRMED);

        Object[] availableRow = {
                101L,
                1L,
                10L,
                "A",
                "A-12",
                new BigDecimal("100000.00"),
                null
        };

        Object[] soldRow = {
                102L,
                1L,
                10L,
                "B",
                "G-10",
                new BigDecimal("70000.00"),
                confirmedReservation
        };

        when(ticketRepository.findTicketList(null, null)).thenReturn(List.of(availableRow, soldRow));

        // when
        List<TicketGetResponse> result = ticketService.getTickets(null, null);

        // then
        verify(ticketRepository).findTicketList(null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(101L);
        assertThat(result.get(0).getProgramId()).isEqualTo(1L);
        assertThat(result.get(0).getPerformanceId()).isEqualTo(10L);
        assertThat(result.get(0).getSection()).isEqualTo("A");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("100000.00");
        assertThat(result.get(0).getStatus()).isEqualTo(TicketStatus.AVAILABLE);

        assertThat(result.get(1).getId()).isEqualTo(102L);
        assertThat(result.get(1).getProgramId()).isEqualTo(1L);
        assertThat(result.get(1).getPerformanceId()).isEqualTo(10L);
        assertThat(result.get(1).getSection()).isEqualTo("B");
        assertThat(result.get(1).getPrice()).isEqualByComparingTo("70000.00");
        assertThat(result.get(1).getStatus()).isEqualTo(TicketStatus.SOLD);
    }

    @Test
    void 존재하지_않는_프로그램_ID로_티켓_조회_예외() {
        // given
        Long programId = 999L;

        when(programRepository.existsById(programId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> ticketService.getTickets(programId, null))
                .isInstanceOf(ProgramNotFoundException.class)
                .hasMessage("존재하지 않는 프로그램입니다.");

        verify(programRepository).existsById(programId);
        verify(ticketRepository, never()).findTicketList(any(), any());
    }

    @Test
    void 존재하지_않는_회차_ID로_티켓_조회_예외() {
        // given
        Long performanceId = 999L;

        when(performanceRepository.existsById(performanceId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> ticketService.getTickets(null, performanceId))
                .isInstanceOf(PerformanceNotFoundException.class)
                .hasMessage("존재하지 않는 회차입니다.");

        verify(performanceRepository).existsById(performanceId);
        verify(ticketRepository, never()).findTicketList(any(), any());
    }

    @Test
    void 줍줍용_티켓_조회() {
        // given
        Performance performance = mock(Performance.class);
        Seat seat = mock(Seat.class);
        Ticket ticket = mock(Ticket.class);
        Program program = mock(Program.class);

        when(performance.getId()).thenReturn(10L);
        when(performance.getStartAt()).thenReturn(LocalDateTime.of(2026, 9, 10, 19, 30));
        when(performance.getEndAt()).thenReturn(LocalDateTime.of(2026, 9, 10, 22, 0));
        when(performance.getVenue()).thenReturn("잠실실내체육관");

        when(program.getName()).thenReturn("테스트 공연");

        when(seat.getSection()).thenReturn("A");
        when(seat.getSeatRow()).thenReturn("A");
        when(seat.getSeatNumber()).thenReturn(12);

        when(ticket.getId()).thenReturn(101L);
        when(ticket.getPrice()).thenReturn(new BigDecimal("100000.00"));

        Object[] row = {
                ticket,
                performance,
                program,
                seat,
                null
        };

        when(ticketRepository.findInternalTicketList(null)).thenReturn(List.<Object[]>of(row));

        // when
        List<TicketInternalGetResponse> result = ticketService.getInternalTickets(null, null);

        // then
        verify(ticketRepository).findInternalTicketList(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(101L);
        assertThat(result.get(0).getPerformanceId()).isEqualTo(10L);
        assertThat(result.get(0).getProgramName()).isEqualTo("테스트 공연");
        assertThat(result.get(0).getVenue()).isEqualTo("잠실실내체육관");
        assertThat(result.get(0).getSection()).isEqualTo("A");
        assertThat(result.get(0).getRowNumber()).isEqualTo("A");
        assertThat(result.get(0).getSeatNumber()).isEqualTo(12);
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("100000.00");
        assertThat(result.get(0).getStatus()).isEqualTo(TicketStatus.AVAILABLE);
    }

    @Test
    void 회차_ID로_줍줍용_티켓_조회() {
        // given
        Long performanceId = 10L;

        when(performanceRepository.existsById(performanceId)).thenReturn(true);

        when(ticketRepository.findInternalTicketList(performanceId)).thenReturn(List.of());

        // when
        List<TicketInternalGetResponse> result = ticketService.getInternalTickets(performanceId, null);

        // then
        verify(performanceRepository).existsById(performanceId);
        verify(ticketRepository).findInternalTicketList(performanceId);

        assertThat(result).isEmpty();
    }

    @Test
    void 존재하지_않는_회차_ID로_줍줍용_티켓_조회_예외() {
        // given
        Long performanceId = 999L;

        when(performanceRepository.existsById(performanceId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> ticketService.getInternalTickets(performanceId, null))
                .isInstanceOf(PerformanceNotFoundException.class)
                .hasMessage("존재하지 않는 회차입니다.");

        verify(performanceRepository).existsById(performanceId);
        verify(ticketRepository, never()).findInternalTicketList(any());
    }

    @Test
    void 티켓_상태로_줍줍용_티켓_조회() {
        // given
        Ticket availableTicket = mock(Ticket.class);
        Ticket soldTicket = mock(Ticket.class);

        Performance performance = mock(Performance.class);
        Program program = mock(Program.class);
        Seat seat = mock(Seat.class);

        when(performance.getId()).thenReturn(10L);

        when(program.getName()).thenReturn("테스트 공연");

        Object[] availableRow = {
                availableTicket,
                performance,
                program,
                seat,
                null
        };

        Reservation confirmedReservation = mock(Reservation.class);
        when(confirmedReservation.getStatus()).thenReturn(ReservationStatus.CONFIRMED);

        Object[] soldRow = {
                soldTicket,
                performance,
                program,
                seat,
                confirmedReservation
        };

        when(ticketRepository.findInternalTicketList(null)).thenReturn(List.of(availableRow, soldRow));

        // when
        List<TicketInternalGetResponse> result = ticketService.getInternalTickets(null, "AVAILABLE");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(TicketStatus.AVAILABLE);
    }

    @Test
    void 잘못된_티켓_상태_입력_예외() {
        // when & then
        assertThatThrownBy(() -> ticketService.getInternalTickets(null, "INVALID"))
                .isInstanceOf(InvalidTicketStatusException.class)
                .hasMessage("status는 AVAILABLE, RESERVED, SOLD만 조회 가능합니다.");

        verify(ticketRepository, never()).findInternalTicketList(any());
    }
}