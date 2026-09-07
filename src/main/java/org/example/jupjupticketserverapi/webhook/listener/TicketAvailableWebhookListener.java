package org.example.jupjupticketserverapi.webhook.listener;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.webhook.client.JupjupWebhookClient;
import org.example.jupjupticketserverapi.webhook.event.TicketAvailableEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TicketAvailableWebhookListener {
    //예약 REFUNDED(ReservationService.cancel())->DB COMMIT 완료 후 webhook 전송하기 위한 listener
    private final JupjupWebhookClient jupjupWebhookClient;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(TicketAvailableEvent event) {

        jupjupWebhookClient.notifyTicketAvailable(
                event.externalTicketId(),
                event.performanceId()
        );
    }
}