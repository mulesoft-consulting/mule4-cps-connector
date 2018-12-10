package org.mule.consulting.cps.extension.api.common;

import java.util.Map;

import org.mule.consulting.cps.extension.api.error.ConfigurationNotFoundException;
import org.mule.consulting.cps.extension.api.error.CpsException;

/**
 * Abstracts access to Rest API
 */
public interface ApplicationDataProvider {

    /**
     * Convenience instance to improve readability of the code.
     */
    static ApplicationDataProviderFactory factory = new ApplicationDataProviderFactory();

    /**
     * Load the model for application configuration.
     * @param name
     * @param version
     * @param environment
     * @return
     * @throws ConfigurationServiceException
     */
    ApplicationConfiguration loadApplicationConfiguration(String projectName, String branchName, String instanceId, String envName, String keyId, String clientId, String clientSecret, boolean passCredentialsAsHeaders, Map<String, String> additionalHeaders) throws CpsException;

	Map<String, Object> loadApplication(String projectName, String branchName, String instanceId, String envName,
			String keyId, String clientId, String clientSecret, boolean passCredentialsAsHeaders,
			Map<String, String> additionalHeaders) throws CpsException;
}
