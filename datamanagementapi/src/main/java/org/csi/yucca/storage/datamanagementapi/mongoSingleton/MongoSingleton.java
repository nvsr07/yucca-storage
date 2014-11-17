package org.csi.yucca.storage.datamanagementapi.mongoSingleton;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

import org.csi.yucca.storage.datamanagementapi.util.ConfigParamsSingleton;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoSingleton {

	private static MongoClient mongo = null;
	private static Map<String, String> mongoParams = null;
	private static MongoCredential credential = null;

	private MongoSingleton() throws NumberFormatException, UnknownHostException {
		mongoParams = ConfigParamsSingleton.getInstance().getParams();

		credential = MongoCredential.createMongoCRCredential(mongoParams.get(ConfigParamsSingleton.MONGO_USERNAME),
				mongoParams.get(ConfigParamsSingleton.MONGO_DB_AUTH), mongoParams.get(ConfigParamsSingleton.MONGO_PASSWORD).toCharArray());

		ServerAddress serverAdd;

		serverAdd = new ServerAddress(mongoParams.get(ConfigParamsSingleton.MONGO_HOST), Integer.parseInt(mongoParams.get(ConfigParamsSingleton.MONGO_PORT)));

		if ("true".equals(mongoParams.get(ConfigParamsSingleton.MONGO_DB_AUTH_FLAG)))
			mongo = new MongoClient(serverAdd, Arrays.asList(credential));
		else
			mongo = new MongoClient(serverAdd);

	}

	public static MongoClient getMongoClient() throws NumberFormatException, UnknownHostException {
		if (mongo == null)
			new MongoSingleton();
		return mongo;
	}
}
