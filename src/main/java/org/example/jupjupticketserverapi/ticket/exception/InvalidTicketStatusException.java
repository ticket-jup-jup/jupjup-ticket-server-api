package org.example.jupjupticketserverapi.ticket.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class InvalidTicketStatusException extends ServiceException {
    public InvalidTicketStatusException(String message) {
        super(HttpStatus.NOT_FOUND, "INVALID_TICKET_STATUS", message);
    }
}
