package org.example.jupjupticketserverapi.reservation.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jupjupticketserverapi.jupjup.client.jupjupClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketCanceledWebhookEventListener {

    private final jupjupClient jupJupClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TicketCanceledWebhookEvent event) {
        log.info(
                "취소표 webhook 이벤트 수신. ticketId={}, performanceId={}",
                event.ticketId(),
                event.performanceId()
        );

        jupJupClient.sendTicketCanceledWebhook(event);
    }
}