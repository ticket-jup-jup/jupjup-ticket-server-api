package org.example.jupjupticketserverapi.reservation.service;

import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateResponse;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.reservation.exception.ReservationAlreadyExistsException;
import org.example.jupjupticketserverapi.reservation.repository.ReservationRepository;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.ticket.exception.TicketNotFoundException;
import org.example.jupjupticketserverapi.ticket.repository.TicketRepository;
import org.example.jupjupticketserverapi.user.entity.User;
import org.example.jupjupticketserverapi.user.exception.UserNotFoundException;
import org.example.jupjupticketserverapi.user.respository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private User user;

    @Mock
    private Ticket ticket;

    @Mock
    private Reservation savedReservation;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository,
                userRepository,
                ticketRepository
        );
    }

    @Test
    void 예약_생성_성공() {
        // given
        ReservationCreateRequest request = mock(ReservationCreateRequest.class);

        Long userId = 1L;
        Long ticketId = 10L;

        when(request.getUserId()).thenReturn(userId);
        when(request.getTicketId()).thenReturn(ticketId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(reservationRepository
                .existsByTicketIdAndStatusAndExpiresAtAfter(
                        eq(ticketId),
                        eq(ReservationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(false);

        when(reservationRepository
                .existsByTicketIdAndStatus(
                        ticketId,
                        ReservationStatus.CONFIRMED
                ))
                .thenReturn(false);

        when(savedReservation.getId()).thenReturn(100L);
        when(savedReservation.getUser()).thenReturn(user);
        when(savedReservation.getTicket()).thenReturn(ticket);
        when(savedReservation.getStatus()).thenReturn(ReservationStatus.PENDING);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        when(savedReservation.getExpiresAt()).thenReturn(expiresAt);
        when(savedReservation.getCreatedAt()).thenReturn(createdAt);
        when(savedReservation.getUpdatedAt()).thenReturn(updatedAt);

        when(user.getId()).thenReturn(userId);
        when(ticket.getId()).thenReturn(ticketId);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // when
        List<ReservationCreateResponse> result = reservationService.create(request);

        // then
        verify(userRepository).findById(userId);
        verify(ticketRepository).findById(ticketId);

        verify(reservationRepository)
                .existsByTicketIdAndStatusAndExpiresAtAfter(
                        eq(ticketId),
                        eq(ReservationStatus.PENDING),
                        any(LocalDateTime.class)
                );

        verify(reservationRepository)
                .existsByTicketIdAndStatus(
                        ticketId,
                        ReservationStatus.CONFIRMED
                );

        verify(reservationRepository).save(any(Reservation.class));

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isNotNull();
    }

    @Test
    void 존재하지_않는_유저_예외() {
        // given
        ReservationCreateRequest request = mock(ReservationCreateRequest.class);

        Long userId = 1L;
        Long ticketId = 10L;

        when(request.getUserId()).thenReturn(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 유저입니다.");

        verify(userRepository).findById(userId);
        verifyNoInteractions(ticketRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void 존재하지_않는_티켓_예외() {
        // given
        ReservationCreateRequest request = mock(ReservationCreateRequest.class);

        Long userId = 1L;
        Long ticketId = 10L;

        when(request.getUserId()).thenReturn(userId);
        when(request.getTicketId()).thenReturn(ticketId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                reservationService.create(request)
        )
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessage("존재하지 않는 티켓입니다.");

        verify(userRepository).findById(userId);
        verify(ticketRepository).findById(ticketId);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void PENDING_예약이_이미_있으면_예외() {
        // given
        ReservationCreateRequest request = mock(ReservationCreateRequest.class);

        Long userId = 1L;
        Long ticketId = 10L;

        when(request.getUserId()).thenReturn(userId);
        when(request.getTicketId()).thenReturn(ticketId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        when(reservationRepository
                .existsByTicketIdAndStatusAndExpiresAtAfter(
                        eq(ticketId),
                        eq(ReservationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(ReservationAlreadyExistsException.class)
                .hasMessage("이미 예약된 티켓입니다.");

        verify(reservationRepository)
                .existsByTicketIdAndStatusAndExpiresAtAfter(
                        eq(ticketId),
                        eq(ReservationStatus.PENDING),
                        any(LocalDateTime.class)
                );

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void CONFIRMED_예약이_이미_있으면_예외() {
        // given
        ReservationCreateRequest request = mock(ReservationCreateRequest.class);

        Long userId = 1L;
        Long ticketId = 10L;

        when(request.getUserId()).thenReturn(userId);
        when(request.getTicketId()).thenReturn(ticketId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        when(reservationRepository
                .existsByTicketIdAndStatusAndExpiresAtAfter(
                        eq(ticketId),
                        eq(ReservationStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(false);

        when(reservationRepository
                .existsByTicketIdAndStatus(
                        ticketId,
                        ReservationStatus.CONFIRMED
                ))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(ReservationAlreadyExistsException.class)
                .hasMessage("이미 예약된 티켓입니다.");

        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}