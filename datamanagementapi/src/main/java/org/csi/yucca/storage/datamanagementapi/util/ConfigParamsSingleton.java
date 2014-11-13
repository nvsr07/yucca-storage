package org.csi.yucca.storage.datamanagementapi.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfigParamsSingleton {

	private static Map<String, String> params =null;
	private static ConfigParamsSingleton instance = null;
	private ConfigParamsSingleton(){
		
		params = new HashMap<String, String>();
		ResourceBundle rb= ResourceBundle.getBundle("SDPDataApiConfig");
		params.put("MONGO_HOST", rb.getString("MONGO_HOST"));
		params.put("MONGO_PORT", rb.getString("MONGO_PORT"));
		params.put("MONGO_DB_META", rb.getString("MONGO_DB_META"));
		params.put("MONGO_COLLECTION_DATASET", rb.getString("MONGO_COLLECTION_DATASET"));
		params.put("MONGO_COLLECTION_API", rb.getString("MONGO_COLLECTION_API"));
		params.put("MONGO_COLLECTION_STREAM", rb.getString("MONGO_COLLECTION_STREAM"));
		params.put("MONGO_API_ADDRESS", rb.getString("MONGO_API_ADDRESS"));
		params.put("MONGO_STREAM_TOPIC", rb.getString("MONGO_STREAM_TOPIC"));
		params.put("MONGO_USERNAME", rb.getString("MONGO_USERNAME"));
		params.put("MONGO_PASSWORD", rb.getString("MONGO_PASSWORD"));
		params.put("MONGO_DB_AUTH", rb.getString("MONGO_DB_AUTH"));
		params.put("MONGO_DB_AUTH_FLAG", rb.getString("MONGO_DB_AUTH_FLAG"));
		
	}
	
	public static ConfigParamsSingleton getInstance(){
		
		if(instance==null){
			instance = new ConfigParamsSingleton();
		}
		
		return instance;
	}
	
	public Map<String,String> getParams(){
			return params;			
	}
	
}
