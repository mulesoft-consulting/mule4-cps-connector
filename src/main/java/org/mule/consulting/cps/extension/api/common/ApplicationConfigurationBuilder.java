package org.mule.consulting.cps.extension.api.common;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApplicationConfigurationBuilder {

	private String projectName;
	private String branchName;
	private String instanceId;
	private String envName;
	private String keyId;
	private Map<String, String> properties;
	private List<ApplicationConfiguration> imports;
	private ConfigurationDataWrapper dataWrapper;

	public ApplicationConfigurationBuilder setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public ApplicationConfigurationBuilder setBranchName(String branchName) {
		this.branchName = branchName;
		return this;
	}

	public ApplicationConfigurationBuilder setInstanceId(String instanceid) {
		this.instanceId = instanceId;
		return this;
	}

	public ApplicationConfigurationBuilder setEnvName(String envName) {
		this.envName = envName;
		return this;
	}

	public ApplicationConfigurationBuilder setKeyId(String keyId) {
		this.keyId = keyId;
		return this;
	}

	public ApplicationConfigurationBuilder setProperties(Map<String, String> properties) {
		this.properties = properties;
		return this;
	}

	public ApplicationConfigurationBuilder setImports(List<ApplicationConfiguration> imports) {
		this.imports = imports;
		return this;
	}

	public ApplicationConfigurationBuilder setDataWrapper(ConfigurationDataWrapper wrapper) {
		this.dataWrapper = wrapper;
		return this;
	}

	public ApplicationConfiguration build() {

		if (imports == null) {
			imports = Collections.emptyList();
		}

		if (properties == null) {
			properties = Collections.emptyMap();
		}

		return new ApplicationConfiguration(projectName, branchName, instanceId, envName, keyId,
				Collections.unmodifiableMap(properties), Collections.unmodifiableList(imports), dataWrapper);
	}

	public ApplicationConfigurationBuilder importApp(ApplicationConfiguration config) {

		synchronized (this) {
			if (imports == null) {
				imports = new LinkedList<>();
			}
		}

		imports.add(config);

		return this;
	}
}