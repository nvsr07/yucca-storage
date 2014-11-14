package org.csi.yucca.storage.datamanagementapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBApiDAO;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;

@Path("/dataset")
public class MetadataService {

	public static final String IMPORT_BULKDATASET_METADATA_REQ_KEY = "dataset";
	public static final String IMPORT_BULKDATASET_ENCODING_REQ_KEY = "encoding";
	public static final String IMPORT_BULKDATASET_FORMAT_TYPE_REQ_KEY = "formatType";
	public static final String IMPORT_BULKDATASET_CSV_SEP_REQ_KEY = "csvSeparator";
	public static final String IMPORT_BULKDATASET_CSV_SKIP_FIRS_ROW_REQ_KEY = "skipFirstRow";
	public static final String IMPORT_BULKDATASET_FILE_REQ_KEY = "file";

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(MetadataService.class);

	@GET
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll(@PathParam("tenant") String tenant) {
		log.debug("[MetadataService::getAll] - START");
		MongoClient mongo = (MongoClient) context.getAttribute(MongoDBContextListener.MONGO_CLIENT);

		String supportDb = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DB);
		String supportDatasetCollection = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DATASET_COLLECTION);
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

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
		MongoClient mongo = (MongoClient) context.getAttribute(MongoDBContextListener.MONGO_CLIENT);

		String supportDb = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DB);
		String supportDatasetCollection = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DATASET_COLLECTION);
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		Metadata metadata = new Metadata();
		metadata.setId(id);
		metadata = metadataDAO.readMetadata(metadata);

		return metadata.toJson();
	}

	@POST
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createMetadata(@PathParam("tenant") String tenant, @Context HttpServletRequest request) {
		log.debug("[MetadataService::createMetadata] - START");

		String datasetMetadata = null;
		String encoding = null;
		String formatType = null;
		String csvSeparator = null;
		boolean skipFirstRow = false;
		List<String> csvData = null;

		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (IMPORT_BULKDATASET_METADATA_REQ_KEY.equals(item.getFieldName()))
					datasetMetadata = read(item.openStream());
				else if (IMPORT_BULKDATASET_ENCODING_REQ_KEY.equals(item.getFieldName()))
					encoding = read(item.openStream());
				else if (IMPORT_BULKDATASET_FORMAT_TYPE_REQ_KEY.equals(item.getFieldName()))
					formatType = read(item.openStream());
				else if (IMPORT_BULKDATASET_CSV_SEP_REQ_KEY.equals(item.getFieldName()))
					csvSeparator = read(item.openStream());
				else if (IMPORT_BULKDATASET_CSV_SKIP_FIRS_ROW_REQ_KEY.equals(item.getFieldName()))
					skipFirstRow = new Boolean(read(item.openStream()));
				else if (IMPORT_BULKDATASET_FILE_REQ_KEY.equals(item.getFieldName())) {
					csvData = readFileRows(item.openStream(), encoding);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		MongoClient mongo = (MongoClient) context.getAttribute(MongoDBContextListener.MONGO_CLIENT);

		String supportDb = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DB);
		String supportDatasetCollection = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DATASET_COLLECTION);
		
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		String supportApiCollection = (String) context.getAttribute(MongoDBContextListener.SUPPORT_API_COLLECTION);
		MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);

		Metadata metadata = Metadata.fromJson(datasetMetadata);
		if (metadata.getConfigData() == null)
			metadata.setConfigData(new ConfigData());
		metadata.getConfigData().setType(Metadata.CONFIG_DATA_TYPE_DATASET);
		metadata.getConfigData().setSubtype(Metadata.CONFIG_DATA_SUBTYPE_BULK_DATASET);
	
		Metadata metadataCreated = metadataDAO.createMetadata(metadata);

		MyApi api = MyApi.createFromMetadataDataset(metadataCreated);
		MyApi apiCreated = apiDAO.createApi(api);

		return metadataCreated.toJson();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{tenant}/{id}")
	public String updateMetadata(@PathParam("tenant") String tenant, @PathParam("id") String id, final String metadataInput) {
		log.debug("[MetadataService::updateMetadata] - START");
		MongoClient mongo = (MongoClient) context.getAttribute(MongoDBContextListener.MONGO_CLIENT);

		String supportDb = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DB);
		String supportDatasetCollection = (String) context.getAttribute(MongoDBContextListener.SUPPORT_DATASET_COLLECTION);
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		Metadata newMetadata = Metadata.fromJson(metadataInput);
		newMetadata.setId(id);

		metadataDAO.updateDataset(newMetadata);

		return metadataDAO.readMetadata(newMetadata).toJson();
	}

	@SuppressWarnings("unused")
	private int size(InputStream stream) {
		int length = 0;
		try {
			byte[] buffer = new byte[2048];
			int size;
			while ((size = stream.read(buffer)) != -1) {
				length += size;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return length;
	}

	private String read(InputStream stream) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

	private List<String> readFileRows(InputStream stream, String encoding) throws UnsupportedEncodingException {
		List<String> rows = null;
		if (encoding == null)
			encoding = "UTF-8";
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
		try {
			rows = new LinkedList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				rows.add(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return rows;
	}
}
