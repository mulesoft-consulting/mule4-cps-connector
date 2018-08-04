package org.mule.consulting.cps.extension.api.error;

public class ConfigurationNotFoundException extends CpsException {

    public ConfigurationNotFoundException(String message) {
        super(message);
    }

    public ConfigurationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
