package org.mule.consulting.cps.extension.api.common.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.mule.consulting.cps.extension.api.CpsPropertiesProviderFactory;
import org.mule.consulting.cps.extension.internal.CpsConfiguration;
import org.mule.consulting.cps.extension.internal.CpsPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class RestClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(RestClientUtil.class);

	public static Client getClient(CpsConfiguration config) throws Exception {

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {

			}
		} };

		// load boot properties
		Properties bootProperties = loadBootProperties();

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustManager, null);

		logger.debug("Setting up connector with properties: {}", config);

		Client client = null;
		if (CpsConfiguration.asBooleanValue(config.getInsecure())) {
			logger.warn("Using Insecure SSL configuration for configuration property service.");
			client = ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(allHostsValid).build();
		} else {
			logger.debug("using Secure SSL");
			client = ClientBuilder.newClient();
		}
		return client.register(JacksonJsonProvider.class);

	}

	private static Properties loadBootProperties() {
		Properties bootProperties = new Properties();
		InputStream input = null;
		try {
			input = RestClientUtil.class.getClassLoader().getResourceAsStream("cps-boot.properties");
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
		return bootProperties;
	}
}
