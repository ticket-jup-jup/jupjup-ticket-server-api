package org.example.jupjupticketserverapi.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.ticket.dto.TicketGetResponse;
import org.example.jupjupticketserverapi.ticket.dto.TicketInternalGetReesponse;
import org.example.jupjupticketserverapi.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/api/tickets")
    public ResponseEntity<ApiResponse<TicketGetResponse>> getTickets(
            @RequestParam(required = false) Long program,
            @RequestParam(required = false) Long performance
    ) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTickets(program, performance)));
    }

    @GetMapping("/api/internal/tickets")
    public ResponseEntity<ApiResponse<TicketInternalGetReesponse>> getInternalTickets(
            @RequestParam(required = false) Long performance,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getInternalTickets(performance, status)));
    }
}
