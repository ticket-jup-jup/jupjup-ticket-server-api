package org.example.jupjupticketserverapi.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCancelResponse;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationCancelResponse>> cancel(
            @PathVariable Long reservationId
    ) {
        ReservationCancelResponse response = reservationService.cancel(reservationId);
        return ResponseEntity.ok(ApiResponse.success(List.of(response)));
    }
}
