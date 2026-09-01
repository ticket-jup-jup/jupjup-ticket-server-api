package org.example.jupjupticketserverapi.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.auth.dto.AuthVerifyRequest;
import org.example.jupjupticketserverapi.auth.dto.AuthVerifyResponse;
import org.example.jupjupticketserverapi.auth.service.AuthService;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthVerifyResponse>> verify(
            @Valid @RequestBody AuthVerifyRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.verify(request)));
    }

}
