package org.mule.consulting.cps.extension.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class CpsOperations {

  /**
   * Return a JSON string containing the CPS configuration .
   */
  @MediaType(value = ANY, strict = false)
  public String getConfig(@Config CpsConfiguration configuration) {
    return configuration.toString();
  }
}
