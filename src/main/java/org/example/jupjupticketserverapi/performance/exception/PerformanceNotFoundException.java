package org.example.jupjupticketserverapi.performance.exception;

import org.example.jupjupticketserverapi.global.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class PerformanceNotFoundException extends ServiceException {
    public PerformanceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "PERFORMANCE_NOT_FOUND", message);
    }
}
