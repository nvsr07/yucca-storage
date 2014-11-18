package org.csi.yucca.storage.datamanagementapi.mongoSingleton;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigSingleton {

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

	static Logger log = Logger.getLogger(ConfigSingleton.class);

	private static ConfigSingleton instance = null;
	private Properties config;

	public static ConfigSingleton getInstance() {
		if (instance == null)
			instance = new ConfigSingleton();

		return instance;
	}

	public ConfigSingleton() {
		try {
			loadConfiguration();
		} catch (IOException e) {
			log.error("[ConfigSingleton::loadConfiguration] - ERROR:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public Properties loadConfiguration() throws IOException {
		return loadConfiguration("SDPDataApiConfig.properties");
	}

	public String getConfig(String key){
		return (String) config.get(key);
	}
	private Properties loadConfiguration(String configPath) throws IOException {
		log.debug("[ConfigSingleton::loadConfiguration] - START, configPath " + configPath);
		try {
			config = new Properties();
			config.load(ConfigSingleton.class.getClassLoader().getResourceAsStream(configPath));
			return config;
		} finally {
			log.debug("[Config::loadConfiguration] - END, configPath " + configPath);
		}
	}
}
