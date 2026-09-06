package org.example.jupjupticketserverapi.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 60_000) // 1분마다 실행
    public void expireReservations() {
        log.info("예약 만료 처리 시작");

        reservationService.expireReservations();

        log.info("예약 만료 처리 종료");
    }
}