package org.example.jupjupticketserverapi.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCancelResponse;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.reservation.exception.ReservationNotCancellableException;
import org.example.jupjupticketserverapi.reservation.exception.ReservationNotFoundException;
import org.example.jupjupticketserverapi.reservation.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationCancelResponse cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ReservationNotFoundException("예약을 찾을 수 없습니다.")
        );

        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationNotCancellableException(
                    "취소할 수 없는 예약입니다. 현재 상태: " + reservation.getStatus());
        }

        reservation.cancel();
        return ReservationCancelResponse.from(reservation);
    }
}
