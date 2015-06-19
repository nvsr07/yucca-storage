package org.csi.yucca.storage.datamanagementapi.service;

import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/statistics")
public class StatisticsService {

	// private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	// Map<String,String> mongoParams = null;

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(StatisticsService.class);

	@GET
	@Path("/getLatest")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLatest() throws UnknownHostException {

		// mongoParams = ConfigParamsSingleton.getInstance().getParams();
		mongo = MongoSingleton.getMongoClient();

		DBObject statistics = null;
		DB db = mongo.getDB(Config.getInstance().getDbSupport());
		DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportStatistics());

		DBObject sortOb = new BasicDBObject("datetime", -1);
		DBCursor list = col.find().sort(sortOb).limit(1);

		if (list.hasNext()) {
			statistics = list.next();
		}
		return JSON.serialize(statistics);
	}

	@GET
	@Path("/getGlobalStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGlobalStatistics() throws UnknownHostException {
		return loadGlobalStatistic(null, null, null);
	}

	@GET
	@Path("/getGlobalStatistics/{year}/{month}/{day}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGlobalStatistics(@PathParam("year") Integer year, @PathParam("month") Integer month, @PathParam("day") Integer day)
			throws UnknownHostException {

		BasicDBObject yearElement = new BasicDBObject( "date.year", new BasicDBObject("$lte",year ));
		BasicDBObject monthElement = new BasicDBObject( "date.month", new BasicDBObject("$lte",month ));
		BasicDBObject dayElement = new BasicDBObject( "date.day", new BasicDBObject("$lte",day ));
		return loadGlobalStatistic(yearElement, monthElement, dayElement);
	}

	private String loadGlobalStatistic(BasicDBObject year, BasicDBObject month, BasicDBObject day) throws NumberFormatException, UnknownHostException {
		mongo = MongoSingleton.getMongoClient();
		
		
		DBObject statistics = null;
		DB db = mongo.getDB(Config.getInstance().getDbSupport());
		String aa = Config.getInstance().getCollectionSupportStreamStats();
		System.out.println(aa);
		DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportStreamStats());

		BasicDBObject sortOb = new BasicDBObject("date.year",-1 ).append("date.month",-1).append("date.day",-1);		
		
		
		DBCursor list = null;
		if (year==null|| month==null || day ==null) {
			list = col.find().sort(sortOb).limit(1);
		} else {
			 BasicDBList listaQuery= new BasicDBList();
			 listaQuery.add(year);
			 listaQuery.add(month);
			 listaQuery.add(day);
			  
			 BasicDBObject query= new BasicDBObject("$and",listaQuery);
			list = col.find(query).sort(sortOb).limit(1);
		}
			
		

		if (list!=null && list.hasNext()) {
			statistics = list.next();
		}
		return JSON.serialize(statistics);

	}
	
//	
//	 basicdbobject elementoAnno= new basicdbobject( "date.year", new basicdbobject("$lte",new Integer(2015) )
//	 basicdbobject elementomese= new basicdbobject( "date.month", new basicdbobject("$lte",new Integer(6) )
//	 basicdbobject elementoGiorno= new basicdbobject( "date.day", new basicdbobject("$lte",new Integer(15) )
//	  
//	 basicDblist listaQuery= new basicDblist();
//	 listaQuery.add(elementoAnno)
//	 listaQuery.add(elementoMese)
//	 listaQuery.add(elementoGiorno)
//	  
//	 basicDbObject query= new basicDbobject("$and",listaQuery)
//
//
//	 creazione sort
//
//	  
//
//	 basicDbobject sort = new basicdbobject("date.year",-1 ).append("date.month",-1).append("date.day",-1)

}
