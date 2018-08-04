package org.mule.consulting.cps.extension.internal;

import org.mule.consulting.cps.extension.api.error.CpsErrors;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;

/**
 * This is the main class of an extension, is the entry point from which
 * configurations, connection providers, operations and sources are going to be
 * declared.
 */
@Xml(prefix = "cps")
@Extension(name = "Configuration-properties-service")
@ErrorTypes(CpsErrors.class)
@Configurations(CpsConfiguration.class)
@Export(resources = "META-INF/services/org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory", classes = org.mule.consulting.cps.extension.api.CpsPropertiesProviderFactory.class)
public class CpsExtension {
	
}
