package org.example.jupjupticketserverapi.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCancelResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateResponse;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationCreateResponse>> create(
            @RequestBody ReservationCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.create(request)));
    }

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationCancelResponse>> cancel(
            @PathVariable Long reservationId
    ) {
        ReservationCancelResponse response = reservationService.cancel(reservationId);
        return ResponseEntity.ok(ApiResponse.success(List.of(response)));
    }
}
