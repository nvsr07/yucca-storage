package org.csi.yucca.storage.datamanagementapi.mongoSingleton;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigSingleton {

	static Logger log = Logger.getLogger(ConfigSingleton.class);

	private static ConfigSingleton instance = null;

	public static ConfigSingleton getConfig() {
		if (instance == null)
			instance = new ConfigSingleton();

		return instance;
	}
	
	public ConfigSingleton(){
		try {
			loadConfiguration();
		} catch (IOException e) {
			log.error("[ConfigSingleton::loadConfiguration] - ERROR:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public  Properties loadConfiguration() throws IOException {
		return loadConfiguration("SDPDataApiConfig.properties");
	}

	private  Properties loadConfiguration(String configPath) throws IOException {
		log.debug("[ConfigSingleton::loadConfiguration] - START, configPath " + configPath);
		try {
			Properties config = new Properties();
			config.load(ConfigSingleton.class.getClassLoader().getResourceAsStream(configPath));
			return config;
		} finally {
			log.debug("[Config::loadConfiguration] - END, configPath " + configPath);
		}
	}
}
