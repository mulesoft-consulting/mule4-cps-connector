package org.mule.consulting.cps.extension.internal;

import java.util.Map;

import org.mule.consulting.cps.extension.api.CpsPropertiesProviderFactory;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Example;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an extension configuration, values set in this class
 * are commonly used across multiple operations since they represent something
 * core from the extension.
 */
@Operations(CpsOperations.class)
public class CpsConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CpsConfiguration.class);

	@Parameter
	private String configId;

	@Parameter
	@Optional(defaultValue = "")
	@Placement(order = 1)
	@Summary("Base URL of Configuration Server.")
	@Example("https://localhost:9084/configuration-property-service/v1/config")
	private String configServerBaseUrl;

	@Parameter
	@Optional(defaultValue = "false")
	@Placement(order = 2)
	@Summary("Base URL is Insecure SSL.")
	@Example("false")
	private String insecure;

	@Parameter
	@Placement(order = 3)
	@Summary("Source code project name associated with this configuration.")
	@Example("base")
	private String projectName;

	@Parameter
	@Placement(order = 4)
	@Summary("Source code branch name associated with this configuration.")
	@Example("base")
	private String branchName;

	@Parameter
	@Placement(order = 5)
	@Summary("Deployed instance id for this configuration.")
	@Example("base")
	private String instanceId;

	@Parameter
	@Placement(order = 6)
	@Summary("The environment this configuration is used in.")
	@Example("DEV")
	private String envName;

	@Parameter
	@Placement(order = 7)
	@Summary("Encryption key for this configuration.")
	@Example("base")
	private String keyId;

	@Parameter
	@Placement(order = 8)
	@Summary("The client id to use when accessing the configuration service.")
	@Example("bc80c73ad5fb4f21b4fc620c443eecae")
	private String clientId;

	@Parameter
	@Placement(order = 9)
	@Summary("The client secret to use when accessing the configuration service.")
	@Password
	private String clientSecret;

	@Parameter
	@Optional(defaultValue = "true")
	@Placement(order = 10)
	@Summary("Send the credentials as Headers to the configuration service.")
	@Example("true")
	private String passCredentialsAsHeaders;

    @Parameter
    @Optional @Placement(order = 11)
    @Summary("Additional headers to send.")
    private Map<String, String> additionalHeaders;

	public String getConfigId() {
		return configId;
	}

	public String getConfigServerBaseUrl() {
		return configServerBaseUrl;
	}

	public String getInsecure() {
		return insecure;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getEnvName() {
		return envName;
	}

	public String getKeyId() {
		return keyId;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getPassCredentialsAsHeaders() {
		return passCredentialsAsHeaders;
	}

	public Map<String, String> getAdditionalHeaders() {
		return additionalHeaders;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public void setConfigServerBaseUrl(String configServerBaseUrl) {
		this.configServerBaseUrl = configServerBaseUrl;
	}

	public void setInsecure(String insecure) {
		this.insecure = insecure;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setPassCredentialsAsHeaders(String passCredentialsAsHeaders) {
		this.passCredentialsAsHeaders = passCredentialsAsHeaders;
	}

	public void setAdditionalHeaders(Map<String, String> additionalHeaders) {
		this.additionalHeaders = additionalHeaders;
	}

	public static boolean asBooleanValue(String booleanValueAsString) {
		logger.debug("baseUrlIsInsecureSSL:" + booleanValueAsString);
		String value = null;
		try {
			if (booleanValueAsString == null) {
				logger.debug("booleanValueAsString: false");
				return false;
			} else if (booleanValueAsString.startsWith("${")) {
				String systemProperty = booleanValueAsString.substring(2, booleanValueAsString.length() - 1);
				value = System.getProperty(systemProperty);
				logger.debug("booleanValueAsString: " + value);
			} else {
				value = booleanValueAsString;
			}

			if (value != null) {
				logger.debug("booleanValueAsString: " + value);
				if (value.trim().toLowerCase().equals("true"))
					return true;
				if (value.trim().toLowerCase().equals("yes"))
					return true;
				if (value.trim().toLowerCase().equals("1"))
					return true;
				if (value.trim().toLowerCase().equals("y"))
					return true;
				return false;
			} else {
				logger.debug("booleanValueAsString: false due to null value");
				return false;
			}
		} catch (Exception e) {
			logger.debug("booleanValueAsString: false due to exception");
			return false;
		}
	}

	public String toString() {
		return "{\"configServerBaseUrl\": \"" 
				+ configServerBaseUrl + "\", \"projectName\": \"" 
				+ projectName + "\", \"branchName\": \"" 
				+ branchName + "\", \"instanceId\": \"" 
				+ instanceId + "\", \"envName\": \""
				+ envName + "\", \"keyId\": \"" 
				+ keyId + "\"}";
	}

}
