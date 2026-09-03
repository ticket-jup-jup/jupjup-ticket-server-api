package org.example.jupjupticketserverapi.reservation.service;

import org.example.jupjupticketserverapi.payment.entity.Payment;
import org.example.jupjupticketserverapi.payment.entity.PaymentMethod;
import org.example.jupjupticketserverapi.payment.entity.PaymentStatus;
import org.example.jupjupticketserverapi.payment.repository.PaymentRepository;
import org.example.jupjupticketserverapi.reservation.dto.*;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.reservation.exception.ReservationAlreadyExistsException;
import org.example.jupjupticketserverapi.reservation.exception.ReservationNotCancellableException;
import org.example.jupjupticketserverapi.reservation.exception.ReservationNotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
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
    private PaymentRepository paymentRepository;

    @Mock
    private User user;

    @Mock
    private Ticket ticket;

    @Mock
    private Reservation reservation;

    @Mock
    private Reservation savedReservation;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository,
                userRepository,
                ticketRepository,
                paymentRepository
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

    @Test
    void 예약_확정_및_결제_생성() {

        // given
        Long reservationId = 1L;
        Long ticketId = 10L;

        Reservation reservation = mock(Reservation.class);
        Ticket ticket = mock(Ticket.class);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(reservation.getTicket()).thenReturn(ticket);
        when(ticket.getId()).thenReturn(ticketId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticket.getPrice()).thenReturn(BigDecimal.valueOf(10000));

        ReservationConfirmRequest request = mock(ReservationConfirmRequest.class);

        when(request.getPaymentMethod()).thenReturn(PaymentMethod.CARD);
        when(reservation.getId()).thenReturn(reservationId);
        when(reservation.getUser()).thenReturn(user);
        when(reservation.getStatus()).thenReturn(ReservationStatus.CONFIRMED);
        when(reservation.getExpiresAt()).thenReturn(LocalDateTime.now().plusMinutes(10));
        when(reservation.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(reservation.getUpdatedAt()).thenReturn(LocalDateTime.now());

        when(user.getId()).thenReturn(1L);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<ReservationConfirmResponse> result = reservationService.confirm(reservationId, request);

        // then
        verify(reservation).confirm();
        verify(paymentRepository).save(any(Payment.class));

        assertThat(result).hasSize(1);

        ReservationConfirmResponse response = result.get(0);
        assertThat(response.getReservation()).isNotNull();
        assertThat(response.getPayment()).isNotNull();
        assertThat(response.getPayment().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.getPayment().getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(response.getPayment().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getPayment().getPaidAt()).isNotNull();
        assertThat(response.getPayment().getCreatedAt()).isNotNull();
    }

    @Test
    void 존재하지_않는_예약_예외() {

        // given
        Long reservationId = 999L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        ReservationConfirmRequest request = mock(ReservationConfirmRequest.class);

        // when & then
        verify(ticketRepository, never()).findById(any());
        verify(paymentRepository, never()).save(any());

        assertThatThrownBy(() ->
                reservationService.confirm(
                        reservationId,
                        request
                )
        ).isInstanceOf(ReservationNotFoundException.class);
    }


    // ── 예약 취소 (이슈 #32) ──────────────────────────────────

    @Test
    void 예약_취소_성공_PENDING() {
        // given
        Reservation reservation = 취소가능한_예약(ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(ticket.getId()).thenReturn(10L);

        // when
        ReservationCancelResponse response = reservationService.cancel(1L);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REFUNDED);
        assertThat(response.reservationId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo("REFUNDED");
        assertThat(response.ticketId()).isEqualTo(10L);
    }

    @Test
    void 예약_취소_성공_CONFIRMED() {
        // given
        Reservation reservation = 취소가능한_예약(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // when
        reservationService.cancel(1L);

        // then: 확정 예약이 환불되는 순간 이 티켓은 다시 예약 가능 = 취소표
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REFUNDED);
    }

    @Test
    void 이미_환불된_예약_취소_예외() {
        // given
        Reservation reservation = 취소가능한_예약(ReservationStatus.REFUNDED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationService.cancel(1L))
                .isInstanceOf(ReservationNotCancellableException.class);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.REFUNDED);
    }

    @Test
    void 만료된_예약_취소_예외() {
        // given
        Reservation reservation = 취소가능한_예약(ReservationStatus.EXPIRED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() -> reservationService.cancel(1L))
                .isInstanceOf(ReservationNotCancellableException.class);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void 존재하지_않는_예약_취소_예외() {
        // given
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.cancel(99L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    /**
     * 원하는 상태의 실제 Reservation 을 만든다.
     * savedReservation mock 을 안 쓰는 이유: 취소는 상태 "전이" 가 핵심이라
     * 실제 객체로 refund() 가 정말 상태를 바꾸는지 검증해야 한다.
     * 생성자는 항상 PENDING 으로 시작하므로 엔티티 메서드로 목표 상태까지 전이시킨다.
     */
    private Reservation 취소가능한_예약(ReservationStatus target) {
        Reservation reservation = new Reservation(user, ticket, LocalDateTime.now().plusMinutes(10));
        switch (target) {
            case CONFIRMED -> reservation.confirm();
            case REFUNDED -> reservation.refund();
            case EXPIRED -> reservation.expire();
            case PENDING -> {
            }
        }
        ReflectionTestUtils.setField(reservation, "id", 1L);
        return reservation;
    }

    @Test
    void 전체_예약_조회() {
        // given
        User user = mock(User.class);
        Ticket ticket = mock(Ticket.class);

        when(user.getId()).thenReturn(1L);
        when(ticket.getId()).thenReturn(10L);

        Reservation reservation1 = new Reservation(
                user,
                ticket,
                LocalDateTime.now().plusMinutes(10)
        );

        Reservation reservation2 = new Reservation(
                user,
                ticket,
                LocalDateTime.now().plusMinutes(20)
        );

        when(reservationRepository.findAll()).thenReturn(List.of(reservation1, reservation2));

        // when
        List<ReservationGetResponse> result = reservationService.getAll();

        // then
        verify(reservationRepository).findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(0).getTicketId()).isEqualTo(10L);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(result.get(1).getUserId()).isEqualTo(1L);
        assertThat(result.get(1).getTicketId()).isEqualTo(10L);
        assertThat(result.get(1).getStatus()).isEqualTo(ReservationStatus.PENDING);
    }
}