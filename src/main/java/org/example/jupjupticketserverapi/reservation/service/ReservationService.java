package org.example.jupjupticketserverapi.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jupjupticketserverapi.payment.dto.PaymentResponse;
import org.example.jupjupticketserverapi.payment.entity.Payment;
import org.example.jupjupticketserverapi.payment.repository.PaymentRepository;
import org.example.jupjupticketserverapi.reservation.dto.*;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.reservation.event.TicketCanceledWebhookEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ReservationCreateResponse> create(
            ReservationCreateRequest request
    ) {

        // 유저 정보 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("존재하지 않는 유저입니다.")
                );

        // 티켓 정보 조회
        Ticket ticket = ticketRepository.findByIdForUpdate(request.getTicketId())
                .orElseThrow(() ->
                        new TicketNotFoundException("존재하지 않는 티켓입니다.")
                );

        // 이미 예약된 티켓인지 확인
        LocalDateTime now = LocalDateTime.now();

        // 임시예약(PENDING)이 아직 유효한지 확인
        boolean pendingExists =
                reservationRepository
                        .existsByTicketIdAndStatusAndExpiresAtAfter(
                                request.getTicketId(),
                                ReservationStatus.PENDING,
                                now
                        );

        // 예약확정(CONFIRMED)된 티켓인지 확인
        boolean confirmedExists =
                reservationRepository
                        .existsByTicketIdAndStatus(
                                request.getTicketId(),
                                ReservationStatus.CONFIRMED
                        );

        if (pendingExists || confirmedExists) {
            throw new ReservationAlreadyExistsException(
                    "이미 예약된 티켓입니다."
            );
        }

        // 만료시간 계산
        LocalDateTime expiresAt = now.plusMinutes(10);

        // 기존에 만료/환불 상태인 예약 데이터가 있으면 UPDATE
        Optional<Reservation> existingReservation =
                reservationRepository.findByTicketIdAndStatusIn(
                        request.getTicketId(),
                        List.of(ReservationStatus.EXPIRED, ReservationStatus.REFUNDED)
                );

        Reservation reservation;
        if (existingReservation.isPresent()) {
            reservation = existingReservation.get();
            reservation.renew(user, expiresAt);
        } else {
            // 기존 예약이 없으면 새 예약 생성
            reservation = new Reservation(
                    user,
                    ticket,
                    expiresAt
            );

            reservation = reservationRepository.save(reservation);
        }

        ReservationResponse reservationResponse =
                new ReservationResponse(
                        reservation.getId(),
                        reservation.getUser().getId(),
                        reservation.getTicket().getId(),
                        reservation.getStatus().name(),
                        reservation.getExpiresAt(),
                        reservation.getCreatedAt(),
                        reservation.getUpdatedAt()
                );

        return List.of(
                new ReservationCreateResponse(
                        reservationResponse,
                        null
                )
        );
    }

    @Transactional
    public List<ReservationConfirmResponse> confirm(Long reservationId, ReservationConfirmRequest request) {

        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationNotFoundException("존재하지 않는 예약입니다.")
                );

        // 티켓 데이터 조회
        Ticket ticket = ticketRepository.findById(reservation.getTicket().getId())
                .orElseThrow(
                        () -> new TicketNotFoundException("존재하지 않는 티켓입니다.")
                );

        // 예약 상태 변경
        reservation.confirm();

        // 결제 데이터 추가
        Payment payment = new Payment(
                reservation,
                ticket.getPrice(),
                request.getPaymentMethod()
        );

        Payment savedPayment = paymentRepository.save(payment);

        ReservationResponse reservationResponse =
                new ReservationResponse(
                        reservation.getId(),
                        reservation.getUser().getId(),
                        reservation.getTicket().getId(),
                        reservation.getStatus().name(),
                        reservation.getExpiresAt(),
                        reservation.getCreatedAt(),
                        reservation.getUpdatedAt()
                );

        PaymentResponse paymentResponse = new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getReservation().getId(),
                savedPayment.getAmount(),
                savedPayment.getPaymentMethod(),
                savedPayment.getStatus(),
                savedPayment.getPaidAt(),
                savedPayment.getCreatedAt()
        );

        return List.of(
                new ReservationConfirmResponse(
                        reservationResponse,
                        paymentResponse
                )
        );
    }

    @Transactional
    public ReservationCancelResponse cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationNotCancellableException(
                    "취소할 수 없는 예약입니다. 현재 상태: " + reservation.getStatus());
        }

        // 결제 데이터가 있으면 삭제
        paymentRepository.findByReservationId(reservationId)
                .ifPresent(paymentRepository::delete);

        reservation.refund();

        // 줍줍서버로 취소표 알림 Webhook 전송
        publishTicketCanceledWebhookEvent(reservation);

        return ReservationCancelResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationGetResponse> getAll() {

        return reservationRepository.findAll()
                .stream().map(reservation -> new ReservationGetResponse(
                        reservation.getId(),
                        reservation.getUser().getId(),
                        reservation.getTicket().getId(),
                        reservation.getStatus(),
                        reservation.getExpiresAt(),
                        reservation.getCreatedAt(),
                        reservation.getUpdatedAt()
                )).toList();
    }

    @Transactional
    public void expireReservations() {
        List<Reservation> reservations =
                reservationRepository.findAllByStatusAndExpiresAtLessThanEqual(
                        ReservationStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Reservation reservation : reservations) {
            reservation.expire();

            // 줍줍서버로 취소표 알림 Webhook 전송
            publishTicketCanceledWebhookEvent(reservation);
        }

        log.info("만료된 예약 {}건 처리", reservations.size());
    }

    private void publishTicketCanceledWebhookEvent(Reservation reservation) {

        Ticket ticket = reservation.getTicket();

        eventPublisher.publishEvent(
                new TicketCanceledWebhookEvent(
                        ticket.getId(),
                        ticket.getPerformance().getId(),
                        ticket.getSeat().getId(),
                        ticket.getPrice(),
                        LocalDateTime.now()
                )
        );
    }
}
