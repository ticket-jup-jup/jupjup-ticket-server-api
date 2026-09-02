package org.example.jupjupticketserverapi.reservation.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ReservationNotFoundException extends ServiceException {
    public ReservationNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", message);
    }
}
