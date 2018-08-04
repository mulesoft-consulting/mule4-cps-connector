package org.mule.consulting.cps.extension.api.common.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;
import org.mule.consulting.cps.encryption.CpsEncryptor;
import org.mule.consulting.cps.extension.api.common.ApplicationConfiguration;
import org.mule.consulting.cps.extension.api.common.ApplicationConfigurationBuilder;
import org.mule.consulting.cps.extension.api.common.ApplicationDataProvider;
import org.mule.consulting.cps.extension.api.error.ConfigurationsUnavailableException;
import org.mule.consulting.cps.extension.api.error.CpsException;
import org.mule.consulting.cps.extension.api.error.UnretryableConnectionException;
import org.mule.consulting.cps.extension.internal.CpsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestDataProvider implements ApplicationDataProvider {

	private static final Logger logger = LoggerFactory.getLogger(RestDataProvider.class);

	private final CpsConfiguration config;
	private final Client restClient;
	private Properties bootProperties = new Properties();

	private String countStr = System.getProperty("mule.cps.retry.count", "30");
	private String sleepMillisStr = System.getProperty("mule.cps.retry.sleep.millis", "10000");
	private String apiPolicySyncMillisStr = System.getProperty("mule.cps.api.sync.millis", "20000");
	private long sleepMillis = Long.parseLong(sleepMillisStr);
	private long apiPolicySyncMillis = Long.parseLong(sleepMillisStr);

	public RestDataProvider(CpsConfiguration config, Client restClient) {
		this.config = config;
		this.restClient = restClient;

		logger.info("System property mule.cps.retry.count set to " + countStr);
		logger.info("System property mule.cps.retry.sleep.millis set to " + sleepMillisStr);
		logger.info("System property mule.cps.api.sync.millis set to " + apiPolicySyncMillisStr);

		loadBootProperties();
	}


	@Override
	public ApplicationConfiguration loadApplicationConfiguration(String projectName, String branchName,
			String instanceId, String envName, String keyId, String clientId, String clientSecret,
			boolean passCredentialsAsHeaders) throws CpsException {

		ApplicationConfigurationBuilder builder = ApplicationConfiguration.builder();

		Map<String, Object> app = loadApplication(projectName, branchName, instanceId, envName, keyId, clientId,
				clientSecret, passCredentialsAsHeaders);

		builder.setProjectName(projectName).setBranchName(branchName).setInstanceId(instanceId).setEnvName(envName)
				.setKeyId(keyId).setProperties((Map) app.get("properties"));

		List<Map<String, String>> imports = null;
		List<ApplicationConfiguration> importList = new ArrayList<ApplicationConfiguration>();
		if (app.containsKey("imports")) {
			imports = (List<Map<String, String>>) app.get("imports");
		} else if (app.containsKey("parents")) {
			imports = (List<Map<String, String>>) app.get("parents");
		}
		if (imports != null) {
			for (Map<String, String> importParent : imports) {
				importList.add(loadApplicationConfiguration(importParent.get("projectName"),
						importParent.get("branchName"), importParent.get("instanceId"), importParent.get("envName"),
						importParent.get("keyId"), clientId, clientSecret, passCredentialsAsHeaders));
			}
			builder.setImports(importList);
		}

		return builder.build();
	}

	@Override
	public Map<String, Object> loadApplication(String projectName, String branchName, String instanceId, String envName,
			String keyId, String clientId, String clientSecret, boolean passCredentialsAsHeaders) throws CpsException {

		boolean useAuthorizationHeader = !passCredentialsAsHeaders;
		
		// read it as a java map
		Map<String, Object> result = null;
		String serverUri = getValueOrProperty(config.getConfigServerBaseUrl());
		if (serverUri != null && (serverUri.trim().length() > 0 && !serverUri.startsWith("${"))) {
			
			WebTarget target = restClient.target(serverUri).path(getValueOrProperty(projectName))
					.path(getValueOrProperty(branchName)).path(getValueOrProperty(instanceId))
					.path(getValueOrProperty(envName)).path(getValueOrProperty(keyId));
			logger.debug("WebTarget: " + target.getUri().toString());

			Response response = null;

			int statuscode = 0;
			String statusmessage = "";

			try {

				int count = Integer.parseInt(countStr);
				while (true) {
					if (useAuthorizationHeader) {
						StringBuilder aheader = new StringBuilder();
						aheader.append(getValueOrProperty(clientId)).append(":")
						       .append(getValueOrProperty(clientSecret));
						String authString = Base64.encodeAsString(aheader.toString());
						aheader = new StringBuilder();
						aheader.append("Basic ").append(authString);
						response = target.request().accept(MediaType.APPLICATION_JSON)
								.header("Authorization", aheader.toString()).get();
					} else {
						response = target.request().accept(MediaType.APPLICATION_JSON)
								.header("client_id", getValueOrProperty(clientId))
								.header("client_secret", getValueOrProperty(clientSecret)).get();
					}
					try {
						if (response != null) {
							statuscode = response.getStatus();
							if (statuscode == 400 || statuscode == 401) {
								throw new UnretryableConnectionException(
										"CPS Connector Received unretryable Http status code: " + statuscode);
							} else if (statuscode == 503) {
								// allow time for policy to become effective
								logger.error("Received Temporary Service Unavailable...waiting for API policy sync");
								Thread.sleep(apiPolicySyncMillis);
								throw new ConfigurationsUnavailableException("Received Temporary Service Unavailable");
							} else if (statuscode != 200) {
								throw new ConfigurationsUnavailableException("Retryable Http status code");
							}
							result = response.readEntity(Map.class);
							break;
						} else {
							logger.error("No response was received from " + target.toString());
						}
					} catch (UnretryableConnectionException e) {
						statuscode = response.getStatus();
						statusmessage = response.readEntity(String.class);
						throw e;
					} catch (Exception e) {
						statuscode = response.getStatus();
						try {
							statusmessage = response.readEntity(String.class);
						} catch (Exception e2) {
							statusmessage = "";
						}
						logger.error("CPS Connector sleep and retry, received Http status code: " + statuscode);
						logger.error(statusmessage);
						Thread.sleep(sleepMillis);
						count--;

					} finally {
						if (response != null) {
							response.close();
							response = null;
						}
					}
					if (count == 0) {
						throw new ConfigurationsUnavailableException(
								"Retries exhausted, unable to retrieve configurations from server");
					}
				}
			} catch (Exception e) {
				logger.error("Cannot get properties from: " + target.toString());
				logger.error(
						"Check your src/main/resources-filtered/cps-boot.properties file, verify the configuration property service is properly configured, and verify the configuration is loaded into the configuration property service. (try the url manually from a browser)");
				logger.error("Http status code: " + statuscode);
				logger.error(statusmessage);
				throw new ConfigurationsUnavailableException(
						"Configurations unavailable...is the configuration property service running?", e);
			} finally {
				if (response != null) {
					response.close();
				}
			}

			logger.debug("Got settings from configuration property server: {}", result);
		} else {
			result = new LinkedHashMap<String, Object>();
			logger.warn("No configuration property server configured.");
		}

		decryptProperties(result);
		return result;

	}

	public InputStream loadDocument(String docName, String clientId, String clientSecret)
			throws CpsException {

		WebTarget target = restClient.target(getValueOrProperty(config.getConfigServerBaseUrl())).path("document")
				.path(getValueOrProperty(config.getProjectName())).path(getValueOrProperty(config.getBranchName()))
				.path(getValueOrProperty(config.getInstanceId())).path(getValueOrProperty(config.getEnvName()))
				.path(docName);

		logger.debug("loadDocument @" + target.getUri());

		return target.request().header("client_id", getValueOrProperty(clientId))
				.header("client_secret", getValueOrProperty(clientSecret)).get(InputStream.class);
	}

	private void loadBootProperties() {
		InputStream input = null;
		try {
			input = loadClasspathResource("cps-boot.properties");
			if (input != null) {
				bootProperties.load(input);
			}
		} catch (IOException ex) {
			logger.warn(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	private String getValueOrProperty(String value) {
		if (value.startsWith("${")) {
			if (!value.contains(":")) {
				String systemProperty = value.substring(2, value.length() - 1);
				String propValue = bootProperties.getProperty(systemProperty);
				if (propValue != null) {
					return propValue;
				}
				propValue = System.getProperty(systemProperty);
				if (propValue != null) {
					return propValue;
				}
				return value;
			} else if (value.contains(":")) {
				String propValue = value.substring(value.indexOf(':') + 1, value.length() - 1);
				if (propValue != null) {
					return propValue;
				}
				return null;
			}
		}
		return value;
	}

    public InputStream loadClasspathResource(String resourceName) {

        InputStream is = null;

        if (Thread.currentThread().getContextClassLoader() != null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
        	this.getClass().getClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
            logger.warn("Could not find resource: {} either on classpath or filesystem...", resourceName);
        }

        return is;

    }

	private void decryptProperties(Map<String, Object> payload) {

		String keyId = (String) payload.get("keyId");
		String cipherKey = (String) payload.get("cipherKey");
		if (keyId == null || keyId.isEmpty()) {
			String msg = "Need a keyId to be specified in the configuration file, cannot continue with decrypt";
			logger.error(msg);
			return;
		}

		if (cipherKey == null || cipherKey.isEmpty()) {
			String msg = "No cipherKey is specified in the configuration file, nothing to decrypt";
			logger.info(msg);
			return;
		}

		Map<String, String> properties = (Map<String, String>) payload.get("properties");
		try {
			CpsEncryptor cpsEncryptor = new CpsEncryptor(keyId, cipherKey);
			for (String key : properties.keySet()) {
				String encryptedValue = properties.get(key);
				String value = cpsEncryptor.decrypt(encryptedValue);
				properties.put(key, value);
			}
		} catch (Exception e) {
			logger.error("Decryption of properties failed");
			e.printStackTrace();
			return;
		}
	}
}
