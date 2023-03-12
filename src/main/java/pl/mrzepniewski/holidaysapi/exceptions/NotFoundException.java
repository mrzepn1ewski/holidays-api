package pl.mrzepniewski.holidaysapi.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {

        super(message);
        Logger logger = LoggerFactory.getLogger(NotFoundException.class);
        logger.info(message);
    }


}
