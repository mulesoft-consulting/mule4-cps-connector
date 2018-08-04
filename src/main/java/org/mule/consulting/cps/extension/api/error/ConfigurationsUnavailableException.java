package org.mule.consulting.cps.extension.api.error;

public class ConfigurationsUnavailableException extends CpsException {

    public ConfigurationsUnavailableException(String message) {
        super(message);
    }

    public ConfigurationsUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
