package org.mule.consulting.cps.extension.api.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum CpsErrors implements ErrorTypeDefinition<CpsErrors> {
	CpsException,ConfigurationNotFound,ConfigurationsUnavailableException,UnretryableConnectionException
}
