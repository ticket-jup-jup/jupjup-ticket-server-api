package org.example.jupjupticketserverapi.program.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.program.dto.ProgramGetResponse;
import org.example.jupjupticketserverapi.program.service.ProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProgramGetResponse>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(programService.getAll()));
    }
}
