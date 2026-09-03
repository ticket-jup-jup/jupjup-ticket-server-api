package org.example.jupjupticketserverapi.performance.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.performance.dto.PerformanceGetResponse;
import org.example.jupjupticketserverapi.performance.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<PerformanceGetResponse>> getAll(
            @RequestParam(required = false) Long program
    ) {
        return ResponseEntity.ok(ApiResponse.success(performanceService.getAll(program)));
    }
}
