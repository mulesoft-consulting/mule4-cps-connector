package org.mule.consulting.cps.extension.internal;

import org.mule.consulting.cps.extension.api.common.ApplicationConfiguration;
import org.mule.consulting.cps.extension.api.error.CpsException;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import java.util.Optional;

public class CpsPropertiesProvider implements ConfigurationPropertiesProvider {
	private static final Logger logger = LoggerFactory.getLogger(CpsPropertiesProvider.class);

    private final ApplicationConfiguration config;
    private final String serverBaseUrl;

    public CpsPropertiesProvider(String serverBaseUrl, ApplicationConfiguration config) throws CpsException {
        this.config = config;
        this.serverBaseUrl = serverBaseUrl;
    	
    	logger.debug("Enter cpsPropertiesProvider constructor");
    	logger.debug(getDescription());
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
    	
    	logger.debug("Enter getConfigurationProperty for: " + configurationAttributeKey);
    	logger.debug(getDescription());

        String value = config.readProperty(configurationAttributeKey);

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(new ConfigurationProperty() {
            @Override
            public Object getSource() {
                return value;
            }

            @Override
            public Object getRawValue() {
                return value;
            }

            @Override
            public String getKey() {
                return configurationAttributeKey;
            }
        });

    }

    @Override
    public String getDescription() {
        return "CPS: " + serverBaseUrl + " and coordinates: {" +
                "projectName: " + config.getProjectName() +
                ", branchName: " + config.getBranchName() +
                ", instanceId: " + config.getInstanceId() +
                ", envName: " + config.getEnvName() +
                ", keyId: " + config.getKeyId()
                + "}";
    }
}
