package org.example.jupjupticketserverapi.jupjup.client;

import lombok.extern.slf4j.Slf4j;
import org.example.jupjupticketserverapi.reservation.event.TicketCanceledWebhookEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class jupjupClient {

    private final RestClient restClient;

    public jupjupClient(@Value("${jupjup.url}") String url) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
    }

    public void sendTicketCanceledWebhook(TicketCanceledWebhookEvent event) {
        try {
            restClient.post()
                    .uri("/api/internal/webhooks/tickets/canceled")
                    .body(event)
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "줍줍서버 취소표 webhook 전송 성공. ticketId={}, performanceId={}",
                    event.ticketId(),
                    event.performanceId()
            );
        } catch (RestClientException e) {
            log.error(
                    "줍줍서버 취소표 webhook 전송 실패. ticketId={}, performanceId={}, message={}",
                    event.ticketId(),
                    event.performanceId(),
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }
}