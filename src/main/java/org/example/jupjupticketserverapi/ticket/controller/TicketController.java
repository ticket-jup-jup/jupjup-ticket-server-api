package org.example.jupjupticketserverapi.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.ticket.dto.TicketGetResponse;
import org.example.jupjupticketserverapi.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<ApiResponse<TicketGetResponse>> getTickets(
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Long performance
    ) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTickets(program, performance)));
    }
}
