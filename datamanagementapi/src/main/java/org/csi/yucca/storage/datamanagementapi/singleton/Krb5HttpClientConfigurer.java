package org.csi.yucca.storage.datamanagementapi.singleton;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.impl.HttpClientConfigurer;
import org.apache.solr.client.solrj.impl.SolrPortAwareCookieSpecFactory;
import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Krb5HttpClientConfigurer extends HttpClientConfigurer {
	public static final String LOGIN_CONFIG_PROP = "java.security.auth.login.config";
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// private static final Configuration jaasConfig = new
	// SolrJaasConfiguration();
	private static Configuration jaasConfig = new SolrJaasConfiguration();
	private String clienName = "ClientSolrJ";

	private HttpRequestInterceptor bufferedEntityInterceptor;

	public Krb5HttpClientConfigurer(String client) {
		this.clienName = client;
		jaasConfig = new SolrJaasConfiguration(this.clienName);

		this.bufferedEntityInterceptor = new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
				if (request instanceof HttpEntityEnclosingRequest) {
					HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
					HttpEntity requestEntity = enclosingRequest.getEntity();
					enclosingRequest.setEntity(new BufferedHttpEntity(requestEntity));
				}
			}
		};
	}

	public void configure_orig(DefaultHttpClient httpClient, SolrParams config) {
		super.configure(httpClient, config);

		if (System.getProperty("java.security.auth.login.config") != null) {
			String configValue = System.getProperty("java.security.auth.login.config");

			if (configValue != null) {
				logger.info("Setting up SPNego auth with config: " + configValue);
				String useSubjectCredsProp = "javax.security.auth.useSubjectCredsOnly";
				String useSubjectCredsVal = System.getProperty("javax.security.auth.useSubjectCredsOnly");

				if (useSubjectCredsVal == null) {
					System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
				} else if (!(useSubjectCredsVal.toLowerCase(Locale.ROOT).equals("false"))) {
					logger.warn("System Property: javax.security.auth.useSubjectCredsOnly set to: " + useSubjectCredsVal
							+ " not false.  SPNego authentication may not be successful.");
				}

				Configuration.setConfiguration(jaasConfig);

				AuthSchemeRegistry registry = new AuthSchemeRegistry();
				registry.register("Negotiate", new SPNegoSchemeFactory(true, false));
				httpClient.setAuthSchemes(registry);

				Credentials useJaasCreds = new Credentials() {
					public String getPassword() {
						return null;
					}

					public Principal getUserPrincipal() {
						return null;
					}
				};
				SolrPortAwareCookieSpecFactory cookieFactory = new SolrPortAwareCookieSpecFactory();
				httpClient.getCookieSpecs().register("solr-portaware", cookieFactory);
				httpClient.getParams().setParameter("http.protocol.cookie-policy", "solr-portaware");

				httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, useJaasCreds);

				httpClient.addRequestInterceptor(this.bufferedEntityInterceptor);
			} else {
				httpClient.getCredentialsProvider().clear();
			}
		}
	}

	public void configure(DefaultHttpClient httpClient, SolrParams config) {
		super.configure(httpClient, config);

		// if (true || System.getProperty("java.security.auth.login.config") !=
		// null) {
		if (true || System.getProperty("java.security.auth.login.config") != null) {
			String configValue = System.getProperty("java.security.auth.login.config");

			configValue = "dummyvalue";

			if (configValue != null) {
				logger.info("Setting up SPNego auth with config: " + configValue);
				String useSubjectCredsProp = "javax.security.auth.useSubjectCredsOnly";
				String useSubjectCredsVal = System.getProperty("javax.security.auth.useSubjectCredsOnly");

				if (useSubjectCredsVal == null) {
					System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
				} else if (!(useSubjectCredsVal.toLowerCase(Locale.ROOT).equals("false"))) {
					logger.warn("System Property: javax.security.auth.useSubjectCredsOnly set to: " + useSubjectCredsVal
							+ " not false.  SPNego authentication may not be successful.");
				}

				Configuration.setConfiguration(jaasConfig);

				AuthSchemeRegistry registry = new AuthSchemeRegistry();
				registry.register("Negotiate", new SPNegoSchemeFactory(true, false));
				httpClient.setAuthSchemes(registry);

				Credentials useJaasCreds = new Credentials() {
					public String getPassword() {
						return null;
					}

					public Principal getUserPrincipal() {
						return null;
					}
				};
				SolrPortAwareCookieSpecFactory cookieFactory = new SolrPortAwareCookieSpecFactory();
				httpClient.getCookieSpecs().register("solr-portaware", cookieFactory);
				httpClient.getParams().setParameter("http.protocol.cookie-policy", "solr-portaware");

				httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, useJaasCreds);

				httpClient.addRequestInterceptor(this.bufferedEntityInterceptor);
			} else {
				httpClient.getCredentialsProvider().clear();
			}
		}
	}

	private static class SolrJaasConfiguration extends Configuration {
		private Configuration baseConfig;
		private String clienName = "ClientSolrJ_1";

		private Set<String> initiateAppNames = new HashSet(Arrays.asList(new String[] { "com.sun.security.jgss.krb5.initiate", "com.sun.security.jgss.initiate" }));

		public SolrJaasConfiguration() {
			try {
				this.baseConfig = Configuration.getConfiguration();
			} catch (SecurityException e) {
				this.baseConfig = null;
			}
		}

		public SolrJaasConfiguration(String clientName) {
			this.clienName = clientName;
			try {
				this.baseConfig = Configuration.getConfiguration();
			} catch (SecurityException e) {
				this.baseConfig = null;
			}
		}

		public AppConfigurationEntry[] getAppConfigurationEntry(String appName) {
			if (this.baseConfig == null)
				return null;

			Krb5HttpClientConfigurer.logger.debug("Login prop: " + System.getProperty("java.security.auth.login.config"));

			// String clientAppName = System.getProperty(
			// "solr.kerberos.jaas.appname", "Client");

			String clientAppName = this.clienName;

			if (true || this.initiateAppNames.contains(appName)) {
				Krb5HttpClientConfigurer.logger.debug("Using AppConfigurationEntry for appName '" + clientAppName + "' instead of: " + appName);
				return this.baseConfig.getAppConfigurationEntry(clientAppName);
			}
			return this.baseConfig.getAppConfigurationEntry(appName);
		}
	}
}