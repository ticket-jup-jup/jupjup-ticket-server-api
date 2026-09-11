package org.example.jupjupticketserverapi.seat.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.seat.dto.SeatGetResponse;
import org.example.jupjupticketserverapi.seat.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<ApiResponse<SeatGetResponse>> getAll(
            @RequestParam(required = false) Long performance
    ) {
        return ResponseEntity.ok(ApiResponse.success(seatService.getAll(performance)));
    }
}
