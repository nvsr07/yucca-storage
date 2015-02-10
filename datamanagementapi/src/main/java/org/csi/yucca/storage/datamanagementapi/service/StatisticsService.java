package org.csi.yucca.storage.datamanagementapi.service;


import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/statistics")
public class StatisticsService {

	//private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	//Map<String,String> mongoParams = null;

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(StatisticsService.class);

	@GET
	@Path("/getLatest")
	@Produces(MediaType.APPLICATION_JSON)
	public String createTenant() throws UnknownHostException {

		//mongoParams = ConfigParamsSingleton.getInstance().getParams();
		mongo =  MongoSingleton.getMongoClient();

		DBObject statistics = null;
		DB db = mongo.getDB(Config.getInstance().getDbSupport());
		DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportStatistics());
			
		DBObject sortOb = new BasicDBObject("_id",-1);
		DBCursor list = col.find().sort(sortOb).limit(1);
		
		if(list.hasNext()){
			statistics = list.next();
		}

		return JSON.serialize(statistics);
	}
}
