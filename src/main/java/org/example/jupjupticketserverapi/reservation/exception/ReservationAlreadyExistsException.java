package org.example.jupjupticketserverapi.reservation.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ReservationAlreadyExistsException extends ServiceException {
    public ReservationAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, "RESERVATION_ALREADY_EXIST", message);
    }
}
