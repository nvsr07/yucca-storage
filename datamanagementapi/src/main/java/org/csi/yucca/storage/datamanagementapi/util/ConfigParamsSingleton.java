package org.csi.yucca.storage.datamanagementapi.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfigParamsSingleton {

	public static final String MONGO_HOST = "MONGO_HOST";
	public static final String MONGO_PORT = "MONGO_PORT";
	public static final String MONGO_DB_META = "MONGO_DB_META";
	public static final String MONGO_DB_AUTH_FLAG = "MONGO_DB_AUTH_FLAG";
	public static final String MONGO_DB_AUTH = "MONGO_DB_AUTH";
	public static final String MONGO_COLLECTION_DATASET = "MONGO_COLLECTION_DATASET";
	public static final String MONGO_COLLECTION_API = "MONGO_COLLECTION_API";
	public static final String MONGO_COLLECTION_STREAM = "MONGO_COLLECTION_STREAM";
	public static final String MONGO_COLLECTION_TENANT = "MONGO_COLLECTION_TENANT";
	public static final String MONGO_USERNAME = "MONGO_USERNAME";
	public static final String MONGO_PASSWORD = "MONGO_PASSWORD";
	public static final String BASE_API_URL = "BASE_API_URL";
	
	
	private static Map<String, String> params = null;
	private static ConfigParamsSingleton instance = null;

	private ConfigParamsSingleton() {

		params = new HashMap<String, String>();
		ResourceBundle rb = ResourceBundle.getBundle("SDPDataApiConfig");
		params.put(MONGO_HOST, rb.getString(MONGO_HOST));
		params.put(MONGO_PORT, rb.getString(MONGO_PORT));
		params.put(MONGO_DB_META, rb.getString(MONGO_DB_META));
		params.put(MONGO_COLLECTION_DATASET, rb.getString(MONGO_COLLECTION_DATASET));
		params.put(MONGO_COLLECTION_API, rb.getString(MONGO_COLLECTION_API));
		params.put(MONGO_COLLECTION_STREAM, rb.getString(MONGO_COLLECTION_STREAM));
		params.put(MONGO_USERNAME, rb.getString(MONGO_USERNAME));
		params.put(MONGO_PASSWORD, rb.getString(MONGO_PASSWORD));
		params.put(MONGO_DB_AUTH, rb.getString(MONGO_DB_AUTH));
		params.put(MONGO_DB_AUTH_FLAG, rb.getString(MONGO_DB_AUTH_FLAG));
		params.put(MONGO_COLLECTION_TENANT, rb.getString(MONGO_COLLECTION_TENANT));

	}

	public static ConfigParamsSingleton getInstance() {

		if (instance == null) {
			instance = new ConfigParamsSingleton();
		}

		return instance;
	}

	public Map<String, String> getParams() {
		return params;
	}

}
