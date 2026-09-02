package org.example.jupjupticketserverapi.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public List<ReservationCreateResponse> create(
            ReservationCreateRequest request
    ) {

        // 유저 정보 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("존재하지 않는 유저입니다.")
                );

        // 티켓 정보 조회
        Ticket ticket = ticketRepository.findById(request.getTicketId())
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

        // 예약 생성
        Reservation reservation = new Reservation(
                user,
                ticket,
                expiresAt
        );

        Reservation savedReservation =
                reservationRepository.save(reservation);

        ReservationResponse reservationResponse =
                new ReservationResponse(
                        savedReservation.getId(),
                        savedReservation.getUser().getId(),
                        savedReservation.getTicket().getId(),
                        savedReservation.getStatus().name(),
                        savedReservation.getExpiresAt(),
                        savedReservation.getCreatedAt(),
                        savedReservation.getUpdatedAt()
                );

        return List.of(
                new ReservationCreateResponse(
                        reservationResponse,
                        null
                )
        );
    }
}