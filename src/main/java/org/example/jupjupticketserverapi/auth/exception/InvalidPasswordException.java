package org.example.jupjupticketserverapi.auth.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ServiceException {
    public InvalidPasswordException(String message) {
        super(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", message);
    }
}
