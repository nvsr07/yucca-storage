package org.csi.yucca.storage.datamanagementapi.service;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.util.APIFiller;
import org.csi.yucca.storage.datamanagementapi.util.MetadataFiller;
import org.csi.yucca.storage.datamanagementapi.util.StreamFiller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/metadata")
public class InstallCepService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(InstallCepService.class);

	@POST
	@Path("/insertFromStream")
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(final String datasetInput) {
		log.debug("[MetadataService::createMetadataService] - START");

//		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
//		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");
//
//		Dataset dataset = Dataset.fromJson(datasetInput);
//		Dataset datasetCreated = datasetDAO.createDataset(dataset);
//		
		Gson gson = new GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .serializeNulls()
        .create();
		
//		String json = datasetInput.replaceAll("Device", "PIPPO E COMPANIA");
//		String json = datasetInput.replaceAll("\\{.*@nil.*:.*\\}", "null"); // match @nil elements 
		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		
		POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
		if(pojoStreams != null && pojoStreams.getStreams()!= null && pojoStreams.getStreams().getStream()!= null){
			MetadataFiller.fillMetadata(pojoStreams.getStreams().getStream());
			MyApi api = APIFiller.fillApi(pojoStreams.getStreams().getStream());
			StreamOut strOut = StreamFiller.fillStream(pojoStreams.getStreams().getStream());
			
			
			System.out.println(gson.toJson(api, MyApi.class));
			System.out.println(gson.toJson(strOut, StreamOut.class));
			
		}
		
		return json;
	}
}
