package org.example.jupjupticketserverapi.reservation.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class InvalidReservationStatusException extends ServiceException {
    public InvalidReservationStatusException(String message) {
        super(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", message);
    }
}
