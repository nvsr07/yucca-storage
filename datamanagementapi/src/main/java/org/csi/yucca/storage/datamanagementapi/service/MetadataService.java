package org.csi.yucca.storage.datamanagementapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Date;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBApiDAO;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.MetadataWithExtraAttribute;
import org.csi.yucca.storage.datamanagementapi.service.response.CreateDatasetResponse;
import org.csi.yucca.storage.datamanagementapi.service.response.ErrorMessage;
import org.csi.yucca.storage.datamanagementapi.service.response.UpdateDatasetResponse;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.upload.MongoDBDataUpload;
import org.csi.yucca.storage.datamanagementapi.upload.SDPBulkInsertException;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
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
	public String getAllCurrent(@PathParam("tenant") String tenant) throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::getAll] - START");
		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		String result = "[]";
		List<Metadata> allDataset = metadataDAO.readAllMetadata(tenant, true);
		if (allDataset != null) {
			Gson gson = JSonHelper.getInstance();
			result = gson.toJson(allDataset);
		}
		return result;
	}

	@GET
	@Path("/download/{tenant}/{datasetCode}/{format}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadData(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode, @PathParam("format") String format)
			throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::downloadData] - START tenant: " + tenant + "|datasetCode: " + datasetCode + "|format: " + format);
		final char separator = ';';

		if (format == null) {
			format = "csv";
		}

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		Metadata metadata = metadataDAO.readCurrentMetadataByCode(datasetCode);

		String fileName = metadata.getInfo().getDatasetName() + "." + format;

		final List<Field> header = new LinkedList<Field>();
		for (Field field : metadata.getInfo().getFields()) {
			header.add(field);
		}

		String collectionName = Config.getInstance().getCollectionTenantData();
		if (metadata.getConfigData() != null && Metadata.CONFIG_DATA_SUBTYPE_STREAM_DATASET.equals(metadata.getConfigData().getSubtype()))
			collectionName = Config.getInstance().getCollectionTenantMeasures();

		DBCollection dataCollection = mongo.getDB("DB_" + tenant).getCollection(collectionName);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idDataset", metadata.getIdDataset());

		final DBCursor cursor = dataCollection.find(searchQuery).limit(Constants.MAX_NUM_ROW_DATA_DOWNLOAD);

		StreamingOutput stream = new StreamingOutput() {

			public void write(OutputStream os) throws IOException, WebApplicationException {

				CSVWriter writer = new CSVWriter(new OutputStreamWriter(os), separator);
				String[] headerNames = new String[header.size()];
				int counter = 0;
				for (Field field : header) {
					headerNames[counter] = field.getFieldName();
					counter++;
				}
				writer.writeNext(headerNames);
				while (cursor.hasNext()) {
					DBObject doc = cursor.next();
					String[] row = new String[header.size()];
					counter = 0;
					for (Field field : header) {
						row[counter] = "" + doc.get(field.getFieldName());
						counter++;
					}
					writer.writeNext(row);
				}

				writer.flush();
				writer.close();
			}
		};

		Response build = Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=" + fileName).build();
		return build;
	}

	@GET
	@Path("/{tenant}/{datasetCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode) throws NumberFormatException, UnknownHostException {
		// select
		log.debug("[MetadataService::get] - START - datasetCode: " + datasetCode);
		System.out.println("DatasetItem requested with datasetCode=" + datasetCode);
		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
		String supportApiCollection = Config.getInstance().getCollectionSupportApi();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		Metadata metadata = metadataDAO.readCurrentMetadataByCode(datasetCode);

		MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);

		MyApi api = apiDAO.readApiByCode(datasetCode);

		String baseApiUrl = Config.getInstance().getBaseApiUrl();

		return new MetadataWithExtraAttribute(metadata, api, baseApiUrl).toJson();
	}

	@POST
	@Path("/{tenant}")
	@Produces(MediaType.APPLICATION_JSON)
	public String createMetadata(@PathParam("tenant") String tenant, @Context HttpServletRequest request) throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::createMetadata] - START");

		String datasetMetadata = null;
		String encoding = null;
		String formatType = null;
		String csvSeparator = null;
		boolean skipFirstRow = false;
		String csvData = null;

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

		log.debug("[MetadataService::createMetadata] - encoding: " + encoding + ", formatType: " + formatType + ", csvSeparator: " + csvSeparator);
		Metadata metadata = Metadata.fromJson(datasetMetadata);
		CreateDatasetResponse createDatasetResponse = new CreateDatasetResponse();

		metadata.setDatasetVersion(1);
		if (metadata.getConfigData() == null)
			metadata.setConfigData(new ConfigData());
		metadata.getConfigData().setType(Metadata.CONFIG_DATA_TYPE_DATASET);
		metadata.getConfigData().setSubtype(Metadata.CONFIG_DATA_SUBTYPE_BULK_DATASET);
		metadata.getConfigData().setCurrent(1);
		if (metadata.getInfo() == null)
			metadata.setInfo(new Info());

		if (metadata.getInfo().getFields() != null) {
			for (Field field : metadata.getInfo().getFields()) {
				if (field != null && field.getDataType() == null)
					field.setDataType("string");
			}
		}
		metadata.getInfo().setRegistrationDate(new Date());

		MongoDBDataUpload dataUpload = new MongoDBDataUpload();
		List<SDPBulkInsertException> checkFileToWriteErrors = dataUpload.checkFileToWrite(csvData, csvSeparator, metadata, skipFirstRow);
		if (checkFileToWriteErrors != null && checkFileToWriteErrors.size() > 0) {
			for (SDPBulkInsertException error : checkFileToWriteErrors) {
				createDatasetResponse.addErrorMessage(new ErrorMessage(error.getErrorCode(), error.getErrorMessage(), error.getErrorDetail()));
			}
		} else {
			MongoClient mongo = MongoSingleton.getMongoClient();
			String supportDb = Config.getInstance().getDbSupport();
			String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

			MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

			String supportApiCollection = Config.getInstance().getCollectionSupportApi();
			MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);

			BasicDBObject searchTenantQuery = new BasicDBObject();
			searchTenantQuery.put("tenantCode", tenant);
			DBCollection tenantCollection = mongo.getDB(supportDb).getCollection("tenant");
			DBObject tenantData = tenantCollection.find(searchTenantQuery).one();
			Long idTenant = ((Number) tenantData.get("idTenant")).longValue();

			metadata.getConfigData().setIdTenant(idTenant);

			Metadata metadataCreated = metadataDAO.createMetadata(metadata);

			MyApi api = MyApi.createFromMetadataDataset(metadataCreated);
			api.getConfigData().setType(Metadata.CONFIG_DATA_TYPE_API);
			api.getConfigData().setSubtype(Metadata.CONFIG_DATA_SUBTYPE_API_MULTI_BULK);

			MyApi apiCreated = apiDAO.createApi(api);

			createDatasetResponse.setMetadata(metadataCreated);
			createDatasetResponse.setApi(apiCreated);
			try {
				dataUpload.writeFileToMongo(mongo, "DB_" + tenant, "data", metadataCreated);
			} catch (Exception e) {
				log.error("[MetadataService::createMetadata] - writeFileToMongo ERROR: " + e.getMessage());
				createDatasetResponse.addErrorMessage(new ErrorMessage(e));
				e.printStackTrace();
			}
		}

		return createDatasetResponse.toJson();
	}

	@POST
	@Path("/add/{tenant}/{datasetCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public String addDataa(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode, @Context HttpServletRequest request)
			throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::createMetadata] - START");

		String encoding = null;
		String formatType = null;
		String csvSeparator = null;
		boolean skipFirstRow = false;
		String csvData = null;

		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (IMPORT_BULKDATASET_ENCODING_REQ_KEY.equals(item.getFieldName()))
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

		log.debug("[MetadataService::createMetadata] - encoding: " + encoding + ", formatType: " + formatType + ", csvSeparator: " + csvSeparator);
		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		Metadata existingMetadata = metadataDAO.readCurrentMetadataByCode(datasetCode);
		UpdateDatasetResponse updateDatasetResponse = new UpdateDatasetResponse();

		MongoDBDataUpload dataUpload = new MongoDBDataUpload();

		List<SDPBulkInsertException> checkFileToWriteErrors = dataUpload.checkFileToWrite(csvData, csvSeparator, existingMetadata, skipFirstRow);
		if (checkFileToWriteErrors != null && checkFileToWriteErrors.size() > 0) {
			for (SDPBulkInsertException error : checkFileToWriteErrors) {
				updateDatasetResponse.addErrorMessage(new ErrorMessage(error.getErrorCode(), error.getErrorMessage(), error.getErrorDetail()));
			}
		} else {

			try {
				dataUpload.writeFileToMongo(mongo, "DB_" + tenant, "data", existingMetadata);
			} catch (Exception e) {
				log.error("[MetadataService::createMetadata] - writeFileToMongo ERROR: " + e.getMessage());
				updateDatasetResponse.addErrorMessage(new ErrorMessage(e));
				e.printStackTrace();
			}
		}

		return updateDatasetResponse.toJson();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{tenant}/{datasetCode}")
	public String updateMetadata(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode, final String metadataInput)
			throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::updateMetadata] - START");
		UpdateDatasetResponse updateDatasetResponse = new UpdateDatasetResponse();
		try {
			MongoClient mongo = MongoSingleton.getMongoClient();
			String supportDb = Config.getInstance().getDbSupport();
			String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
			MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

			Metadata inputMetadata = Metadata.fromJson(metadataInput);

			// change version: in the existing metadata set current=0;
			// new metadata created from the existing one changing only the
			// field editable (get from input) and increment version
			//
			Metadata existingMetadata = metadataDAO.readCurrentMetadataByCode(datasetCode);
			Metadata newMetadata = metadataDAO.readCurrentMetadataByCode(datasetCode);

			newMetadata.getInfo().setCopyright(inputMetadata.getInfo().getCopyright());
			newMetadata.getInfo().setDataDomain(inputMetadata.getInfo().getDataDomain());
			newMetadata.getInfo().setDescription(inputMetadata.getInfo().getDescription());
			newMetadata.getInfo().setDisclaimer(inputMetadata.getInfo().getDisclaimer());
			newMetadata.getInfo().setLicense(inputMetadata.getInfo().getLicense());
			newMetadata.getInfo().setTags(inputMetadata.getInfo().getTags());
			newMetadata.getInfo().setVisibility(inputMetadata.getInfo().getVisibility());

			int counter = 0;
			for (Field existingField : newMetadata.getInfo().getFields()) {
				existingField.setFieldAlias(inputMetadata.getInfo().getFields()[counter].getFieldAlias());
				counter++;
			}
			existingMetadata.getConfigData().setCurrent(0);
			metadataDAO.updateMetadata(existingMetadata);
			metadataDAO.createNewVersion(newMetadata);

			updateDatasetResponse.setMetadata(newMetadata);
		} catch (Exception e) {
			log.debug("[MetadataService::updateMetadata] - ERROR " + e.getMessage());
			updateDatasetResponse.addErrorMessage(new ErrorMessage(e));
			e.printStackTrace();
		}
		return updateDatasetResponse.toJson();
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

	private String readFileRows(InputStream stream, String encoding) throws UnsupportedEncodingException {
		// List<String> rows = null;
		StringBuffer rows = new StringBuffer();
		if (encoding == null)
			encoding = "UTF-8";
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
		try {
			// rows = new LinkedList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				rows.append(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return rows.toString();
	}

}
