package org.csi.yucca.storage.datamanagementapi.service;

import java.util.LinkedList;
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

import com.mongodb.MongoClient;

@Path("/dataset")
public class DatasetService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(DatasetService.class);

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getAll() {
		log.debug("[DatasetService::getAll] - START");
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		List<String> result = new LinkedList<String>();
		List<Dataset> allDataset = datasetDAO.readAllDataset();
		if (allDataset != null) {
			for (Dataset dataset : allDataset) {
				result.add(dataset.toJson());
			}
		}

		return result;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("id") String id) {
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
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(final String datasetInput) {
		log.debug("[DatasetService::createDataset] - START");

		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		Dataset dataset = Dataset.fromJson(datasetInput);
		Dataset datasetCreated = datasetDAO.createDataset(dataset);
		return datasetCreated.toJson();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String updateDataset(@PathParam("id") String id, final String datasetInput) {
		log.debug("[DatasetService::createDataset] - START");
		MongoClient mongo = (MongoClient) context.getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		Dataset newDataset = Dataset.fromJson(datasetInput);
		newDataset.setId(id);
		
		datasetDAO.updateDataset(newDataset);
		
		return datasetDAO.readDataset(newDataset).toJson();
	}
}
