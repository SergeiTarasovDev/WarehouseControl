package ru.teliontech.warehousecontrol.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidArgumentException extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvalidArgumentException.class);

    public InvalidArgumentException(String message) {
        super(message);
        LOGGER.warn(message);
    }
}