package org.example.jupjupticketserverapi.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReservationCreateRequest {

    @NotBlank(message = "사용자 ID를 입력해주세요.")
    private Long userId;

    @NotBlank(message = "티켓 ID를 입력해주세요.")
    private Long ticketId;
}
