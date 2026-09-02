package org.example.jupjupticketserverapi.reservation.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.payment.dto.PaymentResponse;

@Getter
public class ReservationConfirmResponse {

    private final ReservationResponse reservationResponse;
    private final PaymentResponse paymentResponse;

    public ReservationConfirmResponse(ReservationResponse reservationResponse, PaymentResponse paymentResponse) {
        this.reservationResponse = reservationResponse;
        this.paymentResponse = paymentResponse;
    }
}
