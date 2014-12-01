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
	public static final String MONGO_USERNAME = "MONGO_USERNAME";
	public static final String MONGO_PASSWORD = "MONGO_PASSWORD";
	public static final String BASE_API_URL = "BASE_API_URL";

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
		params.put(BASE_API_URL, rb.getString(BASE_API_URL));
		ResourceBundle rbSecret = ResourceBundle.getBundle("SDPDataApiSecret");
		params.put(MONGO_PASSWORD, rbSecret.getString(MONGO_PASSWORD));
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	public String getMongoHost() {
		return params.get(MONGO_HOST);
	}

	public String getMongoPort() {
		return params.get(MONGO_PORT);
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

	public String getMongoUsername() {
		return params.get(MONGO_USERNAME);
	}

	public String getMongoPassword() {
		return params.get(MONGO_PASSWORD);
	}

	public String getBaseApiUrl() {
		return params.get(BASE_API_URL);
	}

}
