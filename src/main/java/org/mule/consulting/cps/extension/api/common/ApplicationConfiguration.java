package org.mule.consulting.cps.extension.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfiguration implements Serializable {

	private String projectName;
	private String branchName;
	private String instanceId;
	private String envName;
	private String keyId;
	private Properties bootProperties = new Properties();
	private Properties projectFileProperties = new Properties();
	private final Map<String, String> properties;
	private final List<ApplicationConfiguration> imports;
	private final ConfigurationDataWrapper dataWrapper;

	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

	public static ApplicationConfigurationBuilder builder() {
		return new ApplicationConfigurationBuilder();
	}

	ApplicationConfiguration(String projectName, String branchName, String instanceId, String envName, String keyId,
			Map<String, String> properties, List<ApplicationConfiguration> imports,
			ConfigurationDataWrapper dataWrapper) {
		this.projectName = projectName;
		this.branchName = branchName;
		this.instanceId = instanceId;
		this.envName = envName;
		this.keyId = keyId;
		this.properties = properties;
		this.imports = imports;
		this.dataWrapper = dataWrapper;
		
		InputStream input = null;
		try {
			input = loadClasspathResource("cps-boot.properties");
			if (input != null) {
				bootProperties.load(input);
			} else {
				logger.info("cps-boot.properties is null");
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
		String projectPropertyFileName = envName.toUpperCase() + "-config.properties";
		try {
			input = loadClasspathResource(projectPropertyFileName);
			if (input != null) {
				projectFileProperties.load(input);
			} else {
				logger.info(projectPropertyFileName + " is null");
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

	public Map<String, String> getProperties() {

		Map<String, String> properties = this.properties;

		if (dataWrapper != null) {
			return dataWrapper.wrapProperties(properties);
		}

		return properties;
	}

	public List<ApplicationConfiguration> getImports() {
		return imports;
	}

	public String readProperty(String keyOrDefault) {

		logger.debug("project properties: " + projectFileProperties);
		logger.debug("properties: " + properties);
		logger.debug("Call to read key {}", keyOrDefault);
		
		String[] keyOrDefaultx = keyOrDefault.split(":",2);
		String key = keyOrDefaultx[0];

		String prop = null;
		
		prop = projectFileProperties.getProperty(key);
		if (prop != null) {
			logger.debug("project file property: " + prop);
			return prop;
		}

		// try with this one
		prop = properties.get(key);
		if (prop != null) {
			logger.debug("config property: " + prop);
			return prop;
		}

		// if the property is not here, may be in one of the imports.
		for (ApplicationConfiguration importedApp : imports) {
			prop = importedApp.readProperty(key);
			if (prop != null) {
				return prop;
			}
		}
		
		prop = bootProperties.getProperty(key);
		logger.debug("bootProperties: " + prop);
		if (prop != null) {
			return prop;
		}

		if (keyOrDefault.contains(":")) {
			logger.debug("using default value");
			return keyOrDefaultx[1];
		}
		
		return null;
	}

	public ConfigurationDataWrapper getDataWrapper() {
		return dataWrapper;
	}

	private String findWrappedKey(String plainKey) {

		if (dataWrapper == null) {
			return plainKey;
		}

		logger.debug("Invoking data wrapper to unwrap existing keys...");

		for (String wrappedKey : properties.keySet()) {
			if (dataWrapper.wrapKey(wrappedKey).equals(plainKey)) {
				return wrappedKey;
			}
		}

		logger.debug("key not found, resorting to unwrapped key...");

		return plainKey;
	}

	private String getValueOrProperty(String value) {
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

}
