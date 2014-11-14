package org.csi.yucca.storage.datamanagementapi.service;


import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.model.tenantin.TenantIn;
import org.csi.yucca.storage.datamanagementapi.model.tenantout.TenantOut;
import org.csi.yucca.storage.datamanagementapi.mongoSingleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.util.APIFiller;
import org.csi.yucca.storage.datamanagementapi.util.ConfigParamsSingleton;
import org.csi.yucca.storage.datamanagementapi.util.MetadataFiller;
import org.csi.yucca.storage.datamanagementapi.util.StreamFiller;
import org.csi.yucca.storage.datamanagementapi.util.TenantFiller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

@Path("/tenant")
public class InstallTenantService {

	private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	Map<String,String> mongoParams = null;
//	MongoCredential credential=null;

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(InstallTenantService.class);

	@POST
	@Path("/insert")
	@Produces(MediaType.APPLICATION_JSON)
	public String createTenant(final String tenantInput) throws UnknownHostException {

		mongoParams = ConfigParamsSingleton.getInstance().getParams();
//		credential = MongoCredential.createMongoCRCredential( mongoParams.get("MONGO_DB_USERNAME"), mongoParams.get("MONGO_DB_AUTH"), mongoParams.get("MONGO_PASSWORD").toCharArray());
//
//
//		ServerAddress serverAdd = new ServerAddress(mongoParams.get("MONGO_HOST"), Integer.parseInt(mongoParams.get("MONGO_PORT")));
		mongo =  MongoSingleton.getMongoClient();


		Gson gson = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.serializeNulls()
		.create();

		String json = tenantInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try{
			TenantIn tenantin = gson.fromJson(json, TenantIn.class);
			if(tenantin != null && tenantin.getTenants()!= null && tenantin.getTenants().getTenant()!= null){

				DB db = mongo.getDB(mongoParams.get("MONGO_DB_META"));
				DBCollection col = db.getCollection(mongoParams.get("MONGO_COLLECTION_TENANT"));
				TenantOut myTenant= 	TenantFiller.fillTenant(tenantin.getTenants().getTenant());

				DBObject dbObject = (DBObject)JSON.parse(gson.toJson(myTenant, TenantOut.class));
				insertDocumentWithKey(col,dbObject,"idTenant",MAX_RETRY);


			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}

		return JSON.parse("{OK:1}").toString();
	}
  
	private static Integer insertDocumentWithKey(DBCollection col,DBObject obj,String key,Integer maxRetry) throws Exception{
		Integer id =0;
		try{
			BasicDBObject sortobj = new BasicDBObject();
			sortobj.append(key, -1);			
			DBObject doc = col.find().sort(sortobj).limit(1).one();
			id = ((Number)doc.get(key)).intValue() +1;
			obj.put(key, id);
			col.insert(obj);
		}catch(Exception e){
			if(maxRetry>0){
				return insertDocumentWithKey(col, obj,key,--maxRetry);
			}else{
				throw e;
			}
		}
		return id;
	}
}
