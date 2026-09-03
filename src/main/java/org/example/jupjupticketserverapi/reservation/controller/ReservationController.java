package org.example.jupjupticketserverapi.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.reservation.dto.*;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCancelResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationGetResponse;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<ReservationGetResponse>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationCreateResponse>> create(
            @RequestBody ReservationCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.create(request)));
    }

    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<ApiResponse<ReservationConfirmResponse>> confirm(
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationConfirmRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.confirm(reservationId, request)));
    }

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationCancelResponse>> cancel(
            @PathVariable Long reservationId
    ) {
        ReservationCancelResponse response = reservationService.cancel(reservationId);
        return ResponseEntity.ok(ApiResponse.success(List.of(response)));
    }
}
