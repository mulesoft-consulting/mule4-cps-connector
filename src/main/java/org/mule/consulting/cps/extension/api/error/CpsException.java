package org.mule.consulting.cps.extension.api.error;

public class CpsException extends Exception {

    public CpsException(String message) {
        super(message);
    }

    public CpsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CpsException(Throwable cause) {
        super(cause);
    }

    public CpsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
