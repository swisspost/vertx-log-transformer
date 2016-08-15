package org.swisspush.logtransformer.strategy;

/**
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
public class LogContentException extends Exception {

    public LogContentException(String message) {
        super(message);
    }

    public LogContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
