package org.example.jupjupticketserverapi.webhook.client;

import lombok.extern.slf4j.Slf4j;
import org.example.jupjupticketserverapi.webhook.dto.TicketStatusWebhookRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class JupjupWebhookClient {

    private final RestClient restClient;

    public JupjupWebhookClient(
            RestClient.Builder restClientBuilder,
            @Value("${jupjup-api.url:http://localhost:8080}")
            String jupjupApiUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(jupjupApiUrl)
                .build();
    }

    public void notifyTicketAvailable(
            Long externalTicketId,
            Long performanceId
    ) {

        try {
            restClient.post()
                    .uri("/api/internal/webhooks/tickets")
                    .body(
                            new TicketStatusWebhookRequest(
                                    externalTicketId,
                                    performanceId,
                                    "AVAILABLE"
                            )
                    )
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "Webhook 전송 성공: externalTicketId={}",
                    externalTicketId
            );

        } catch (RestClientException e) {

            // 중요:
            // Webhook 실패 때문에 원래의 '예약 취소'까지 실패시키지 않는다.
            // 놓친 이벤트는 Polling이 보완한다.
            log.warn(
                    "Webhook 전송 실패. Polling에서 보완 예정: externalTicketId={}, message={}",
                    externalTicketId,
                    e.getMessage()
            );
        }
    }
}