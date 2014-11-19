package org.csi.yucca.storage.datamanagementapi.singleton;

import java.net.UnknownHostException;
import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoSingleton {

	private static MongoClient mongo = null;
	private static MongoCredential credential = null;

	private MongoSingleton() throws NumberFormatException, UnknownHostException {

		credential = MongoCredential.createMongoCRCredential(Config.getInstance().getMongoUsername(), Config.getInstance()
				.getDbAuth(), Config.getInstance().getMongoPassword().toCharArray());

		ServerAddress serverAdd;

		serverAdd = new ServerAddress(Config.getInstance().getMongoHost(), Integer.parseInt(Config.getInstance().getMongoPort()));

		if ("true".equals(Config.getInstance().getDbAuthFlag()))
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
