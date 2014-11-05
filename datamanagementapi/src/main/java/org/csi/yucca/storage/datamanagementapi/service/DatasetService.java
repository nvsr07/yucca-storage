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
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBDatasetDAO;
import org.csi.yucca.storage.datamanagementapi.model.dataset.Dataset;
import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;

@Path("/dataset")
public class DatasetService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(DatasetService.class);

	@GET
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll(@PathParam("tenant") String tenant) {
		log.debug("[DatasetService::getAll] - START");
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		String result = "[]";
		List<Dataset> allDataset = datasetDAO.readAllDataset(tenant);
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
		log.debug("[DatasetService::get] - START - id: " + id);
		System.out.println("DatasetItem requested with id=" + id);
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		Dataset dataset = new Dataset();
		dataset.setId(id);
		dataset = datasetDAO.readDataset(dataset);

		return dataset.toJson();
	}

	@POST
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(@PathParam("tenant") String tenant, final String datasetInput) {
		log.debug("[DatasetService::createDataset] - START");

		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		Dataset dataset = Dataset.fromJson(datasetInput);
		Dataset datasetCreated = datasetDAO.createDataset(dataset);
		return datasetCreated.toJson();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{tenant}/{id}")
	public String updateDataset(@PathParam("tenant") String tenant, @PathParam("id") String id, final String datasetInput) {
		log.debug("[DatasetService::createDataset] - START");
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		Dataset newDataset = Dataset.fromJson(datasetInput);
		newDataset.setId(id);

		datasetDAO.updateDataset(newDataset);

		return datasetDAO.readDataset(newDataset).toJson();
	}
}
