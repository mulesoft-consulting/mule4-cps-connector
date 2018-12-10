package org.mule.consulting.cps.extension.api.common.fileclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.mule.consulting.cps.extension.api.error.CpsException;
import org.mule.consulting.cps.extension.api.common.ApplicationConfiguration;
import org.mule.consulting.cps.extension.api.common.ApplicationConfigurationBuilder;
import org.mule.consulting.cps.extension.api.common.ApplicationDataProvider;
import org.mule.consulting.cps.extension.api.error.ConfigurationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provider that loads data from the classpath instead of reaching out to
 * configuration service. Local config file should be called
 * {projectName}-{branchName}-{instanceId}-{envName}-{keyId}-config.json in the classpath.
 */
public class LocalFileDataProvider implements ApplicationDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileUtil.class);

    private String localFileName;
	
	public LocalFileDataProvider(String path, String projectName, String branchName, String instanceId, String envName,
			String keyId) {
		StringBuilder sb = new StringBuilder();
		sb.append(path)
		  .append(projectName).append("-")
		  .append(branchName).append("-")
		  .append(instanceId).append("-")
		  .append(envName).append("-")
		  .append(keyId).append("-config.json");
		localFileName = sb.toString();
	}
	
	@Override
	public Map<String, Object> loadApplication(String projectName, String branchName, String instanceId, String envName,
			String keyId, String clientId, String clientSecret, boolean passCredentialsAsHeaders, Map<String, String> additionalHeaders) throws ConfigurationNotFoundException {

		logger.info("Loading local file: {}", localFileName);
		logger.info("Will attempt to load classpathResource {}", localFileName);
		LocalFileUtil localFileUtil = new LocalFileUtil();
		InputStream is = localFileUtil.loadClasspathResource(localFileName);

		if (is == null) {
			throw new RuntimeException("Local file not found!! " + "Please check that "
					+ localFileName + " exists in classpath!");
		}

		ObjectMapper om = new ObjectMapper();

		try {
			return om.readValue(is, Map.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ApplicationConfiguration loadApplicationConfiguration(String projectName, String branchName,
			String instanceId, String envName, String keyId, String clientId, String clientSecret, boolean passCredentialsAsHeaders, Map<String, String> additionalHeaders) throws CpsException {

		ApplicationConfigurationBuilder builder = ApplicationConfiguration.builder();

		Map<String, Object> app = loadApplication(projectName, branchName, instanceId, envName, keyId, null, null, false, null);

		builder.setProjectName(projectName).setBranchName(branchName).setInstanceId(instanceId).setEnvName(envName)
				.setKeyId(keyId).setProperties((Map) app.get("properties"));

		if (app.containsKey("imports")) {
			logger.warn("Local file contains imports definition, which is not supported!");
		}

		return builder.build();
	}

}
