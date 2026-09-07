package org.example.jupjupticketserverapi.webhook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketStatusWebhookRequest {

    private Long externalTicketId;
    private Long performanceId;
    private String status;
}