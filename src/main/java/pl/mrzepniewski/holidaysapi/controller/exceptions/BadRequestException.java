package pl.mrzepniewski.holidaysapi.controller.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {

        super(message);
        Logger logger = LoggerFactory.getLogger(BadRequestException.class);
        logger.info(message);
    }
}
