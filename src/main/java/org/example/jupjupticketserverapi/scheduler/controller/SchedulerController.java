package org.example.jupjupticketserverapi.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final ReservationService reservationService;

    @PostMapping("/reservations/expire")
    public void expireReservations() {
        reservationService.expireReservations();
    }
}