package org.csi.yucca.storage.datamanagementapi.singleton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class Config {

	public static final String MONGO_HOST = "MONGO_HOST";
	public static final String MONGO_PORT = "MONGO_PORT";
	public static final String MONGO_DB_SUPPORT = "MONGO_DB_SUPPORT";
	public static final String MONGO_DB_AUTH_FLAG = "MONGO_DB_AUTH_FLAG";
	public static final String MONGO_DB_AUTH = "MONGO_DB_AUTH";
	public static final String MONGO_COLLECTION_SUPPORT_DATASET = "MONGO_COLLECTION_SUPPORT_DATASET";
	public static final String MONGO_COLLECTION_SUPPORT_API = "MONGO_COLLECTION_SUPPORT_API";
	public static final String MONGO_COLLECTION_SUPPORT_STREAM = "MONGO_COLLECTION_SUPPORT_STREAM";
	public static final String MONGO_COLLECTION_SUPPORT_TENANT = "MONGO_COLLECTION_SUPPORT_TENANT";
	public static final String MONGO_COLLECTION_SUPPORT_STATISTICS = "MONGO_COLLECTION_SUPPORT_STATISTICS";
	public static final String MONGO_COLLECTION_SUPPORT_STREAM_STATS = "MONGO_COLLECTION_SUPPORT_STREAM_STATS";
	public static final String MONGO_USERNAME = "MONGO_USERNAME";
	public static final String MONGO_PASSWORD = "MONGO_PASSWORD";
	public static final String MONGO_COLLECTION_TENANT_DATA = "MONGO_COLLECTION_TENANT_DATA";
	public static final String MONGO_COLLECTION_TENANT_SOCIAL = "MONGO_COLLECTION_TENANT_SOCIAL";
	public static final String MONGO_COLLECTION_TENANT_MEASURES = "MONGO_COLLECTION_TENANT_MEASURES";
	public static final String BASE_API_URL = "BASE_API_URL";
	public static final String DAMMI_INFO = "DAMMI_INFO";
	public static final String STORE_USERNAME = "STORE_USERNAME";
	public static final String STORE_PASSWORD = "STORE_PASSWORD";
	public static final String CONSOLE_ADDRESS = "CONSOLE_ADDRESS";
	public static final String HTTP_OK = "HTTP_OK";
	public static final String RESPONSE_OK = "RESPONSE_OK";
	public static final String STORE_API_ADDRESS = "STORE_API_ADDRESS";
	public static final String PUBLISHER_BASE_URL = "PUBLISHER_BASE_URL";
	public static final String USERPORTAL_BASE_URL = "USERPORTAL_BASE_URL";
	public static final String STORE_BASE_URL = "STORE_BASE_URL";
	public static final String BASE_EXPOSED_API_URL = "BASE_EXPOSED_API_URL";
	public static final String API_ADMIN_SERVICES_URL = "API_ADMIN_SERVICES_URL";
	public static final String KNOX_SDNET_URL = "KNOX_SDNET_URL";
	public static final String KNOX_USER = "KNOX_USER";
	public static final String KNOX_PWD = "KNOX_PWD";
	public static final String MAIL_FROM = "MAIL_FROM";
	public static final String MAIL_TO = "MAIL_TO";
	public static final String MAIL_SERVER = "MAIL_SERVER";
	public static final String MAIL_SUBJECT_404 = "MAIL_SUBJECT_404";
	public static final String MAIL_BODY_404 = "MAIL_BODY_404";
	public static final String MAIL_SUBJECT_500 = "MAIL_SUBJECT_500";
	public static final String MAIL_BODY_500 = "MAIL_BODY_500";
	public static final String MAIL_SUBJECT_200 = "MAIL_SUBJECT_200";
	public static final String MAIL_BODY_200 = "MAIL_BODY_200";
	
	public static final String SDP_SOLR_URL = "SDP_SOLR_URL";
	public static final String SDP_SOLR_COLLECTION = "SDP_SOLR_COLLECTION";
	
	public static final String SOLR_PASSWORD = "SOLR_PASSWORD";
	public static final String SOLR_USERNAME = "SOLR_USERNAME";
	public static final String SOLR_TYPE_ACCESS = "SOLR_TYPE_ACCESS";
	
	public static final String SOLR_SECURITY_DOMAIN_NAME = "SOLR_SECURITY_DOMAIN_NAME";
	
	
	

	public static final String DATA_INSERT_BASE_URL = "DATA_INSERT_BASE_URL";
	public static final String DATA_DELETE_BASE_URL = "DATA_DELETE_BASE_URL";
	
	private static Map<String, String> params = null;
	private static Config instance = null;
	static Logger log = Logger.getLogger(Config.class);

	private Config() {

		params = new HashMap<String, String>();
		ResourceBundle rb = ResourceBundle.getBundle("SDPDataApiConfig");
		params.put(MONGO_HOST, rb.getString(MONGO_HOST));
		params.put(MONGO_PORT, rb.getString(MONGO_PORT));
		params.put(MONGO_DB_SUPPORT, rb.getString(MONGO_DB_SUPPORT));
		params.put(MONGO_COLLECTION_SUPPORT_DATASET, rb.getString(MONGO_COLLECTION_SUPPORT_DATASET));
		params.put(MONGO_COLLECTION_SUPPORT_API, rb.getString(MONGO_COLLECTION_SUPPORT_API));
		params.put(MONGO_COLLECTION_SUPPORT_STREAM, rb.getString(MONGO_COLLECTION_SUPPORT_STREAM));
		params.put(MONGO_USERNAME, rb.getString(MONGO_USERNAME));
		params.put(MONGO_DB_AUTH, rb.getString(MONGO_DB_AUTH));
		params.put(MONGO_DB_AUTH_FLAG, rb.getString(MONGO_DB_AUTH_FLAG));
		params.put(MONGO_COLLECTION_SUPPORT_TENANT, rb.getString(MONGO_COLLECTION_SUPPORT_TENANT));
		params.put(MONGO_COLLECTION_SUPPORT_STATISTICS, rb.getString(MONGO_COLLECTION_SUPPORT_STATISTICS));
		params.put(MONGO_COLLECTION_SUPPORT_STREAM_STATS, rb.getString(MONGO_COLLECTION_SUPPORT_STREAM_STATS));
		params.put(BASE_API_URL, rb.getString(BASE_API_URL));
		params.put(MONGO_COLLECTION_TENANT_DATA, rb.getString(MONGO_COLLECTION_TENANT_DATA));
		params.put(MONGO_COLLECTION_TENANT_SOCIAL, rb.getString(MONGO_COLLECTION_TENANT_SOCIAL));
		params.put(MONGO_COLLECTION_TENANT_MEASURES, rb.getString(MONGO_COLLECTION_TENANT_MEASURES));
		params.put(DAMMI_INFO, rb.getString(DAMMI_INFO));
		params.put(CONSOLE_ADDRESS, rb.getString(CONSOLE_ADDRESS));
		params.put(HTTP_OK, rb.getString(HTTP_OK));
		params.put(RESPONSE_OK, rb.getString(RESPONSE_OK));
		params.put(STORE_API_ADDRESS, rb.getString(STORE_API_ADDRESS));
		params.put(STORE_BASE_URL, rb.getString(STORE_BASE_URL));
		params.put(PUBLISHER_BASE_URL, rb.getString(PUBLISHER_BASE_URL));
		params.put(USERPORTAL_BASE_URL, rb.getString(USERPORTAL_BASE_URL));
		params.put(BASE_EXPOSED_API_URL, rb.getString(BASE_EXPOSED_API_URL));
		params.put(API_ADMIN_SERVICES_URL, rb.getString(API_ADMIN_SERVICES_URL));
		params.put(KNOX_SDNET_URL, rb.getString(KNOX_SDNET_URL));
		params.put(MAIL_FROM, rb.getString(MAIL_FROM));
		params.put(MAIL_TO, rb.getString(MAIL_TO));
		params.put(MAIL_SERVER, rb.getString(MAIL_SERVER));
		params.put(MAIL_SUBJECT_404, rb.getString(MAIL_SUBJECT_404));
		params.put(MAIL_BODY_404, rb.getString(MAIL_BODY_404));
		params.put(MAIL_SUBJECT_500, rb.getString(MAIL_SUBJECT_500));
		params.put(MAIL_BODY_500, rb.getString(MAIL_BODY_500));
		params.put(MAIL_SUBJECT_200, rb.getString(MAIL_SUBJECT_200));
		params.put(MAIL_BODY_200, rb.getString(MAIL_BODY_200));
		params.put(DATA_INSERT_BASE_URL, rb.getString(DATA_INSERT_BASE_URL));
		params.put(DATA_DELETE_BASE_URL, rb.getString(DATA_DELETE_BASE_URL));
		params.put(SDP_SOLR_URL, rb.getString(SDP_SOLR_URL));
		params.put(SDP_SOLR_COLLECTION, rb.getString(SDP_SOLR_COLLECTION));
		
		params.put(SOLR_USERNAME, rb.getString(SOLR_USERNAME));
		params.put(SOLR_TYPE_ACCESS, rb.getString(SOLR_TYPE_ACCESS));
		params.put(SOLR_SECURITY_DOMAIN_NAME, rb.getString(SOLR_SECURITY_DOMAIN_NAME));


		
		
		ResourceBundle rbSecret = ResourceBundle.getBundle("SDPDataApiSecret");
		params.put(MONGO_PASSWORD, rbSecret.getString(MONGO_PASSWORD));
		params.put(STORE_USERNAME, rbSecret.getString(STORE_USERNAME));
		params.put(STORE_PASSWORD, rbSecret.getString(STORE_PASSWORD));
		params.put(KNOX_USER, rbSecret.getString(KNOX_USER));
		params.put(KNOX_PWD, rbSecret.getString(KNOX_PWD));
		params.put(SOLR_PASSWORD, rbSecret.getString(SOLR_PASSWORD));
	}

	
	
	
	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	
	
	
	
	public String getSolrSecurityDomainName() {
		return (params.get(SOLR_SECURITY_DOMAIN_NAME) != null ? params.get(SOLR_SECURITY_DOMAIN_NAME) : "");
	}
	public String getSolrTypeAccess() {
		return (params.get("SOLR_TYPE_ACCESS") != null ? params.get("SOLR_TYPE_ACCESS") : "");
	}
	
	public String getSolrUsername(){
		return (params.get("SOLR_USERNAME") != null ? params.get("SOLR_USERNAME") : "");
		
	}
	
	public String getSolrPassword() {
		return (params.get("SOLR_PASSWORD") != null ? params.get("SOLR_PASSWORD") : "");
		
	}
	
	
	
	
	
	
	
	public String getSolrUrl() {
		return params.get(SDP_SOLR_URL);
	}	
	public String getSolrCollection() {
		return params.get(SDP_SOLR_COLLECTION);
	}	
	
	
	
	public String[] getMongoHost() {
		return params.get(MONGO_HOST).split(";");
	}

	public String[] getMongoPort() {
		return params.get(MONGO_PORT).split(";");
	}

	public String getDbSupport() {
		return params.get(MONGO_DB_SUPPORT);
	}

	public String getDbAuthFlag() {
		return params.get(MONGO_DB_AUTH_FLAG);
	}

	public String getDbAuth() {
		return params.get(MONGO_DB_AUTH);
	}

	public String getCollectionSupportDataset() {
		return params.get(MONGO_COLLECTION_SUPPORT_DATASET);
	}

	public String getCollectionSupportApi() {
		return params.get(MONGO_COLLECTION_SUPPORT_API);
	}

	public String getCollectionSupportStream() {
		return params.get(MONGO_COLLECTION_SUPPORT_STREAM);
	}

	public String getCollectionSupportTenant() {
		return params.get(MONGO_COLLECTION_SUPPORT_TENANT);
	}

	public String getCollectionSupportStatistics() {
		return params.get(MONGO_COLLECTION_SUPPORT_STATISTICS);
	}

	public String getCollectionSupportStreamStats() {
		return params.get(MONGO_COLLECTION_SUPPORT_STREAM_STATS);
	}

	public String getCollectionTenantData() {
		return params.get(MONGO_COLLECTION_TENANT_DATA);
	}

	public String getCollectionTenantSocial() {
		return params.get(MONGO_COLLECTION_TENANT_SOCIAL);
	}

	public String getCollectionTenantMeasures() {
		return params.get(MONGO_COLLECTION_TENANT_MEASURES);
	}

	public String getMongoUsername() {
		return params.get(MONGO_USERNAME);
	}

	public String getMongoPassword() {
		return params.get(MONGO_PASSWORD);
	}

	public String getBaseApiUrl() {
		return params.get(BASE_API_URL);
	}

	public String getDammiInfo() {
		return params.get(DAMMI_INFO);
	}

	public String getStoreUsername() {
		return params.get(STORE_USERNAME);
	}

	public String getStorePassword() {
		return params.get(STORE_PASSWORD);
	}

	public String getConsoleAddress() {
		return params.get(CONSOLE_ADDRESS);
	}

	public String getHttpOk() {
		return params.get(HTTP_OK);
	}

	public String getResponseOk() {
		return params.get(RESPONSE_OK);
	}

	public String getStoreApiAddress() {
		return params.get(STORE_API_ADDRESS);
	}

	public String getStoreBaseUrl() {
		return params.get(STORE_BASE_URL);
	}

	public String getPublisherBaseUrl() {
		return params.get(PUBLISHER_BASE_URL);
	}

	public String getUserportalBaseUrl() {
		return params.get(USERPORTAL_BASE_URL);
	}

	public String getBaseExposedApiUrl() {
		return params.get(BASE_EXPOSED_API_URL);
	}

	public String getDataInsertBaseUrl() {
		return params.get(DATA_INSERT_BASE_URL);
	}
	
	public String getDataDeleteBaseUrl() {
		return params.get(DATA_DELETE_BASE_URL);
	}

	public String getApiAdminServiceUrl() {
		return params.get(API_ADMIN_SERVICES_URL);
	}

	public String getApiKnoxSDNETUrl() {
		return params.get(KNOX_SDNET_URL);
	}

	public String getKnoxSDNETUser() {
		return params.get(KNOX_USER);
	}

	public String getKnoxSDNETPwd() {
		return params.get(KNOX_PWD);
	}

	public String getMailFrom() {
		return params.get(MAIL_FROM);
	}

	public String getMailTo() {
		return params.get(MAIL_TO);
	}

	public String getMailServer() {
		return params.get(MAIL_SERVER);
	}

	public static String getMailSubject404() {
		return params.get(MAIL_SUBJECT_404);
	}

	public static String getMailSubject500() {
		return params.get(MAIL_SUBJECT_500);
	}

	public static String getMailSubject200() {
		return params.get(MAIL_SUBJECT_200);
	}

	public static String getMailBody404() {
		return params.get(MAIL_BODY_404);
	}

	public static String getMailBody500() {
		return params.get(MAIL_BODY_500);
	}

	public static String getMailBody200() {
		return params.get(MAIL_BODY_200);
	}

	public static Properties loadClientConfiguration() throws IOException {
		return loadConfiguration("client.properties");
	}

	public static Properties loadServerConfiguration() throws IOException {
		return loadConfiguration("server.properties");
	}

	public static Properties loadAuthorizationConfiguration() throws IOException {
		return loadConfiguration("authorization.properties");
	}

	private static Properties loadConfiguration(String configPath) throws IOException {
		log.debug("[Config::loadConfiguration] - START, configPath " + configPath);
		try {
			Properties config = new Properties();
			config.load(Config.class.getClassLoader().getResourceAsStream(configPath));
			return config;
		} finally {
			log.debug("[Config::loadConfiguration] - END, configPath " + configPath);
		}
	}
}
