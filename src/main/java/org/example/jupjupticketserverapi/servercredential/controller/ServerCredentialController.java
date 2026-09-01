package org.example.jupjupticketserverapi.servercredential.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateRequest;
import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateResponse;
import org.example.jupjupticketserverapi.servercredential.service.ServerCredentialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/server-credential")
public class ServerCredentialController {

    private final ServerCredentialService serverCredentialService;

    @PostMapping
    public ResponseEntity<ApiResponse<ServerCredentialCreateResponse>> create(
            @Valid @RequestBody ServerCredentialCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(serverCredentialService.create(request)));
    }
}
