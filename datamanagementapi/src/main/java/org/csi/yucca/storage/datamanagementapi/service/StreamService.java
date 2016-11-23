package org.csi.yucca.storage.datamanagementapi.service;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBApiDAO;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBStreamDAO;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Components;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Element;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Stream;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.Streams;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.mongodb.MongoClient;

@Path("/stream")
public class StreamService {

	public static final String CONSUMER_YUCCA_LIGHT = "yuccaLight";

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(MetadataService.class);

	@GET
	@Path("/metadata")
	@Produces("application/json; charset=UTF-8")
	public String getAllCurrent(@QueryParam("tenant") String tenant, @QueryParam("consumerType") String consumerType,
			@QueryParam("consumerId") String consumerId, @QueryParam("consumerVersion") String consumerVersion) throws NumberFormatException,
			UnknownHostException {
		log.debug("[MetadataService::getAll] - START");

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();

		String supportDatasetCollection = Config.getInstance().getCollectionSupportStream();
		MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportDatasetCollection);

		String supportMetadataCollection = Config.getInstance().getCollectionSupportDataset();
		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportMetadataCollection);

		String supportApiCollection = Config.getInstance().getCollectionSupportApi();
		MongoDBApiDAO apiDAO = new MongoDBApiDAO(mongo, supportDb, supportApiCollection);

		String result = "[]";
		List<StreamOut> allStreams = streamDAO.readAllStream(tenant, false);
		if (allStreams != null) {
			if (CONSUMER_YUCCA_LIGHT.equals(consumerType)) {
				allStreams = createStreamsYuccaLight(allStreams, metadataDAO, apiDAO);
			}
			Gson gson = JSonHelper.getInstance();
			result = gson.toJson(allStreams);
		}
		return result;
	}

	@GET
	@Path("/icon/{tenant}/{virtualentityCode}/{streamCode}")
	@Produces("image/png")
	public Response streamIcon(@PathParam("tenant") String tenant, @PathParam("virtualentityCode") String virtualentityCode,
			@PathParam("streamCode") String streamCode) throws NumberFormatException, UnknownHostException, Exception {
		log.debug("[MetadataService::datasetIcon] - START tenant: " + tenant + "|streamCode: " + streamCode);

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportStreamCollection = Config.getInstance().getCollectionSupportStream();
		MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportStreamCollection);

		final StreamOut stream = streamDAO.readCurrentStreamByCode(virtualentityCode, streamCode, null);
		Long idDataset = stream.getConfigData().getIdDataset();

		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		final Metadata metadata = metadataDAO.readCurrentMetadataByIdDataset(idDataset, null);

		byte[] iconBytes = metadata.readDatasetIconBytes();

		return Response.ok().entity(iconBytes).type("image/png").build();
	}

	@GET
	@Path("/datasetCode/{tenant}/{virtualentityCode}/{streamCode}")
	@Produces("text/plain")
	public Response streamDatasetCode(@PathParam("tenant") String tenant, @PathParam("virtualentityCode") String virtualentityCode,
			@PathParam("streamCode") String streamCode) throws NumberFormatException, UnknownHostException, Exception {
		log.debug("[MetadataService::datasetIcon] - START tenant: " + tenant + "|streamCode: " + streamCode);

		MongoClient mongo = MongoSingleton.getMongoClient();
		String supportDb = Config.getInstance().getDbSupport();
		String supportStreamCollection = Config.getInstance().getCollectionSupportStream();
		MongoDBStreamDAO streamDAO = new MongoDBStreamDAO(mongo, supportDb, supportStreamCollection);

		final StreamOut stream = streamDAO.readCurrentStreamByCode(virtualentityCode, streamCode);
		Long idDataset = stream.getConfigData().getIdDataset();

		String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();

		MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);
		final Metadata metadata = metadataDAO.readCurrentMetadataByIdDataset(idDataset);

		String datasetCode = metadata.getDatasetCode();

		return Response.ok().entity(datasetCode).type("text/plain").build();
	}
	
	private List<StreamOut> createStreamsYuccaLight(List<StreamOut> streamsIn, MongoDBMetadataDAO metadataDAO, MongoDBApiDAO apiDAO) {
		List<StreamOut> streamsOut = null;
		if (streamsIn != null) {
			streamsOut = new LinkedList<StreamOut>();

			for (StreamOut streamFull : streamsIn) {
				if (streamFull.getStreams() != null
						&& streamFull.getStreams().getStream() != null
						&& ("Device".equals(streamFull.getStreams().getStream().getVirtualEntityType()) || "Application".equals(streamFull.getStreams()
								.getStream().getVirtualEntityType()))) {

					StreamOut streamLight = new StreamOut();
					streamLight.setStreamCode(streamFull.getStreamCode());
					streamLight.setStreamName(streamFull.getStreamName());

					if (streamFull.getConfigData() != null) {
						ConfigData configData = new ConfigData();
						configData.setTenantCode(streamFull.getConfigData().getTenantCode());
						streamLight.setConfigData(configData);
					}

					Stream stream = new Stream();

					stream.setVirtualEntityName(streamFull.getStreams().getStream().getVirtualEntityName());
					stream.setVirtualEntityDescription(streamFull.getStreams().getStream().getVirtualEntityDescription());
					stream.setVirtualEntityCode(streamFull.getStreams().getStream().getVirtualEntityCode());
					stream.setVirtualEntityType(streamFull.getStreams().getStream().getVirtualEntityType());
					stream.setVirtualEntityCategory(streamFull.getStreams().getStream().getVirtualEntityCategory());

					stream.setDomainStream(streamFull.getStreams().getStream().getDomainStream());
					stream.setLicence(streamFull.getStreams().getStream().getLicence());
					stream.setDisclaimer(streamFull.getStreams().getStream().getDisclaimer());
					stream.setCopyright(streamFull.getStreams().getStream().getCopyright());
					stream.setVisibility(streamFull.getStreams().getStream().getVisibility());
					stream.setDeploymentVersion(streamFull.getStreams().getStream().getDeploymentVersion());
					stream.setStreamIcon(streamFull.getStreams().getStream().getStreamIcon());

					if (streamFull.getStreams().getStream().getComponents() != null) {
						Components components = new Components();
						List<Element> elementsLight = new LinkedList<Element>();
						for (Element elementFull : streamFull.getStreams().getStream().getComponents().getElement()) {
							Element elementLight = new Element();
							elementLight.setComponentName(elementFull.getComponentName());
							elementLight.setComponentAlias(elementFull.getComponentAlias());
							elementLight.setTolerance(elementFull.getTolerance());
							elementLight.setMeasureUnit(elementFull.getMeasureUnit());
							elementLight.setMeasureUnitCategory(elementFull.getMeasureUnitCategory());
							elementLight.setPhenomenon(elementFull.getPhenomenon());
							elementLight.setPhenomenonCategory(elementFull.getPhenomenonCategory());
							elementLight.setDataType(elementFull.getDataType());

							elementsLight.add(elementLight);

						}
						components.setElement(elementsLight);
						stream.setComponents(components);
					}

					stream.setStreamTags(streamFull.getStreams().getStream().getStreamTags());
					stream.setVirtualEntityPositions(streamFull.getStreams().getStream().getVirtualEntityPositions());

					streamLight.setStreams(new Streams());
					streamLight.getStreams().setStream(stream);

					if (metadataDAO != null) {
						Metadata medatata = metadataDAO.readCurrentMetadataByIdDataset(streamFull.getConfigData().getIdDataset(), null);
						streamLight.setDatasetCode(medatata.getDatasetCode());
					}
					if (apiDAO != null) {
						MyApi api = apiDAO.readFirstApiByIdStream(streamFull.getIdStream());
						streamLight.setApiCode(api.getApiCode());

					}

					streamsOut.add(streamLight);
				}

			}
		}
		return streamsOut;
	}

}
