package org.mule.consulting.cps.extension.api.error;

public class UnretryableConnectionException extends CpsException {

    public UnretryableConnectionException(String message) {
        super(message);
    }

    public UnretryableConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
