package org.csi.yucca.storage.datamanagementapi.service;


import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.LoginOut;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.util.APIFiller;
import org.csi.yucca.storage.datamanagementapi.util.MetadataFiller;
import org.csi.yucca.storage.datamanagementapi.util.StreamFiller;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/metadata")
public class InstallCepService {

	private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	//	Map<String,String> mongoParams = null;
	//	MongoCredential credential=null;

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(InstallCepService.class);

	@POST
	@Path("/insertFromStream")
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(final String datasetInput) throws UnknownHostException {
		//		log.debug("[MetadataService::createMetadataService] - START");
		//
		//		mongoParams = ConfigParamsSingleton.getInstance().getParams();
		//
		//		credential = MongoCredential.createMongoCRCredential(mongoParams.get("MONGO_USERNAME"), mongoParams.get("MONGO_DB_AUTH"), mongoParams.get("MONGO_PASSWORD").toCharArray());
		//
		//
		//		ServerAddress serverAdd = new ServerAddress(mongoParams.get("MONGO_HOST"), Integer.parseInt(mongoParams.get("MONGO_PORT")));
		//
		//		if("true".equals(mongoParams.get("MONGO_DB_AUTH_FLAG")))
		//			mongo = new MongoClient(serverAdd,Arrays.asList(credential));
		//		else

		
		mongo = MongoSingleton.getMongoClient();


		Gson gson = JSonHelper.getInstance();

		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try{
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if(pojoStreams != null && pojoStreams.getStreams()!= null && pojoStreams.getStreams().getStream()!= null){

				Stream newStream = pojoStreams.getStreams().getStream();
				DB db = mongo.getDB(Config.getInstance().getDbSupport());

				/*
				 * Update of the streams
				 * 
				 */

				DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportStream());


				DBObject findStream = new BasicDBObject();
				DBObject sortStream = new BasicDBObject();
				findStream.put("idStream", newStream.getIdStream());
				sortStream.put("_id",-1);

				DBCursor cursor = col.find(findStream).sort(sortStream);

				Long idDataset = null;
				DBObject oldStream =null;
				if(cursor.hasNext()){
					oldStream = cursor.next();

					col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
					DBObject configData = (DBObject) oldStream.get("configData");
					idDataset = ((Number)configData.get("idDataset")).longValue();

					BasicDBObject findDatasets = new BasicDBObject();

					findDatasets.put("idDataset",idDataset);

					DBObject updateConfig = new BasicDBObject();
					
					updateConfig.put("$set",new BasicDBObject("configData.current", 0));
					
					col.update(findDatasets, updateConfig,false,true);		

				}
				col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
				Metadata myMeta= 	MetadataFiller.fillMetadata(newStream);


				//myMeta get persisted on db and returns the object with the id updated
				new MongoDBMetadataDAO(mongo,Config.getInstance().getDbSupport(),Config.getInstance().getCollectionSupportDataset()).createMetadata(myMeta,idDataset);

				// Insert Api only for new streams not for updates
				if(oldStream==null){
					MyApi api = APIFiller.fillApi(newStream,myMeta);
					System.out.println(gson.toJson(api, MyApi.class));
					col = db.getCollection(Config.getInstance().getCollectionSupportApi());
					DBObject apiObject = (DBObject)JSON.parse(gson.toJson(api, MyApi.class));
					apiObject.removeField("id");
					insertDocumentWithKey(col,apiObject,"idApi",MAX_RETRY);
				}
				
				StreamOut strOut = StreamFiller.fillStream(newStream,myMeta.getIdDataset());
				System.out.println(gson.toJson(strOut, StreamOut.class));
				System.out.println(gson.toJson(myMeta, Metadata.class));


				//stream gets the idStream from the Json
				col = db.getCollection(Config.getInstance().getCollectionSupportStream());
				DBObject dbObject = (DBObject)JSON.parse(gson.toJson(strOut, StreamOut.class));
				
				DBObject uniqueStream = new BasicDBObject("idStream",strOut.getIdStream());
				uniqueStream.put("streams.stream.deploymentVersion", strOut.getStreams().getStream().getDeploymentVersion());
				
				
				dbObject.removeField("id");
				// if the stream with that id and version exists .. update it, otherwise insert the new one.
				//upsert:true  multi:false 
				col.update(uniqueStream,dbObject,true,false);
			}
			
			/*
			 * Aggiungi Stream allo store
			 */
			LoginOut logout = new LoginOut();
			logout.test();
			
			
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
			if(doc != null && doc.get(key)!=null)
				id = ((Number)doc.get(key)).longValue() +1;
			else{
				id=1L;
			}
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
	//	private static Long insertDatasetWithKey(DBCollection col,DBObject obj,String key,String datasetCode,Integer maxRetry) throws Exception{
	//		Long id =0L;
	//		try{
	//			BasicDBObject sortobj = new BasicDBObject();
	//			sortobj.append(key, -1);			
	//			DBObject doc = col.find().sort(sortobj).limit(1).one();
	//			System.out.println(doc);
	//			id = ((Number)doc.get(key)).longValue() +1;
	//			obj.put(key, id);
	//			obj.put("datasetCode", datasetCode+id);
	//			col.insert(obj);
	//		}catch(Exception e){
	//			if(maxRetry>0){
	//				return insertDatasetWithKey(col, obj,key,datasetCode,--maxRetry);
	//			}else{
	//				throw e;
	//			}
	//		}
	//		return id;
	//	}
}
