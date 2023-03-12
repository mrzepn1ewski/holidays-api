package pl.mrzepniewski.holidaysapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DataSourceException extends RuntimeException {

    public DataSourceException(String message, Throwable throwable) {

        super(message, throwable);
        Logger logger = LoggerFactory.getLogger(DataSourceException.class);
        logger.error(message, throwable);
    }

}
