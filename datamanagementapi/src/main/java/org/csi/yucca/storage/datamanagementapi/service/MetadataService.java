package org.csi.yucca.storage.datamanagementapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBApiDAO;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBStreamDAO;
import org.csi.yucca.storage.datamanagementapi.exception.ImportDatabaseException;
import org.csi.yucca.storage.datamanagementapi.exception.MaxDatasetNumException;
import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseReader;
//import org.csi.yucca.storage.datamanagementapi.importdatabase.DatabaseReader;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Jdbc;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.MetadataWithExtraAttribute;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Opendata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantsharing;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantssharing;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ckan.Dataset;
import org.csi.yucca.storage.datamanagementapi.model.metadata.ckan.MetadataCkanFactory;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.service.response.CreateDatasetResponse;
import org.csi.yucca.storage.datamanagementapi.service.response.ErrorMessage;
import org.csi.yucca.storage.datamanagementapi.service.response.UpdateDatasetResponse;
import org.csi.yucca.storage.datamanagementapi.singleton.CloudSolrSingleton;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.KnoxSolrSingleton;
import org.csi.yucca.storage.datamanagementapi.singleton.KnoxSolrSingleton.TEHttpSolrClient;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.upload.DataInsertDataUpload;
import org.csi.yucca.storage.datamanagementapi.upload.DataUpload;
import org.csi.yucca.storage.datamanagementapi.upload.SDPBulkInsertException;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;
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

	// public static final String IMPORT_DATABASE_IMPORT_CONFIG =
	// "importConfig";
	public static final String IMPORT_DATABASE_FILE_SQL_KEY = "file";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_SOURCE_TYPE_KEY = "sourceType";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_DATABASE_TYPE_KEY = "dbType";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_JDBC_HOSTNAME_KEY = "jdbc_hostname";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_JDBC_DBNAME_KEY = "jdbc_dbname";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_JDBC_USERNAME_KEY = "jdbc_username";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_JDBC_PASSWORD_KEY = "jdbc_password";
	public static final String IMPORT_DATABASE_IMPORT_CONFIG_ORGANIZATION_CODE = "organizationCode";

	public static final String IMPORT_DATABASE_CONFIG_IMPORT_TYPE_JDBC = "database";
	public static final String IMPORT_DATABASE_CONFIG_IMPORT_TYPE_SCRIPT = "script";

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(MetadataService.class);

	@GET
	@Path("/{tenant}")
	// @Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public String getAllCurrent(@PathParam("tenant") String tenant, @QueryParam("full") boolean full) throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::getAll] - START");
		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		String result = "[]";
		List<Metadata> allDataset = metadataDAO.readAllMetadata(tenant, true);
		if (allDataset != null) {
			Gson gson = JSonHelper.getInstance();

			if (!full) {
				List<Metadata> allDatasetSlim = new LinkedList<Metadata>();
				for (Metadata metadata : allDataset) {
					allDatasetSlim.add(Metadata.slimmify(metadata));
				}
				result = gson.toJson(allDatasetSlim);
			} else
				result = gson.toJson(allDataset);
		}
		return result;
	}

	// disabled download csv, migrated on odata api
	// @GET
	// @Path("/download/{tenant}/{datasetCode}/{format}")
	// @Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadData(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode, @PathParam("format") String format)
			throws NumberFormatException, UnknownHostException, Exception {
		log.debug("[MetadataService::downloadData] - START tenant: " + tenant + "|datasetCode: " + datasetCode + "|format: " + format);
		final char separator = ';';

		if (format == null) {
			format = "csv";
		}

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		final Metadata metadata = metadataDAO.readCurrentMetadataByCode(datasetCode, null);

		String fileName = metadata.getDatasetCode() + "." + format;

		final List<Field> fields = new LinkedList<Field>();

		final List<String> fixedFields = new LinkedList<String>();
		for (Field field : metadata.getInfo().getFields()) {
			fields.add(field);
		}
		final List<String> headerFixedColumn = new LinkedList<String>();

		String collectionName = Config.getInstance().getCollectionTenantData();
		final boolean isStream = metadata.getConfigData() != null && Metadata.CONFIG_DATA_SUBTYPE_STREAM_DATASET.equals(metadata.getConfigData().getSubtype());
		if (isStream) {
			collectionName = Config.getInstance().getCollectionTenantMeasures();

			// stream metadata
			String supportStreamCollection = Config.getInstance().getCollectionSupportStream();

			MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportStreamCollection);
			StreamOut stream = streamDAO.readStreamByMetadata(metadata);

			headerFixedColumn.add("Sensor.Name");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityName()));
			headerFixedColumn.add("Sensor.Description");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityDescription()));
			headerFixedColumn.add("Sensor.SensorID");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityCode()));
			headerFixedColumn.add("Sensor.Type");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityType()));
			headerFixedColumn.add("Sensor.Category");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityCategory()));

			headerFixedColumn.add("Sensor.virtualEntitySlug");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntitySlug()));

			if (stream.getStreams().getStream().getVirtualEntityPositions() != null && stream.getStreams().getStream().getVirtualEntityPositions().getPosition() != null
					&& stream.getStreams().getStream().getVirtualEntityPositions().getPosition().size() > 0) {
				headerFixedColumn.add("Sensor.Latitude");
				fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityPositions().getPosition().get(0).getLat()));
				headerFixedColumn.add("Sensor.Longitude");
				fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityPositions().getPosition().get(0).getLon()));
				headerFixedColumn.add("Sensor.Elevation");
				fixedFields.add(Util.nvlt(stream.getStreams().getStream().getVirtualEntityPositions().getPosition().get(0).getElevation()));
			}

			headerFixedColumn.add("Stream.StreamCode");
			fixedFields.add(Util.nvlt(stream.getStreamCode()));
			headerFixedColumn.add("Stream.StreamName");
			fixedFields.add(Util.nvlt(stream.getStreamName()));
			headerFixedColumn.add("Stream.Frequency");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getFps()));
			headerFixedColumn.add("Stream.TimeStamp");
		}

		// TODO verifico che sia social ed eseguo il codice relativo
		// sovrasciverndo collectionName
		final boolean isSocial = metadata.getConfigData() != null && Metadata.CONFIG_DATA_SUBTYPE_SOCIAL_DATASET.equals(metadata.getConfigData().getSubtype());
		if (isSocial) {
			collectionName = Config.getInstance().getCollectionTenantSocial();

			// stream social
			String supportStreamCollection = Config.getInstance().getCollectionSupportStream();

			MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportStreamCollection);
			StreamOut stream = streamDAO.readStreamByMetadata(metadata);

			headerFixedColumn.add("Tweet.Query");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtQuery()));
			headerFixedColumn.add("Tweet.Latitudine");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtGeolocLat()));
			headerFixedColumn.add("Tweet.Longitudine");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtGeolocLon()));
			headerFixedColumn.add("Tweet.Radius");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtGeolocRadius()));
			headerFixedColumn.add("Tweet.Unit");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtGeolocUnit()));
			headerFixedColumn.add("Tweet.Lang");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtLang()));
			headerFixedColumn.add("Tweet.Locale");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtLocale()));
			headerFixedColumn.add("Tweet.ResultType");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtResultType()));
			headerFixedColumn.add("Tweet.MaxStreamofVE");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtMaxStreamsOfVE()));
			headerFixedColumn.add("Tweet.RatePercentage");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtRatePercentage()));
			headerFixedColumn.add("Tweet.Count");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtCount()));
			headerFixedColumn.add("Tweet.Until");
			fixedFields.add(Util.nvlt(stream.getStreams().getStream().getTwtUntil()));
		}

		DBCollection dataCollection = mongo.getDB("DB_" + tenant).getCollection(collectionName);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idDataset", metadata.getIdDataset());

		final DBCursor cursor = dataCollection.find(searchQuery).limit(Constants.MAX_NUM_ROW_DATA_DOWNLOAD);

		StreamingOutput streamResponse = new StreamingOutput() {

			public void write(OutputStream os) throws IOException, WebApplicationException {

				CSVWriter writer = new CSVWriter(new OutputStreamWriter(os), separator);
				int totalColumn = fields.size();
				if (isStream) {
					totalColumn = headerFixedColumn.size() + fields.size() * 4;// 4
					// is the fields column exposed (alias, measureUnit,
					// dataType, measure)
				} else if (isSocial) {
					totalColumn = headerFixedColumn.size() + fields.size();
				}
				String[] headerNames = new String[totalColumn];
				int counter = 0;
				for (String s : headerFixedColumn) {
					headerNames[counter] = s;
					counter++;
				}
				for (Field field : fields) {
					if (isStream) {
						headerNames[counter] = field.getFieldName() + ".alias";
						counter++;
						headerNames[counter] = field.getFieldName() + ".measureUnit";
						counter++;
						headerNames[counter] = field.getFieldName() + ".dataType";
						counter++;
						headerNames[counter] = field.getFieldName() + ".measure";
						counter++;
					} else if (isSocial) {
						if (field.getFieldName().equals("getText")) {
							headerNames[counter] = "Tweet.Text";
						} else if (field.getFieldName().equals("createdAt")) {
							headerNames[counter] = "Tweet.CreatedDate";
						} else if (field.getFieldName().equals("mediaCnt")) {
							headerNames[counter] = "Tweet.MediaCount";
						} else {
							String output = Character.toUpperCase(field.getFieldName().charAt(0)) + field.getFieldName().substring(1);
							headerNames[counter] = "Tweet." + output;
						}
						counter++;
					} else {
						headerNames[counter] = Util.nvlt(field.getFieldAlias());
						counter++;
					}
				}
				writer.writeNext(headerNames);

				while (cursor.hasNext()) {
					DBObject doc = cursor.next();
					String[] row = new String[totalColumn];
					counter = 0;
					for (String fixedField : fixedFields) {
						row[counter] = fixedField;
						counter++;
					}
					if (metadata.getConfigData() != null && Metadata.CONFIG_DATA_SUBTYPE_STREAM_DATASET.equals(metadata.getConfigData().getSubtype())) {
						if (doc.get("time") != null)
							row[counter] = Constants.ISO_DATE_FORMAT().format(((Date) doc.get("time")));
						else
							row[counter] = "";
						counter++;
					}

					for (Field field : fields) {
						if (isStream) {
							row[counter] = Util.nvlt(field.getFieldAlias());
							counter++;
							row[counter] = Util.nvlt(field.getMeasureUnit());
							counter++;
							row[counter] = Util.nvlt(field.getDataType());
							counter++;
							row[counter] = Util.nvlt(doc.get(field.getFieldName()));
							counter++;
						} else if (isSocial) {
							row[counter] = Util.nvlt(doc.get(field.getFieldName()));
							counter++;
						} else {
							row[counter] = Util.nvlt(doc.get(field.getFieldName()));
							counter++;
						}
					}
					writer.writeNext(row);
				}

				writer.flush();
				writer.close();
			}
		};

		Response build = Response.ok(streamResponse, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=" + fileName).build();
		return build;
	}

	@GET
	@Path("/{tenant}/{datasetCode}")
	// @Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json; charset=UTF-8")
	public String get(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode, @QueryParam(value = "visibleFrom") String visibleFromParam)
			throws NumberFormatException, UnknownHostException {

		log.debug("[MetadataService::get] - START - datasetCode: " + datasetCode);
		System.out.println("DatasetItem requested with datasetCode=" + datasetCode);

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
		String supportApiCollection = Config.getInstance().getCollectionSupportApi();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		Metadata metadata = metadataDAO.readCurrentMetadataByCode(datasetCode, visibleFromParam);

		if (metadata == null) {

			return "{\"errorMsg\": \"Dataset not Found\"}";
		} else {

			MyApi api = null;
			String baseApiUrl = null;
			if (metadata.getInfo().getUnpublished() == null || !metadata.getInfo().getUnpublished()) {
				MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);
				api = apiDAO.readApiByCode(datasetCode);
				baseApiUrl = Config.getInstance().getStoreApiAddress();
			}

			String supportStreamCollection = Config.getInstance().getCollectionSupportStream();
			MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportStreamCollection);
			StreamOut stream = streamDAO.readStreamByMetadata(metadata);

			MetadataWithExtraAttribute dataset = new MetadataWithExtraAttribute(metadata, stream, api, baseApiUrl);
			String datasetJSON = dataset.toJson();

			return datasetJSON;
		}
	}

	@GET
	@Path("/{tenant}/{virtualentityCode}/{streamCode}")
	@Produces("application/json; charset=UTF-8")
	public String getFromStreamKey(@PathParam("tenant") String tenant, @PathParam("virtualentityCode") String virtualentityCode, @PathParam("streamCode") String streamCode,
			@QueryParam(value = "visibleFrom") String visibleFromParam) throws NumberFormatException, UnknownHostException {
		// select
		log.debug("[MetadataService::get] - START - datasetCode: " + virtualentityCode + " - streamCode: " + streamCode);

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportStreamCollection = Config.getInstance().getCollectionSupportStream();
		MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportStreamCollection);
		Metadata md = null;

		final StreamOut stream = streamDAO.readCurrentStreamByCode(virtualentityCode, streamCode, visibleFromParam);

		if (stream.getConfigData().getIdDataset() != null) {
			Long idDataset = stream.getConfigData().getIdDataset();

			String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

			MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
			md = metadataDAO.readCurrentMetadataByIdDataset(idDataset, visibleFromParam);
		}

		final Metadata metadata = md;

		String supportApiCollection = Config.getInstance().getCollectionSupportApi();

		MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);

		MyApi api = apiDAO.readApiByCode(metadata.getDatasetCode());

		String baseApiUrl = Config.getInstance().getStoreApiAddress();

		return new MetadataWithExtraAttribute(metadata, stream, api, baseApiUrl).toJson();
	}

	@GET
	@Path("/opendata/{outputFormat}/tenant/{tenantCode}")
	@Produces("application/json; charset=UTF-8")
	public String getOpendataDatasetListForTenant(@PathParam("outputFormat") String outputFormat, @PathParam("tenantCode") String tenantCode)
			throws NumberFormatException, UnknownHostException {
		return getOpendataDatasetList(outputFormat, tenantCode);
	}

	@GET
	@Path("/opendata/{outputFormat}")
	@Produces("application/json; charset=UTF-8")
	public String getOpendataDatasetListAll(@PathParam("outputFormat") String outputFormat) throws NumberFormatException, UnknownHostException {
		return getOpendataDatasetList(outputFormat, null);
	}

	private String getOpendataDatasetList(String outputFormat, String tentatFilter) throws NumberFormatException, UnknownHostException {
		// select
		log.debug("[MetadataService::getDatasetOpendata] - START - outputFormat: " + outputFormat + " - tentatFilter " + tentatFilter);
		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		List<String> tenantFilter = null;
		if (tentatFilter != null && tentatFilter.length() > 0) {
			tenantFilter = new LinkedList<String>();
			for (String t : tentatFilter.split("[,]")) {
				tenantFilter.add(t);
			}
		}

		List<Metadata> metadataList = metadataDAO.readOpendataMetadata(tenantFilter);

		Gson gson = JSonHelper.getInstance();
		String responseJson = "";
		if (Constants.OPENDATA_EXPORT_FORMAT_CKAN.equalsIgnoreCase(outputFormat)) {
			List<String> result = createCkanPackageList(metadataList);
			responseJson = gson.toJson(result);
		} else {
			ErrorMessage error = new ErrorMessage(ErrorMessage.UNSUPPORTED_FORMAT, "Unsupported output format - " + outputFormat, "Supported format: ckan");
			responseJson = gson.toJson(error);
		}

		return responseJson;
	}

	private List<String> createCkanPackageList(List<Metadata> metadataList) {
		List<String> result = new LinkedList<String>();
		Map<Long, Integer> idVersionMap = new HashMap<Long, Integer>();
		if (metadataList != null) {
			for (Metadata metadata : metadataList) {
				if (!idVersionMap.containsKey(metadata.getIdDataset()) || idVersionMap.get(metadata.getIdDataset()) < metadata.getDatasetVersion()) {
					idVersionMap.put(metadata.getIdDataset(), metadata.getDatasetVersion());
				}
			}
		}

		for (Entry<Long, Integer> idVersion : idVersionMap.entrySet()) {
			result.add(MetadataCkanFactory.createPackageId(idVersion.getKey(), idVersion.getValue()));
		}
		return result;

	}

	@GET
	@Path("/opendata/{outputFormat}/{packageId}")
	@Produces("application/json; charset=UTF-8")
	public String getOpendataDatasetDetail(@PathParam("outputFormat") String outputFormat, @PathParam("packageId") String packageId)
			throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::getOpendataDatasetDetail] - START - outputFormat: " + outputFormat + " - packageId " + packageId);

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		Long idDataset = MetadataCkanFactory.getDatasetDatasetIdFromPackageId(packageId);
		Integer version = MetadataCkanFactory.getDatasetVersionFromPackageId(packageId);

		Gson gson = JSonHelper.getInstance();
		String responseJson = "";
		if (Constants.OPENDATA_EXPORT_FORMAT_CKAN.equalsIgnoreCase(outputFormat)) {
			Metadata metadata = metadataDAO.findFirstMetadataByDatasetId(idDataset, version);
			if (metadata.getOpendata() == null || !metadata.getOpendata().isOpendata()) {
				ErrorMessage error = new ErrorMessage(ErrorMessage.UNAUTHORIZED_DATA, "Unauthorized request", "The requested data is not an open data");
				responseJson = gson.toJson(error);
			} else {
				Dataset datasetCkan = MetadataCkanFactory.createDataset(metadata);
				responseJson = datasetCkan.toJson();
			}
		} else {
			ErrorMessage error = new ErrorMessage(ErrorMessage.UNSUPPORTED_FORMAT, "Unsupported output format - " + outputFormat, "Supported format: ckan");
			responseJson = gson.toJson(error);
		}

		return responseJson;
	}

	@GET
	@Path("/opendata/{outputFormat}/tenant/{tenantCode}/{packageId}")
	@Produces("application/json; charset=UTF-8")
	public String getOpendataDatasetDetailWithTenant(@PathParam("outputFormat") String outputFormat, @PathParam("tenantCode") String tenantCode,
			@PathParam("packageId") String packageId) throws NumberFormatException, UnknownHostException {
		return getOpendataDatasetDetail(outputFormat, packageId);
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
		String fileName = null;

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
					fileName = item.getName();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("[MetadataService::createMetadata] - encoding: " + encoding + ", formatType: " + formatType + ", csvSeparator: " + csvSeparator);
		Metadata metadata = Metadata.fromJson(datasetMetadata);
		if (fileName != null)
			metadata.getInfo().addFilename(fileName);

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
				field.setFieldName(Util.cleanStringCamelCase(field.getFieldName()));
				field.setSinceVersion(metadata.getDatasetVersion());
				if (field != null && field.getDataType() == null)
					field.setDataType("string");
			}
		}
		metadata.getInfo().setRegistrationDate(new Date());

		if (metadata.getInfo().getUnpublished() != null && metadata.getInfo().getUnpublished()) {

			metadata.setDcatCreatorName(null);
			metadata.setDcatCreatorType(null);
			metadata.setDcatCreatorId(null);
			metadata.setDcatRightsHolderName(null);
			metadata.setDcatRightsHolderType(null);
			metadata.setDcatRightsHolderId(null);
			metadata.setDcatNomeOrg(null);
			metadata.setDcatEmailOrg(null);
			metadata.setDcatDataUpdate(null);

			metadata.getInfo().setVisibility("private");

			if (metadata.getInfo().getCodSubDomain() == null || !metadata.getInfo().getCodSubDomain().matches(Constants.SUBDOMAIN_VALIDATION_PATTERN)) {
				createDatasetResponse.addErrorMessage(new ErrorMessage("ERROR_SUBDOMAIN_ERROR", "INVALID SUBDOMAIN", "The subdomain must no contain space"));
			}
		}

		if (metadata.hasFieldNameDuplicate())
			createDatasetResponse.addErrorMessage(new ErrorMessage("ERROR_DUPLICATE_FIELD", "Field Name Duplicate", "The field names must be unique case insensitive"));

		if (createDatasetResponse.hasError()) {
			createDatasetResponse.setDatasetStatus(CreateDatasetResponse.STATUS_DATASET_NOT_CREATED);
		} else {

			try {

				List<SDPBulkInsertException> checkFileToWriteErrors = null;
				// DataUpload dataUpload = new MongoDBDataUpload();
				DataUpload dataUpload = new DataInsertDataUpload();
				if (csvData != null) {
					checkFileToWriteErrors = dataUpload.checkFileToWrite(csvData, csvSeparator, metadata, skipFirstRow);

				}

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

					int maxDatasetNum = ((Number) tenantData.get("maxDatasetNum")).intValue();

					if (maxDatasetNum > 0) {

						int numCurrentDataset = metadataDAO.countAllMetadata(tenant, true);
						log.info(
								"[MetadataService::createMetadata] -  tenant=" + tenant + "     maxDatasetNum=" + maxDatasetNum + "        numCurrentDataset=" + numCurrentDataset);
						// TODO
						if (numCurrentDataset >= maxDatasetNum)
							throw new MaxDatasetNumException("too many dataset");

					}

					metadata.getConfigData().setIdTenant(idTenant);

					// binary metadata: create a metadata record specific for
					// attachment
					Metadata binaryMetadata = null;
					if (metadata.getInfo().getFields() != null) {
						for (Field field : metadata.getInfo().getFields()) {
							if (field.getDataType().equals("binary")) {
								binaryMetadata = Metadata.createBinaryMetadata(metadata);
								break;
							}
						}
					}
					if (binaryMetadata != null) {
						Metadata binaryMetadataCreated = metadataDAO.createMetadata(binaryMetadata, null);
						metadata.getInfo().setBinaryDatasetVersion(binaryMetadataCreated.getDatasetVersion());
						metadata.getInfo().setBinaryIdDataset(binaryMetadataCreated.getIdDataset());
					}

					List<Tenantsharing> lista = new ArrayList<Tenantsharing>();
					if (metadata.getInfo().getTenantssharing() != null) {
						Set<String> tenantSet = new TreeSet<String>();
						for (Tenantsharing tenantInList : metadata.getInfo().getTenantssharing().getTenantsharing()) {
							if (!tenantInList.getTenantCode().equals(metadata.getConfigData().getTenantCode()) && !tenantSet.contains(metadata.getConfigData().getTenantCode())
									&& tenantInList.getIsOwner() != 1) {
								lista.add(tenantInList);
								tenantSet.add(tenantInList.getTenantCode());
							}
						}
					}
					Tenantsharing owner = new Tenantsharing();
					owner.setIdTenant(metadata.getConfigData().getIdTenant());
					owner.setIsOwner(1);
					owner.setTenantCode(metadata.getConfigData().getTenantCode());
					owner.setTenantName(metadata.getConfigData().getTenantCode());
					// owner.setTenantDescription(metadata.getConfigData().get);

					lista.add(owner);
					Tenantsharing arrayTenant[] = new Tenantsharing[lista.size()];
					arrayTenant = lista.toArray(arrayTenant);
					if (metadata.getInfo().getTenantssharing() == null) {
						Tenantssharing tenantssharing = new Tenantssharing();
						metadata.getInfo().setTenantssharing(tenantssharing);
					}
					metadata.getInfo().getTenantssharing().setTenantsharing(arrayTenant);

					// opendata
					if (!"public".equals(metadata.getInfo().getVisibility())) {
						metadata.setOpendata(null);
					}

					metadata.setDcatReady(1);

					Metadata metadataCreated = metadataDAO.createMetadata(metadata, null);
					createDatasetResponse.setMetadata(metadataCreated);
					createDatasetResponse.setDatasetStatus(CreateDatasetResponse.STATUS_DATASET_CREATED);

					if (metadata.getInfo().getUnpublished() == null || !metadata.getInfo().getUnpublished()) {
						MyApi api = MyApi.createFromMetadataDataset(metadataCreated);
						api.getConfigData().setType(Metadata.CONFIG_DATA_TYPE_API);
						api.getConfigData().setSubtype(Metadata.CONFIG_DATA_SUBTYPE_API_MULTI_BULK);

						MyApi apiCreated = apiDAO.createApi(api);
						createDatasetResponse.setApi(apiCreated);
						String apiName = "";

						log.info("[MetadataService::createMetadata] - CALL API PUB SOLR");

						try {

							// SOLR
							// apiName = StoreService.createApiforBulk(metadata,
							// false, datasetMetadata);
							apiName = StoreService.createApiforBulk(metadataCreated, false, datasetMetadata);
						} catch (Exception duplicate) {
							if (duplicate.getMessage() != null && duplicate.getMessage().toLowerCase().contains("duplicate")) {
								try {
									// SOLR
									// apiName =
									// StoreService.createApiforBulk(metadata,
									// true,
									// datasetMetadata);
									apiName = StoreService.createApiforBulk(metadataCreated, true, datasetMetadata);
								} catch (Exception e) {
									log.error("[MetadataService::createMetadata] - ERROR to update API in Store for Bulk. Message: " + duplicate.getMessage());
								}
							} else {
								log.error("[MetadataService::createMetadata] -  ERROR in create or update API in Store for Bulk. Message: " + duplicate.getMessage());
							}
						}
						try {
							StoreService.publishStore("1.0", apiName, "admin");
							CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(Config.getInstance().getStoreUsername(), Config.getInstance().getStorePassword());
							ApiManagerFacade.updateDatasetSubscriptionIntoStore(httpClient, metadata.getInfo().getVisibility(), metadata.getInfo(), apiName);
							createDatasetResponse.setDatasetStatus(CreateDatasetResponse.STATUS_DATASET_PUT_INTO_STORE);
						} catch (Exception e) {
							log.error("[MetadataService::createMetadata] - ERROR in publish Api in store - message: " + e.getMessage());
						}
					}

					log.info("[MetadataService::createMetadata] - END API PUB SOLR");

					if (csvData != null) {
						try { // TODO create data da aggiornare
								// dataUpload.writeFileToMongo(mongo, "DB_" +
								// tenant, "data", metadataCreated);
								// checkFileToWriteErrors =
								// dataUpload.checkFileToWrite(csvData,
								// csvSeparator, metadataCreated, skipFirstRow);
							dataUpload.prepareHeader(metadataCreated);
							dataUpload.writeData(tenant, metadataCreated);
							createDatasetResponse.setDatasetStatus(CreateDatasetResponse.STATUS_DATASET_DATA_UPLOAD);
						} catch (Exception e) {
							log.error("[MetadataService::createMetadata] - writeFileToMongo ERROR: " + e.getMessage());
							createDatasetResponse.addErrorMessage(new ErrorMessage(e));
							e.printStackTrace();
						}
					}
				}
			} catch (MaxDatasetNumException ex) {
				log.error("[MetadataService::createMetadata] - MaxDatasetNumException ERROR: ", ex);
				createDatasetResponse.addErrorMessage(new ErrorMessage(ex));

			}
		}
		return createDatasetResponse.toJson();
	}

	public static void main(String[] args) {
		String s = "I dati in oggetto riguardano la produzione annuale di rifiuti urbani a livello comunale comprensiva della % di raccolta differenziata raggiunta e del dettaglio delle tipologie di rifiuti raccolte ed avviate a recupero o a smaltimento.In particolare la % di raccolta differenziata viene calcolata sulla base del metodo regionale di cui alla D.G.R. 43-435 del 10 luglio 2000.L’acquisizione dei dati è disciplinata dal protocollo di cui dalla DGR 2 maggio 2001, n°17-2876 mod. da DGR 23 dicembre 2003, n° 48-11386 ed avviene attraverso l’uso di un sistema in rete che utilizza la RUPAR (Rete Unitaria della Pubblica Amministrazione Regionale).Gli utenti abilitati ad accedere a tale applicativo sono i Consorzi di gestione rifiuti, le Province e la Regione i quali, via web browser, utilizzano i servizi disponibili a seconda del profilo assegnato.I dati forniti dai Consorzi, dopo un controllo da parte della Provincia e della Regione, vengono approvati formalmente con deliberazione di giunta regionale.Con D.G.R. 47-5101 del 18/12/2012 sono stati approvati i quantitativi di rifiuti raccolti nel 2011 in modo differenziato e indifferenziato oggetto del presente dataset.Tale procedura ha garantito negli anni informazioni controllate ed omogenee su tutto il territorio regionale.";
		String s600 = s != null ? s.substring(0, 600) : "";
		System.out.println("s600 " + s600);
		String sSmall = "AGRICULTURE,RAIN";
		System.out.println("sSmall " + Util.safeSubstring(sSmall, 600));
	}

	@POST
	// TODO add data da aggiornare
	@Path("/add/{tenant}/{datasetCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public String addData(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode, @Context HttpServletRequest request)
			throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::addData] - START");

		String encoding = null;
		String formatType = null;
		String csvSeparator = null;
		boolean skipFirstRow = false;
		String csvData = null;
		String fileName = null;

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
					fileName = item.getName();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("[MetadataService::addData] - encoding: " + encoding + ", formatType: " + formatType + ", csvSeparator: " + csvSeparator);
		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

		Metadata existingMetadata = metadataDAO.readCurrentMetadataByCode(datasetCode, null);
		existingMetadata.getInfo().addFilename(fileName);
		metadataDAO.updateMetadata(existingMetadata);

		UpdateDatasetResponse updateDatasetResponse = new UpdateDatasetResponse();

		// Scommentare per vecchio utilizzo delle MONGO DB DATA UPLOAD!!
		// DataUpload dataUpload = new MongoDBDataUpload();
		DataUpload dataUpload = new DataInsertDataUpload();

		List<SDPBulkInsertException> checkFileToWriteErrors = dataUpload.checkFileToWrite(csvData, csvSeparator, existingMetadata, skipFirstRow);
		if (checkFileToWriteErrors != null && checkFileToWriteErrors.size() > 0) {
			for (SDPBulkInsertException error : checkFileToWriteErrors) {
				updateDatasetResponse.addErrorMessage(new ErrorMessage(error.getErrorCode(), error.getErrorMessage(), error.getErrorDetail()));
			}
		} else {

			try {
				// dataUpload.writeFileToMongo(mongo, "DB_" + tenant, "data",
				// existingMetadata);
				dataUpload.writeData(tenant, existingMetadata);
			} catch (Exception e) {
				log.error("[MetadataService::addData] - writeFileToMongo ERROR: " + e.getMessage());
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

		// boolean fromPublicToPrivate = false;
		// boolean fromPrivateToPublic = false;

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
			Metadata existingMetadata = metadataDAO.readCurrentMetadataByCode(datasetCode, null);
			Metadata newMetadata = metadataDAO.readCurrentMetadataByCode(datasetCode, null);

			newMetadata.getInfo().setDatasetName(inputMetadata.getInfo().getDatasetName());
			newMetadata.getInfo().setExternalReference(inputMetadata.getInfo().getExternalReference());
			newMetadata.getInfo().setCopyright(inputMetadata.getInfo().getCopyright());
			newMetadata.getInfo().setDescription(inputMetadata.getInfo().getDescription());
			newMetadata.getInfo().setDisclaimer(inputMetadata.getInfo().getDisclaimer());
			newMetadata.getInfo().setLicense(inputMetadata.getInfo().getLicense());
			newMetadata.getInfo().setTags(inputMetadata.getInfo().getTags());
			newMetadata.getInfo().setFileNames(inputMetadata.getInfo().getFileNames());
			newMetadata.getInfo().setVisibility(inputMetadata.getInfo().getVisibility());
			newMetadata.getInfo().setIcon(inputMetadata.getInfo().getIcon());
			newMetadata.getInfo().setTenantssharing(inputMetadata.getInfo().getTenantssharing());

			newMetadata.getInfo().setDataDomain(existingMetadata.getInfo().getDataDomain());
			newMetadata.getInfo().setCodSubDomain(existingMetadata.getInfo().getCodSubDomain());

			newMetadata.setAvailableHive(existingMetadata.getAvailableHive());
			newMetadata.setAvailableSpeed(existingMetadata.getAvailableSpeed());
			newMetadata.setIsTransformed(existingMetadata.getIsTransformed());
			newMetadata.setDbHiveSchema(existingMetadata.getDbHiveSchema());
			newMetadata.setDbHiveTable(existingMetadata.getDbHiveTable());

			if ("MULTI".equals(newMetadata.getInfo().getCodSubDomain()))
				newMetadata.getInfo().setUnpublished(existingMetadata.getInfo().getUnpublished());
			else
				newMetadata.getInfo().setUnpublished(inputMetadata.getInfo().getUnpublished());

			// if (newMetadata.getInfo().getVisibility() !=
			// existingMetadata.getInfo().getVisibility()) {
			// if (newMetadata.getInfo().getVisibility().equals("public")) {
			// fromPrivateToPublic = true;
			// } else {
			// fromPublicToPrivate = true;
			// }
			// }

			if ("public".equals(newMetadata.getInfo().getVisibility()) && inputMetadata.getOpendata() != null && inputMetadata.getOpendata().isOpendata()) {
				Opendata opendata = new Opendata();
				opendata.setOpendata(true);
				opendata.setMetadaUpdateDate(new Date());
				opendata.setSourceId(inputMetadata.getOpendata().getSourceId());
				opendata.setAuthor(inputMetadata.getOpendata().getAuthor());
				opendata.setLanguage(inputMetadata.getOpendata().getLanguage() == null ? "it" : inputMetadata.getOpendata().getLanguage());
				opendata.setDataUpdateDate(inputMetadata.getOpendata().getDataUpdateDate());
				opendata.setOpendataupdatefrequency(inputMetadata.getOpendata().getOpendataupdatefrequency());
				newMetadata.setOpendata(opendata);
			}

			newMetadata.setDcatReady(1);

			if (newMetadata.getInfo().getUnpublished() != null && newMetadata.getInfo().getUnpublished()) {

				newMetadata.setDcatCreatorName(null);
				newMetadata.setDcatCreatorType(null);
				newMetadata.setDcatCreatorId(null);
				newMetadata.setDcatRightsHolderName(null);
				newMetadata.setDcatRightsHolderType(null);
				newMetadata.setDcatRightsHolderId(null);
				newMetadata.setDcatNomeOrg(null);
				newMetadata.setDcatEmailOrg(null);
				newMetadata.setDcatDataUpdate(null);

				newMetadata.getInfo().setVisibility("private");

				if (newMetadata.getInfo().getCodSubDomain() == null || !newMetadata.getInfo().getCodSubDomain().matches(Constants.SUBDOMAIN_VALIDATION_PATTERN)) {
					updateDatasetResponse.addErrorMessage(new ErrorMessage("ERROR_SUBDOMAIN_ERROR", "INVALID SUBDOMAIN", "The subdomain must no contain space"));
				}
			} else {
				newMetadata.setDcatNomeOrg(inputMetadata.getDcatNomeOrg());
				newMetadata.setDcatEmailOrg(inputMetadata.getDcatEmailOrg());
				newMetadata.setDcatCreatorName(inputMetadata.getDcatCreatorName());
				newMetadata.setDcatCreatorType(inputMetadata.getDcatCreatorType());
				newMetadata.setDcatCreatorId(inputMetadata.getDcatCreatorId());
				newMetadata.setDcatRightsHolderName(inputMetadata.getDcatRightsHolderName());
				newMetadata.setDcatRightsHolderType(inputMetadata.getDcatRightsHolderType());
				newMetadata.setDcatRightsHolderId(inputMetadata.getDcatRightsHolderId());
			}
			List<Tenantsharing> lista = new ArrayList<Tenantsharing>();
			if (newMetadata.getInfo().getTenantssharing() != null) {
				Set<String> tenantSet = new TreeSet<String>();
				for (Tenantsharing tenantInList : newMetadata.getInfo().getTenantssharing().getTenantsharing()) {
					if (!tenantInList.getTenantCode().equals(newMetadata.getConfigData().getTenantCode()) && !tenantSet.contains(newMetadata.getConfigData().getTenantCode())
							&& tenantInList.getIsOwner() != 1) {
						lista.add(tenantInList);
						tenantSet.add(tenantInList.getTenantCode());
					}
				}
			}
			Tenantsharing owner = new Tenantsharing();
			owner.setIdTenant(newMetadata.getConfigData().getIdTenant());
			owner.setIsOwner(1);
			owner.setTenantCode(newMetadata.getConfigData().getTenantCode());
			owner.setTenantName(newMetadata.getConfigData().getTenantCode());
			// owner.setTenantDescription(metadata.getConfigData().get);

			lista.add(owner);
			Tenantsharing arrayTenant[] = new Tenantsharing[lista.size()];
			arrayTenant = lista.toArray(arrayTenant);
			if (newMetadata.getInfo().getTenantssharing() == null) {
				Tenantssharing tenantssharing = new Tenantssharing();
				newMetadata.getInfo().setTenantssharing(tenantssharing);
			}
			newMetadata.getInfo().getTenantssharing().setTenantsharing(arrayTenant);

			// int counter = 0;
			// for (Field existingField : newMetadata.getInfo().getFields()) {
			// existingField.setFieldAlias(inputMetadata.getInfo().getFields()[counter].getFieldAlias());
			// counter++;
			// }

			if (inputMetadata.getInfo().getFields() != null) {
				Field[] fields = new Field[inputMetadata.getInfo().getFields().length];
				int counter = 0;
				for (Field field : inputMetadata.getInfo().getFields()) {

					Field existingField = newMetadata.getFieldFromFieldName(field.getFieldName());
					if (existingField != null) {
						if (existingField.getSinceVersion() == null)
							field.setSinceVersion(newMetadata.getDatasetVersion() + 1);
						else
							field.setSinceVersion(existingField.getSinceVersion());

					} else {
						field.setFieldName(Util.cleanStringCamelCase(field.getFieldName()));
						field.setSinceVersion(newMetadata.getDatasetVersion() + 1);
					}
					fields[counter] = field;
					counter++;
				}

				newMetadata.getInfo().setFields(fields);
			}
			if (newMetadata.hasFieldNameDuplicate()) {
				updateDatasetResponse.addErrorMessage(new ErrorMessage("ERROR_DUPLICATE_FIELD", "Field Name Duplicate", "The field names must be unique case insensitive"));
			}
			if (updateDatasetResponse.hasError())
				updateDatasetResponse.setMetadata(newMetadata);
			else {
				existingMetadata.getConfigData().setCurrent(0);
				metadataDAO.updateMetadata(existingMetadata);
				metadataDAO.createNewVersion(newMetadata);

				updateDatasetResponse.setMetadata(newMetadata);
				log.info("[MetadataService::updateMetadata] - new metadata unpublished: " + newMetadata.getInfo().getUnpublished());
				if (newMetadata.getInfo().getUnpublished() == null || !newMetadata.getInfo().getUnpublished()) {

					log.info("[MetadataService::updateMetadata] - publish");

					String supportApiCollection = Config.getInstance().getCollectionSupportApi();
					MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);

					MyApi existingApi = apiDAO.readApiByCode(newMetadata.getDatasetCode());
					if (existingApi == null) {
						MyApi api = MyApi.createFromMetadataDataset(newMetadata);
						api.getConfigData().setType(Metadata.CONFIG_DATA_TYPE_API);
						api.getConfigData().setSubtype(Metadata.CONFIG_DATA_SUBTYPE_API_MULTI_BULK);

						apiDAO.createApi(api);

					}

					String apiName = "";
					// Boolean updateOperation = false;
					try {
						apiName = StoreService.createApiforBulk(newMetadata, false, metadataInput);
					} catch (Exception duplicate) {
						if (duplicate.getMessage().toLowerCase().contains("duplicate")) {
							try {
								apiName = StoreService.createApiforBulk(newMetadata, true, metadataInput);
								// updateOperation = true;
							} catch (Exception e) {
								log.error("[MetadataService::updateMetadata] - ERROR to update API in Store for Bulk. Message: " + duplicate.getMessage());
							}
						} else {
							log.error("[MetadataService::updateMetadata] -  ERROR in create or update API in Store for Bulk. Message: " + duplicate.getMessage());
						}
					}
					// if (!updateOperation) {
					StoreService.publishStore("1.0", apiName, "admin");
					CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(Config.getInstance().getStoreUsername(), Config.getInstance().getStorePassword());
					ApiManagerFacade.updateDatasetSubscriptionIntoStore(httpClient, newMetadata.getInfo().getVisibility(), newMetadata.getInfo(), apiName);
				} else if (existingMetadata.getInfo() != null && existingMetadata.getInfo().getUnpublished() != null && !existingMetadata.getInfo().getUnpublished()) {
					log.info("[MetadataService::updateMetadata] - delete publish info ");

					String apiName = existingMetadata.getDatasetCode() + "_odata";
					log.info("[MetadataService::updateMetadata] - unpublish apiName: " + apiName);
					try {
						boolean removeStore = StoreService.removeStore("1.0", apiName, "admin");
						log.info("[MetadataService::updateMetadata] - unpublish removeStore: " + removeStore);

					} catch (Exception ex) {
						log.error("Impossible to remove " + apiName + ex.getMessage());
					}
					if ("KNOX".equalsIgnoreCase(Config.getInstance().getSolrTypeAccess())) {
						log.info("[MetadataService::updateMetadata] - unpublish using KNOX");

						SolrClient solrServer = null;
						solrServer = KnoxSolrSingleton.getServer();
						((TEHttpSolrClient) solrServer).setDefaultCollection(Config.getInstance().getSolrCollection());
						UpdateResponse resp = solrServer.deleteById(Config.getInstance().getSolrCollection(), "" + existingMetadata.getDatasetCode());
						solrServer.commit();
						log.info("[MetadataService::updateMetadata] deleted status" + resp.getStatus());
					} else {
						log.info("[MetadataService::updateMetadata] - unpublish not using KNOX");

						CloudSolrClient solrServer = CloudSolrSingleton.getServer();
						solrServer.setDefaultCollection(Config.getInstance().getSolrCollection());
						UpdateResponse resp = solrServer.deleteById(Config.getInstance().getSolrCollection(), "" + existingMetadata.getDatasetCode());
						UpdateResponse commit = solrServer.commit();

						log.info("[MetadataService::updateMetadata] Config.getInstance().getSolrCollection() " + Config.getInstance().getSolrCollection());
						log.info("[MetadataService::updateMetadata] existingMetadata.getDatasetCode() " + existingMetadata.getDatasetCode());
						log.info("[MetadataService::updateMetadata] deleted update status " + resp.getStatus());
						log.info("[MetadataService::updateMetadata] deleted commit status " + commit.getStatus());
					}
				}
			}
			// }
		} catch (Exception e) {
			log.debug("[MetadataService::updateMetadata] - ERROR " + e.getMessage());
			updateDatasetResponse.addErrorMessage(new ErrorMessage(e));
			e.printStackTrace();
		}
		return updateDatasetResponse.toJson();
	}

	@GET
	@Path("/icon/{tenant}/{datasetCode}")
	@Produces("image/png")
	public Response datasetIcon(@PathParam("tenant") String tenant, @PathParam("datasetCode") String datasetCode) throws NumberFormatException, UnknownHostException, Exception {
		log.debug("[MetadataService::datasetIcon] - START tenant: " + tenant + "|datasetCode: " + datasetCode);

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		final Metadata metadata = metadataDAO.readCurrentMetadataByCode(datasetCode, null);

		byte[] iconBytes = metadata.readDatasetIconBytes();

		return Response.ok().entity(iconBytes).type("image/png").build();
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
				String lineUtf8 = new String(line.getBytes("iso-8859-1"), "UTF-8");
				sb.append(lineUtf8);
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

	/*
	 * @GET
	 * 
	 * @Path("/importDatabase/{tenant}")
	 * 
	 * @Produces("application/json; charset=UTF-8") public Response
	 * importDatabaseGet(@PathParam("tenant") String tenantCode,
	 * 
	 * @QueryParam("dbType") String dbType, @QueryParam("sourceType") String
	 * sourceType, @QueryParam("jdbc_hostname") String jdbcHostname,
	 * 
	 * @QueryParam("jdbc_dbname") String jdbcDbname,
	 * 
	 * @QueryParam("jdbc_username") String jdbcUsername,
	 * 
	 * @QueryParam("jdbc_password") String jdbcPassword) throws
	 * NumberFormatException, UnknownHostException {
	 * 
	 * log.debug("[MetadataService::importDatabase] - START"); Response
	 * response; try { String dbSchema = importDatabaseJdbc(tenantCode, dbType,
	 * jdbcHostname, jdbcDbname, jdbcUsername, jdbcPassword); response =
	 * Response.ok(dbSchema, MediaType.APPLICATION_JSON).build(); } catch
	 * (ImportDatabaseException e) { response =
	 * Response.status(Response.Status.BAD_REQUEST).entity(e.toJson()).build();
	 * e.printStackTrace(); } catch (Exception ex) { ImportDatabaseException e =
	 * new ImportDatabaseException(ex.getMessage(), null); response =
	 * Response.status
	 * (Response.Status.INTERNAL_SERVER_ERROR).entity(e.toJson()).build(); }
	 * 
	 * return response; // return dbSchema; }
	 */

	// Import Database
	@POST
	@Path("/importDatabase/{tenant}")
	// @Produces(MediaType.APPLICATION_JSON)
	@Produces("text/plain; charset=UTF-8")
	// allowCredentials = true, exposeHeaders = { "X-custom-3", "X-custom-4" },
	// allowMethods = {"POST"})
	public Response importDatabase(@Context HttpServletRequest request, @PathParam("tenant") String tenantCode) throws NumberFormatException, UnknownHostException {

		log.debug("[MetadataService::importDatabase] - START");

		String dbSchema = null;
		String dbType = null;
		String sourceType = null;
		String jdbcHostname = null;
		String jdbcDbname = null;
		String jdbcUsername = null;
		String jdbcPassword = null;
		String organizationCode = null;

		Response response;
		try {
			FileItemStream scriptItemStream = null;
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (IMPORT_DATABASE_IMPORT_CONFIG_DATABASE_TYPE_KEY.equals(item.getFieldName()))
					dbType = read(item.openStream());
				else if (IMPORT_DATABASE_IMPORT_CONFIG_SOURCE_TYPE_KEY.equals(item.getFieldName()))
					sourceType = read(item.openStream());
				else if (IMPORT_DATABASE_IMPORT_CONFIG_JDBC_HOSTNAME_KEY.equals(item.getFieldName()))
					jdbcHostname = read(item.openStream());
				else if (IMPORT_DATABASE_IMPORT_CONFIG_JDBC_DBNAME_KEY.equals(item.getFieldName()))
					jdbcDbname = read(item.openStream());
				else if (IMPORT_DATABASE_IMPORT_CONFIG_JDBC_USERNAME_KEY.equals(item.getFieldName()))
					jdbcUsername = read(item.openStream());
				else if (IMPORT_DATABASE_IMPORT_CONFIG_JDBC_PASSWORD_KEY.equals(item.getFieldName()))
					jdbcPassword = read(item.openStream());
				else if (IMPORT_DATABASE_IMPORT_CONFIG_ORGANIZATION_CODE.equals(item.getFieldName()))
					organizationCode = read(item.openStream());

				else if (IMPORT_DATABASE_FILE_SQL_KEY.equals(item.getFieldName())) {
					// sqlScript = readFileRows(item.openStream(), encoding);
					scriptItemStream = item;
					// fileName = item.getName();
				}

			}

			// String dbSchema = importDatabaseJdbc(dbType, jdbcHostname,
			// jdbcDbname, jdbcUsername, jdbcPassword);
			if (IMPORT_DATABASE_CONFIG_IMPORT_TYPE_JDBC.equals(sourceType))
				dbSchema = importDatabaseJdbc(organizationCode, tenantCode, dbType, jdbcHostname, jdbcDbname, jdbcUsername, jdbcPassword);
			else if (IMPORT_DATABASE_CONFIG_IMPORT_TYPE_JDBC.equals(sourceType) && scriptItemStream != null)
				dbSchema = importDatabaseScript(dbType, scriptItemStream);

			response = Response.ok(dbSchema, MediaType.APPLICATION_JSON).build();
		} catch (ImportDatabaseException e) {
			response = Response.status(Response.Status.BAD_REQUEST).entity(e.toJson()).build();
			e.printStackTrace();
		} catch (Exception ex) {
			ImportDatabaseException e = new ImportDatabaseException(ex.getMessage(), null);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toJson()).build();
		}

		return response;
	}

	private String importDatabaseScript(String dbType, FileItemStream scriptItemStream) {
		// TODO Auto-generated method stub
		return null;
	}

	private String importDatabaseJdbc(String organizationCode, String tenantCode, String dbType, String hostname, String dbname, String username, String password)
			throws ClassNotFoundException, SQLException, ImportDatabaseException {
		DatabaseReader databaseReader = new DatabaseReader(organizationCode, tenantCode, dbType, hostname, dbname, username, password);
		return databaseReader.loadSchema();
	}

	@GET
	@Path("/ingestion_config/{tenant}")
	// @Produces(MediaType.APPLICATION_JSON)
	@Produces("application/csv; charset=UTF-8")
	public String getIngestionConfig(@PathParam("tenant") String tenant, @QueryParam("dbname") String dbName, @QueryParam("dateformat") String dateformat,
			@QueryParam("separator") String sep, @QueryParam("help") boolean help, @QueryParam("onlyImported") Boolean onlyImported)
			throws NumberFormatException, UnknownHostException {
		log.debug("[MetadataService::getImportInfo] - START");
		if (help) {
			return "CSV da usare per caricare un csv contentente la configuarazione da usare per l'ingestion\n\n" + "PARAMETRI\n"
					+ " - dbName: nome database da includere, se omesso vengono presi tutti\n" + " - sep: separatore di colonna, se omesso viene usata la tabulazione\n"
					+ " - onlyImported: flag indicante se includere solo i dataset importati da db: default true\n"
					+ " - dateformat: formato data, se omesso viene usato dd/MM/yyyy\n";
		} else {

			MongoClient mongo = MongoSingleton.getMongoClient();
			String supportDb = Config.getInstance().getDbSupport();
			String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
			MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

			if (dateformat == null)
				dateformat = "dd/MM/yyyy";
			DateFormat df = new SimpleDateFormat(dateformat);
			if (sep == null)
				sep = "\t";

			if (onlyImported == null)
				onlyImported = true;

			String csv = "table" + sep + "column" + sep + "comments" + sep + "datasetCode" + sep + "domain" + sep + "subdomain" + sep + "visibility" + sep + "opendata" + sep
					+ "registrationDate" + sep + "dbName" + sep + "dbSchema" + sep + "dbUrl\n";
			// out csv -> jdbc.url, jdbc.username, tablename_jdbc, datasetcode,
			// registration date, colonne(? chieredere maurizio o fabrizio)
			// dominio, sottodominio, dataset privato/pubblico/open data
			List<Metadata> allDataset = metadataDAO.readAllMetadata(tenant, true);
			if (allDataset != null) {
				for (Metadata metadata : allDataset) {
					if (metadata.getConfigData() != null && (!onlyImported || metadata.getConfigData().getJdbc() != null) && metadata.getInfo().getFields() != null
							&& (dbName == null || dbName.equals(metadata.getConfigData().getJdbc().getDbName()))) {
						Jdbc jdbc = metadata.getConfigData().getJdbc();
						String registrationDate = "";
						if (metadata.getInfo()!=null && metadata.getInfo().getRegistrationDate() != null)
							registrationDate = df.format(metadata.getInfo().getRegistrationDate());

						if (jdbc == null) {
							jdbc = new Jdbc();
							jdbc.setDbName("");
							jdbc.setDbSchema("");
							jdbc.setDbType("");
							jdbc.setDbName("");
							jdbc.setTableName("");
						}
						String domain = metadata.getInfo() != null && metadata.getInfo().getDataDomain() != null ? metadata.getInfo().getDataDomain().toLowerCase() : "";
						String subdomain = metadata.getInfo() != null && metadata.getInfo().getCodSubDomain() != null ? metadata.getInfo().getCodSubDomain().toLowerCase() : "";
						String visibility = metadata.getInfo() != null ? metadata.getInfo().getVisibility() : "";
						Boolean opendata = metadata.getOpendata() != null ? metadata.getOpendata().isOpendata() : false;
						String dbSchema = jdbc.getDbSchema() == null ? jdbc.getDbName() : jdbc.getDbSchema();
						for (Field f : metadata.getInfo().getFields()) {
							String comments = f.getFieldAlias() == null ? "" : f.getFieldAlias().replaceAll("[\\t\\n\\r]+", " ");
							String row = jdbc.getTableName() + sep + f.getSourceColumnName() + sep + comments + sep + metadata.getDatasetCode() + sep + domain + sep + subdomain
									+ sep + visibility + sep + opendata + sep + registrationDate + sep + jdbc.getDbName() + sep + dbSchema + sep + jdbc.getDbUrl();
							csv += row + "\n";
						}
					}
				}
			}
			return csv;
		}
	}
}
