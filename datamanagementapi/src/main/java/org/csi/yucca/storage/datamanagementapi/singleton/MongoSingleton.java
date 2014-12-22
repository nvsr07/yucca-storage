package org.csi.yucca.storage.datamanagementapi.singleton;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoSingleton {

	private static MongoClient mongo = null;
	private static MongoCredential credential = null;

	private MongoSingleton() throws NumberFormatException, UnknownHostException {

		credential = MongoCredential.createMongoCRCredential(Config.getInstance().getMongoUsername(), Config.getInstance()
				.getDbAuth(), Config.getInstance().getMongoPassword().toCharArray());

		List<ServerAddress> servers =new ArrayList<ServerAddress>(); 
		ServerAddress serverAdd=null;
		for(int i=0;i<Config.getInstance().getMongoHost().length;i++){
			String addr = Config.getInstance().getMongoHost()[i];
			Integer port = null;
			try{
				port= Integer.parseInt(Config.getInstance().getMongoPort()[i]);
			}catch(Exception e){
				port=27017;
			}
			serverAdd = new ServerAddress(addr,port);
			if(addr!=null && !"".equals(addr))
				servers.add(serverAdd);
		}
		
		if ("true".equals(Config.getInstance().getDbAuthFlag()))
			mongo = new MongoClient(servers, Arrays.asList(credential));
		else
			mongo = new MongoClient(servers);

	}

	public static MongoClient getMongoClient() throws NumberFormatException, UnknownHostException {
		if (mongo == null)
			new MongoSingleton();
		return mongo;
	}
}
