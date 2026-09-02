package org.example.jupjupticketserverapi.reservation.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ReservationNotCancellableException extends ServiceException {
    public ReservationNotCancellableException(String message) {
        super(HttpStatus.CONFLICT, "RESERVATION_NOT_CANCELLABLE", message);
    }
}
