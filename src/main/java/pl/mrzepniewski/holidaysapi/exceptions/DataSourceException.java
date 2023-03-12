package pl.mrzepniewski.holidaysapi.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DataSourceException extends RuntimeException {

    public DataSourceException(String message, Throwable throwable) {

        super(message, throwable);
        Logger logger = LoggerFactory.getLogger(DataSourceException.class);
        logger.error(message, throwable);
    }

}
