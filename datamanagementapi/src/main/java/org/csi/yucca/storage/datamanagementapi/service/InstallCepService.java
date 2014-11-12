package org.csi.yucca.storage.datamanagementapi.service;

import java.net.UnknownHostException;

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
import org.csi.yucca.storage.datamanagementapi.util.MetadataFiller;
import org.csi.yucca.storage.datamanagementapi.util.StreamFiller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/metadata")
public class InstallCepService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(InstallCepService.class);

	@POST
	@Path("/insertFromStream")
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(final String datasetInput) throws UnknownHostException {
		log.debug("[MetadataService::createMetadataService] - START");

				MongoClient mongo = new MongoClient("tst-sdnet-bgslave1.sdp.csi.it", 27017);
				DB db = mongo.getDB("PersistentProva");
				
				
				
				
//				db.authenticate("cep","mypass");
//				MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");
		
				
		Gson gson = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.serializeNulls()
		.create();

		//		String json = datasetInput.replaceAll("Device", "PIPPO E COMPANIA");
		//		String json = datasetInput.replaceAll("\\{.*@nil.*:.*\\}", "null"); // match @nil elements 
		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try{
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if(pojoStreams != null && pojoStreams.getStreams()!= null && pojoStreams.getStreams().getStream()!= null){
				
				//FIXME find real id
				Integer id = 10;
				
				Metadata myMeta= 	MetadataFiller.fillMetadata(pojoStreams.getStreams().getStream(),id);
				MyApi api = APIFiller.fillApi(pojoStreams.getStreams().getStream(),id);
				StreamOut strOut = StreamFiller.fillStream(pojoStreams.getStreams().getStream(),id);

//				System.out.println(gson.toJson(api, MyApi.class));
//				System.out.println(gson.toJson(strOut, StreamOut.class));
//				System.out.println(gson.toJson(myMeta, Metadata.class));
				
				
				DBCollection col = db.getCollection("metadata");
				DBObject dbObject = (DBObject)JSON.parse(gson.toJson(myMeta, Metadata.class));
				col.insert(dbObject);
				
				col = db.getCollection("stream");
				dbObject = (DBObject)JSON.parse(gson.toJson(strOut, StreamOut.class));
				col.insert(dbObject);
				
				
				col = db.getCollection("api");
				dbObject = (DBObject)JSON.parse(gson.toJson(api, MyApi.class));
				col.insert(dbObject);
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return "//{KO:1//}";
		}

		return "//{OK:1//}";
	}
}
