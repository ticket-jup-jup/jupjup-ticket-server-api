package org.example.jupjupticketserverapi.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.jupjupticketserverapi.payment.entity.PaymentMethod;

@Getter
public class ReservationConfirmRequest {

    @NotNull(message = "결제 수단을 입력해주세요.")
    private PaymentMethod paymentMethod;
}
