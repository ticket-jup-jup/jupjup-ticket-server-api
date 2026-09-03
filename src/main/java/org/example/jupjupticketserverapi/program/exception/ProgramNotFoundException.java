package org.example.jupjupticketserverapi.program.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ProgramNotFoundException extends ServiceException {
    public ProgramNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "PROGRAM_NOT_FOUND", message);
    }
}
