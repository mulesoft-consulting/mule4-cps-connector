package org.mule.consulting.cps.extension.api.common;

import org.apache.commons.lang3.StringUtils;
import org.mule.consulting.cps.extension.api.common.fileclient.LocalFileDataProvider;
import org.mule.consulting.cps.extension.api.common.restclient.RestClientUtil;
import org.mule.consulting.cps.extension.api.common.restclient.RestDataProvider;
import org.mule.consulting.cps.extension.internal.CpsConfiguration;

import javax.ws.rs.client.Client;

public class ApplicationDataProviderFactory {

    public ApplicationDataProvider newApplicationDataProvider(CpsConfiguration config) throws Exception {

		if (config.getConfigServerBaseUrl().startsWith("file://")) {
			return new LocalFileDataProvider(config.getConfigServerBaseUrl().substring(7), config.getProjectName(),
					config.getBranchName(), config.getInstanceId(), config.getEnvName(), config.getKeyId());
        } else {
            Client client = RestClientUtil.getClient(config);
            return new RestDataProvider(config, client);
        }

    }

}
