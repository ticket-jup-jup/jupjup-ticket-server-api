package org.example.jupjupticketserverapi.ticket.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class TicketNotFoundException extends ServiceException {
    public TicketNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "TICKET_NOT_FOUND", message);
    }
}
