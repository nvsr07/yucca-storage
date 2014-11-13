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
import org.csi.yucca.storage.datamanagementapi.util.APIFiller;
import org.csi.yucca.storage.datamanagementapi.util.ConfigParamsSingleton;
import org.csi.yucca.storage.datamanagementapi.util.MetadataFiller;
import org.csi.yucca.storage.datamanagementapi.util.StreamFiller;

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

@Path("/metadata")
public class InstallCepService {

	private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	Map<String,String> mongoParams = null;
	MongoCredential credential=null;

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(InstallCepService.class);

	@POST
	@Path("/insertFromStream")
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(final String datasetInput) throws UnknownHostException {
		log.debug("[MetadataService::createMetadataService] - START");

		mongoParams = ConfigParamsSingleton.getInstance().getParams();

		credential = MongoCredential.createMongoCRCredential(mongoParams.get("MONGO_USERNAME"), mongoParams.get("MONGO_DB_AUTH"), mongoParams.get("MONGO_PASSWORD").toCharArray());


		ServerAddress serverAdd = new ServerAddress(mongoParams.get("MONGO_HOST"), Integer.parseInt(mongoParams.get("MONGO_PORT")));

		if("true".equals(mongoParams.get("MONGO_DB_AUTH_FLAG")))
			mongo = new MongoClient(serverAdd,Arrays.asList(credential));
		else
			mongo = new MongoClient(serverAdd);


		Gson gson = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.serializeNulls()
		.create();

		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try{
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if(pojoStreams != null && pojoStreams.getStreams()!= null && pojoStreams.getStreams().getStream()!= null){

				DB db = mongo.getDB(mongoParams.get("MONGO_DB_META"));
				DBCollection col = db.getCollection(mongoParams.get("MONGO_COLLECTION_DATASET"));
				Metadata myMeta= 	MetadataFiller.fillMetadata(pojoStreams.getStreams().getStream());

				//				DBCollection col = db.getCollection("metadata");
				DBObject dbObject = (DBObject)JSON.parse(gson.toJson(myMeta, Metadata.class));
				Long idDataset =insertDocumentWithKey(col,dbObject,"idDataset",MAX_RETRY);


				MyApi api = APIFiller.fillApi(pojoStreams.getStreams().getStream(),idDataset);
				StreamOut strOut = StreamFiller.fillStream(pojoStreams.getStreams().getStream(),idDataset);

				System.out.println(gson.toJson(api, MyApi.class));
				System.out.println(gson.toJson(strOut, StreamOut.class));
				System.out.println(gson.toJson(myMeta, Metadata.class));


				//stream gets the idStream from the Json
				col = db.getCollection(mongoParams.get("MONGO_COLLECTION_STREAM"));
				dbObject = (DBObject)JSON.parse(gson.toJson(strOut, StreamOut.class));
				col.insert(dbObject);


				col = db.getCollection(mongoParams.get("MONGO_COLLECTION_API"));
				dbObject = (DBObject)JSON.parse(gson.toJson(api, MyApi.class));
				insertDocumentWithKey(col,dbObject,"idApi",MAX_RETRY);
				//				col.insert(dbObject);

			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}

		return JSON.parse("{OK:1}").toString();
	}

	private static Long insertDocumentWithKey(DBCollection col,DBObject obj,String key,Integer maxRetry) throws Exception{
		Long id =0L;
		try{
			BasicDBObject sortobj = new BasicDBObject();
			sortobj.append(key, -1);			
			DBObject doc = col.find().sort(sortobj).limit(1).one();
			System.out.println(doc);
			id = ((Number)doc.get(key)).longValue() +1;
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
