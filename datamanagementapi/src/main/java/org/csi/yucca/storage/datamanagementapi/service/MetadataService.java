package org.csi.yucca.storage.datamanagementapi.service;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;

@Path("/metadata")
public class MetadataService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(MetadataService.class);

	@GET
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll(@PathParam("tenant") String tenant) {
		log.debug("[MetadataService::getAll] - START");
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, "smartlab", "provaAle");

		String result = "[]";
		List<Metadata> allDataset = metadataDAO.readAllMetadata(tenant);
		if (allDataset != null) {
			Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
			result = gson.toJson(allDataset);
		}
		return result;
	}

	@GET
	@Path("/{tenant}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("tenant") String tenant, @PathParam("id") String id) {
		// select
		log.debug("[MetadataService::get] - START - id: " + id);
		System.out.println("DatasetItem requested with id=" + id);
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, "smartlab", "provaAle");

		Metadata metadata = new Metadata();
		metadata.setId(id);
		metadata = metadataDAO.readMetadata(metadata);

		return metadata.toJson();
	}

	@POST
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createMetadata(@PathParam("tenant") String tenant, final String datasetInput) {
		log.debug("[MetadataService::createMetadata] - START");

		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, "smartlab", "provaAle");

		Metadata metadata = Metadata.fromJson(datasetInput);
		Metadata metadataCreated = metadataDAO.createMetadata(metadata);
		return metadataCreated.toJson();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{tenant}/{id}")
	public String updateMetadata(@PathParam("tenant") String tenant, @PathParam("id") String id, final String metadataInput) {
		log.debug("[MetadataService::updateMetadata] - START");
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, "smartlab", "provaAle");

		Metadata newMetadata = Metadata.fromJson(metadataInput);
		newMetadata.setId(id);

		metadataDAO.updateDataset(newMetadata);

		return metadataDAO.readMetadata(newMetadata).toJson();
	}
}
