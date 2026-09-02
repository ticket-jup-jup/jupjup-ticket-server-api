package org.example.jupjupticketserverapi.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateResponse;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<ApiResponse<ReservationCreateResponse>> create(
            @PathVariable Long reservationId,
            @RequestBody ReservationCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.create(request)));
    }
}
