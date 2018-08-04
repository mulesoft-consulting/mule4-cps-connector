package org.mule.consulting.cps.extension.api.error;

import java.util.HashSet;
import java.util.Set;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public class CpsErrorProvider implements ErrorTypeProvider {

	@SuppressWarnings("rawtypes")
	@Override
	public Set<ErrorTypeDefinition> getErrorTypes() {
        HashSet<ErrorTypeDefinition> errors = new HashSet<>();
        errors.add(CpsErrors.CpsException);
        errors.add(CpsErrors.ConfigurationNotFound);
        errors.add(CpsErrors.ConfigurationsUnavailableException);
        errors.add(CpsErrors.UnretryableConnectionException);
        return errors;
	}

}
