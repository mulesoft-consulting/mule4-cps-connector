package org.mule.consulting.cps.extension.api;

import org.mule.consulting.cps.extension.api.common.ApplicationConfiguration;
import org.mule.consulting.cps.extension.api.common.ApplicationDataProvider;
import org.mule.consulting.cps.extension.api.error.CpsException;
import org.mule.consulting.cps.extension.internal.CpsConfiguration;
import org.mule.consulting.cps.extension.internal.CpsPropertiesProvider;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.util.Preconditions;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mulesoft.common.ext.Diff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class CpsPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(CpsPropertiesProviderFactory.class);

    public static final ComponentIdentifier CONFIG_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("cps")
                    .name("config")
                    .build();

    public static final ComponentIdentifier HEADERS_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("cps")
                    .name("custom-headers")
                    .build();

    public static final ComponentIdentifier HEADER_IDENTIFIER =
            ComponentIdentifier.builder()
                    .namespace("cps")
                    .name("custom-header")
                    .build();

    @Override
    public ComponentIdentifier getSupportedComponentIdentifier() {
        return CONFIG_IDENTIFIER;
    }

    @Override
    public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters, ResourceProvider externalResourceProvider) {

        try {
            String configServerBaseUrl = parameters.getStringParameter("configServerBaseUrl");
            String insecure = getOptionalStringParemeter(parameters, "insecure");
            String projectName = parameters.getStringParameter("projectName");
            String branchName = parameters.getStringParameter("branchName");
            String instanceId = parameters.getStringParameter("instanceId");
            String envName = parameters.getStringParameter("envName");
            String keyId = parameters.getStringParameter("keyId");
            String clientId = parameters.getStringParameter("clientId");
            String clientSecret = parameters.getStringParameter("clientSecret");
            String passCredentialsAsHeaders = getOptionalStringParemeter(parameters, "passCredentialsAsHeaders");


            Map<String, String> headers = new LinkedHashMap<>();

            List<ConfigurationParameters> headerConfigs = parameters.getComplexConfigurationParameter(HEADERS_IDENTIFIER);

            //this could be just one
            Optional<ConfigurationParameters> headersParam = headerConfigs.stream().findAny();

            if (headersParam.isPresent()) {
                List<ConfigurationParameters> headersParams = headersParam.get().getComplexConfigurationParameter(HEADER_IDENTIFIER);
                headersParams.forEach(hp -> {
                    String key = getOptionalStringParemeter(hp, "key");
                    String value = getOptionalStringParemeter(hp, "value");
                    logger.debug("Header Name: {},  value: {}", key, value);
                    headers.put(key, value);
                });
            }


            Preconditions.checkArgument(configServerBaseUrl != null, "configServerBaseUrl must not be null");
            Preconditions.checkArgument(projectName != null, "projectName must not be null");
            Preconditions.checkArgument(branchName != null, "branchName must not be null");
            Preconditions.checkArgument(instanceId != null, "instanceId must not be null");
            Preconditions.checkArgument(envName != null, "envName must not be null");
            Preconditions.checkArgument(keyId != null, "keyId must not be null");
            Preconditions.checkArgument(clientId != null, "clientId must not be null");
            Preconditions.checkArgument(clientSecret != null, "clientSecret must not be null");


            CpsConfiguration config = new CpsConfiguration();
            config.setConfigServerBaseUrl(configServerBaseUrl);
            config.setInsecure(insecure);
            config.setProjectName(projectName);
            config.setBranchName(branchName);
            config.setInstanceId(instanceId);
            config.setEnvName(envName);
            config.setKeyId(keyId);
            config.setClientId(clientId);
            config.setClientSecret(clientSecret);
            config.setPassCredentialsAsHeaders(passCredentialsAsHeaders);
            config.setAdditionalHeaders(headers);

            ApplicationDataProvider provider = ApplicationDataProvider.factory.newApplicationDataProvider(config);

            ApplicationConfiguration appConfig = provider.loadApplicationConfiguration(config.getProjectName(),config.getBranchName(),
            		config.getInstanceId(), config.getEnvName(), config.getKeyId(), config.getClientId(), config.getClientSecret(), 
            		CpsConfiguration.asBooleanValue(config.getPassCredentialsAsHeaders()));

            //store in static config cache for further use.
            //StaticConfigCache.get().store(name, config, appConfig);

            return new CpsPropertiesProvider(configServerBaseUrl, appConfig);
        } catch (Exception ex) {
            logger.error("Error while loading configuration!", ex);
            throw new RuntimeException("Error while loading configuration", ex);
        }
    }

    private String getOptionalStringParemeter(ConfigurationParameters params, String key) {
        try {
            return params.getStringParameter(key);
        } catch (NullPointerException ex) {
            logger.debug("Parameter {} is not present as part of configuration Parameters: ", key, params);
            //GRRRR
            return null;
        }
    }

}
