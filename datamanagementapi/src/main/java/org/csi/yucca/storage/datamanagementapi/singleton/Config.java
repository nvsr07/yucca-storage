package org.csi.yucca.storage.datamanagementapi.singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
	

	private static Map<String, String> params = null;
	private static Config instance = null;

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
		
		
		ResourceBundle rbSecret = ResourceBundle.getBundle("SDPDataApiSecret");
		params.put(MONGO_PASSWORD, rbSecret.getString(MONGO_PASSWORD));
		params.put(STORE_USERNAME, rbSecret.getString(STORE_USERNAME));
		params.put(STORE_PASSWORD, rbSecret.getString(STORE_PASSWORD));
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
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
	

}
