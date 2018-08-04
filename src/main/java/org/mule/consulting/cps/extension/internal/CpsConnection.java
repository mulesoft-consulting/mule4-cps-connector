package org.mule.consulting.cps.extension.internal;

import java.util.Properties;

import org.mule.runtime.extension.api.annotation.param.Config;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class CpsConnection extends PreferencesPlaceholderConfigurer {

  private final String id;

  public CpsConnection(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void invalidate() {
    // do something to invalidate this connection!
  }

	@Override
	protected String resolvePlaceholder(String placeholder, Properties p) {
		System.out.println("******Call to resolve placeholder: " + placeholder);

		// String value = this.appConfig.readProperty(placeholder);
		//
		// if (value != null) {
		// logger.debug("Found key in config server");
		// return value;
		// }

		System.out.println("*****Key not found in config server, resolving in the traditional way");
		return super.resolvePlaceholder(placeholder, p);
	}
}
