package org.csi.yucca.storage.datamanagementapi.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBMetadataDAO;
import org.csi.yucca.storage.datamanagementapi.mail.SendHTMLEmail;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.csi.yucca.storage.datamanagementapi.model.types.FileStatus;
import org.csi.yucca.storage.datamanagementapi.model.types.POJOHdfs;
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
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

@Path("/metadata")
public class InstallCepService {

	private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	private static final String OK_RESULT = "{OK:1}";
	private static final String KO_RESULT = "{KO:1}";

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(InstallCepService.class);


	@DELETE
	@Path("/clearDataset/{tenant}/{idDataset}")
	@Produces(MediaType.APPLICATION_JSON)
	public String clearDataset(@PathParam("tenant") String tenant, @PathParam("idDataset") String idDataset) throws UnknownHostException {
		String returnJSONValue = deleteDatasetData(tenant,idDataset,null);
		if (returnJSONValue.equals(JSON.parse(OK_RESULT).toString())){
			//Cancellazione HDFS;
			returnJSONValue = deleteHDFSData(tenant,idDataset,null);
		}
		return returnJSONValue;
	}	


	@DELETE
	@Path("/clearDataset/{tenant}/{idDataset}/{datasetVersion}")
	@Produces(MediaType.APPLICATION_JSON)
	public String clearDatasetByVersion(@PathParam("tenant") String tenant, @PathParam("idDataset") String idDataset,@PathParam("datasetVersion") String datasetVersion) throws UnknownHostException {
		String returnJSONValue = deleteDatasetData(tenant,idDataset,datasetVersion);
		if (returnJSONValue.equals(JSON.parse(OK_RESULT).toString())){
			//Cancellazione HDFS;
			returnJSONValue = deleteHDFSData(tenant,idDataset,datasetVersion);
		}
		return returnJSONValue;
	}

	private String deleteDatasetData(String tenant, String idDataset,String datasetVersion) throws UnknownHostException {
		mongo = MongoSingleton.getMongoClient();
		try{

			Long datasetVersionLng=null;
			if (null!=datasetVersion) datasetVersionLng=new Long(Long.parseLong(datasetVersion));
			DB db = mongo.getDB(Config.getInstance().getDbSupport());

			//recupero DatasetType
			DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
			BasicDBObject searchQuery = new BasicDBObject("idDataset", Long.parseLong(idDataset));
			if (null!=datasetVersionLng) searchQuery.put("datasetVersion",datasetVersionLng.longValue());

			DBObject datasetMetaData = col.findOne(searchQuery);
			Metadata metadataLoaded = Metadata.fromJson(JSON.serialize(datasetMetaData));

			DBCollection colTenant = db.getCollection(Config.getInstance().getCollectionSupportTenant());

			searchQuery = new BasicDBObject();
			searchQuery.put("tenantCode", tenant);

			String dataDb=null;
			String dataCollection=null;
			DBObject tenantInfo = colTenant.findOne(searchQuery);
			//Controllo il tipo del Dataset per interrogare la Mongo Collectio giusta
			if ("streamDataset".equals(metadataLoaded.getConfigData().getSubtype())) {
				dataDb=(String)tenantInfo.get("measuresCollectionDb");
				dataCollection=(String)tenantInfo.get("measuresCollectionName");
			} else if ("binaryDataset".equals(metadataLoaded.getConfigData().getSubtype())) {
				//TODO
				throw new Exception("invalid data type, cannot delete a mediaDataset data");
			} else if ("socialDataset".equals(metadataLoaded.getConfigData().getSubtype())) {
				dataDb=(String)tenantInfo.get("measuresCollectionDb");
				dataCollection=(String)tenantInfo.get("socialCollectionName");
			} else if ("bulkDataset".equals(metadataLoaded.getConfigData().getSubtype())) {
				dataDb=(String)tenantInfo.get("dataCollectionDb");
				dataCollection=(String)tenantInfo.get("dataCollectionName");
			}

			DBCollection colDati=null;
			if (db.getName().equals(dataDb)) colDati=db.getCollection(dataCollection);
			else {
				DB db1= mongo.getDB(dataDb);
				colDati=db1.getCollection(dataCollection);
			}

			searchQuery = new BasicDBObject("idDataset", Long.parseLong(idDataset));
			if (null!=datasetVersionLng) searchQuery.put("datasetVersion",datasetVersionLng.longValue());

			System.out.println("searchQuery: " + searchQuery);
			//Cancello i dati da Mongo
			WriteResult wr = null;
			wr = colDati.remove(searchQuery);

			System.out.println("wr: " + wr);
			
			
			// remove file list
			String supportDb = Config.getInstance().getDbSupport();
			String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
			MongoDBMetadataDAO metadataDAO = new MongoDBMetadataDAO(mongo, supportDb, supportDatasetCollection);

			Metadata existingMetadata = metadataDAO.readCurrentMetadataByIdDataset(Long.parseLong(idDataset),  null);
			existingMetadata.getInfo().setFileNames(new LinkedList<String>());
			metadataDAO.updateMetadata(existingMetadata);


		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse(KO_RESULT).toString();
		}
		return JSON.parse(OK_RESULT).toString();
	}
	
	private String deleteHDFSData(String tenant, String idDataset, String datasetVersion){
		String apiBaseUrl = "";
		String typeDirectory = "";
		String subTypeDirectory = "";
		String URL_DataDomain = "";
		
		SendHTMLEmail mailer = new SendHTMLEmail();
		
		try {
			DB db = mongo.getDB(Config.getInstance().getDbSupport());
			Long datasetVersionLng=null;
			if (null!=datasetVersion) datasetVersionLng=new Long(Long.parseLong(datasetVersion));
			//recupero DatasetType
			DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
			BasicDBObject searchQuery = new BasicDBObject("idDataset", Long.parseLong(idDataset));
			if (null != datasetVersionLng) searchQuery.put("datasetVersion",datasetVersionLng.longValue());

			DBObject datasetMetaData = col.findOne(searchQuery);
			Metadata metadataLoaded = Metadata.fromJson(JSON.serialize(datasetMetaData));
			
			//DBCollection colStream = db.getCollection(Config.getInstance().getCollectionSupportStream());
			BasicDBObject streamSearchQuery = new BasicDBObject("configData.idDataset", Long.parseLong(idDataset));
			if (null != datasetVersionLng) streamSearchQuery.put("configData.datasetVersion",datasetVersionLng.longValue());

			//Verifico il tipo di Dataset per creare il path corretto su HDFS
			if (metadataLoaded.getConfigData().getSubtype().equals("bulkDataset")){
				if (metadataLoaded.getInfo().getCodSubDomain() == null){
					System.out.println("CodSubDomain is null => " + metadataLoaded.getInfo().getCodSubDomain());
					typeDirectory = "db_" + metadataLoaded.getConfigData().getTenantCode();
				} else {
					System.out.println("CodSubDomain => " + metadataLoaded.getInfo().getCodSubDomain());
					typeDirectory = "db_" + metadataLoaded.getInfo().getCodSubDomain();
				}
				subTypeDirectory = metadataLoaded.getDatasetCode();

				System.out.println("typeDirectory => " + typeDirectory);
				System.out.println("subTypeDirectory => " + subTypeDirectory);
			} else if (metadataLoaded.getConfigData().getSubtype().equals("streamDataset")){
				DBObject streamMetaData = col.findOne(streamSearchQuery);
				StreamOut streamMetadataLoaded = StreamOut.fromJson(JSON.serialize(streamMetaData));
				typeDirectory = "so_" + streamMetadataLoaded.getStreams().getStream().getVirtualEntitySlug();
				subTypeDirectory = streamMetadataLoaded.getStreamCode();
			} else if (metadataLoaded.getConfigData().getSubtype().equals("socialDataset")){
				DBObject streamMetaData = col.findOne(streamSearchQuery);
				StreamOut streamMetadataLoaded = StreamOut.fromJson(JSON.serialize(streamMetaData));
				typeDirectory = "so_" + streamMetadataLoaded.getStreams().getStream().getVirtualEntitySlug();
				subTypeDirectory = streamMetadataLoaded.getStreamCode();
			}
			
			URL_DataDomain = metadataLoaded.getInfo().getDataDomain();
			
			apiBaseUrl = Config.getInstance().getApiKnoxSDNETUrl() + new String(tenant).toUpperCase() + "/rawdata/" + URL_DataDomain + "/" + typeDirectory + "/" + subTypeDirectory;
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet httpget = new HttpGet(apiBaseUrl + "?op=LISTSTATUS");
			//Settata la http GET, setto le credenziali di KNOX!
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(Config.getInstance().getKnoxSDNETUser(), Config.getInstance().getKnoxSDNETPwd()));
			 
			// Add AuthCache to the execution context
			final HttpClientContext context = HttpClientContext.create();
			context.setCredentialsProvider(credsProvider);
			 
			HttpResponse response = client.execute(httpget, context);
			log.debug("[SAML2ConsumerServlet::getAllTenants] call to " + apiBaseUrl + " - status " + response.getStatusLine().toString() + " - status Code " + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() == 404){
				mailer.Send404MailForClearDataset(apiBaseUrl);
			} else if (response.getStatusLine().getStatusCode() == 500){
				mailer.Send500MailForClearDataset(apiBaseUrl);
				return JSON.parse(KO_RESULT).toString();
			} else {
				//200 HTTP STATUS CODE
				StringBuilder out = new StringBuilder();
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					out.append(line);
				}

				String inputJson = out.toString();
				log.info("inputJson = " + inputJson);
				
				String json = inputJson.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null");
				Gson gson = JSonHelper.getInstance();
				POJOHdfs pojoHdfs = gson.fromJson(json, POJOHdfs.class);
				
				FileStatus[] hdfsPath = pojoHdfs.getFileStatuses().getFileStatus();
				for (int i = 0; i < hdfsPath.length; i++) {
					
					HttpDelete httpgetDel = null;
					if (null!=datasetVersion){
						if (hdfsPath[i].getPathSuffix().endsWith("-" + datasetVersion + ".csv")){
							httpgetDel = new HttpDelete(apiBaseUrl + "/" + hdfsPath[i].getPathSuffix() + "?op=DELETE");
						}
					} else {
						httpgetDel = new HttpDelete(apiBaseUrl + "/" + hdfsPath[i].getPathSuffix() + "?op=DELETE");
					}
					
					HttpResponse responseDel = client.execute(httpgetDel, context);
					
					if (responseDel.getStatusLine().getStatusCode() == 404){
						mailer.Send404MailForClearDataset(apiBaseUrl + "/" + hdfsPath[i].getPathSuffix());
					} else if (responseDel.getStatusLine().getStatusCode() == 500){
						mailer.Send500MailForClearDataset(apiBaseUrl + "/" + hdfsPath[i].getPathSuffix());
						return JSON.parse(KO_RESULT).toString();
					} else {
						//200 HTTP STATUS CODE
						mailer.Send200MailForClearDataset(apiBaseUrl + "/" + hdfsPath[i].getPathSuffix());
					}
				}
			}
		} catch (Exception e) {
			log.error("[SAML2ConsumerServlet::getAllTenants] - ERROR " + e.getMessage());
			mailer.Send500MailForClearDataset(apiBaseUrl + ". E' stata riscontrata la seguente eccezione: " + e.getMessage() + "\n" + e.getStackTrace());
			e.printStackTrace();
			return JSON.parse(KO_RESULT).toString();
		}
		return JSON.parse(OK_RESULT).toString();
	}

	@POST
	@Path("/insertFromStream")
	@Produces(MediaType.APPLICATION_JSON)
	public String createDataset(final String datasetInput) throws UnknownHostException {

		mongo = MongoSingleton.getMongoClient();

		Gson gson = JSonHelper.getInstance();
		log.info("Json Mapping");
		// match @nil elements
		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null");
		try {
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if (pojoStreams != null && pojoStreams.getStreams() != null && pojoStreams.getStreams().getStream() != null) {

				Stream newStream = pojoStreams.getStreams().getStream();
				DB db = mongo.getDB(Config.getInstance().getDbSupport());

				// Update of the streams
				DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportStream());

				DBObject findStream = new BasicDBObject();
				DBObject sortStream = new BasicDBObject();
				findStream.put("idStream", newStream.getIdStream());
				sortStream.put("_id", -1);

				DBCursor cursor = col.find(findStream).sort(sortStream);
				

				Long idDataset = null;
				DBObject oldObjStream = null;
//				Stream oldStream = null;
				if (cursor.hasNext()) {

					oldObjStream = cursor.next();
//					POJOStreams pojoOldStreams = gson.fromJson(oldObjStream.toString(), POJOStreams.class);
//					oldStream = pojoOldStreams.getStreams().getStream();

					col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
					DBObject configData = (DBObject) oldObjStream.get("configData");
					idDataset = ((Number) configData.get("idDataset")).longValue();

					BasicDBObject findDatasets = new BasicDBObject();
					findDatasets.put("idDataset", idDataset);
					DBObject updateConfig = new BasicDBObject();
					updateConfig.put("$set", new BasicDBObject("configData.current", 0));
					col.update(findDatasets, updateConfig, false, true);
				}
				
				col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
				Metadata myMeta = MetadataFiller.fillMetadata(newStream);

				// myMeta get persisted on db and returns the object with the id
				// updated
				log.info("Saving Metadata for Stream");
				new MongoDBMetadataDAO(mongo, Config.getInstance().getDbSupport(), Config.getInstance().getCollectionSupportDataset()).createMetadata(myMeta, idDataset);

				// Insert Api only for new streams not for updates
				if (oldObjStream == null) {
					log.info("Saving Api for Stream");
					MyApi api = APIFiller.fillApi(newStream, myMeta);
					log.info(gson.toJson(api, MyApi.class));
					col = db.getCollection(Config.getInstance().getCollectionSupportApi());
					DBObject apiObject = (DBObject) JSON.parse(gson.toJson(api, MyApi.class));
					apiObject.removeField("id");
					insertDocumentWithKey(col, apiObject, "idApi", MAX_RETRY);
				}

				StreamOut strOut = StreamFiller.fillStream(newStream, myMeta.getIdDataset());
				log.info(gson.toJson(strOut, StreamOut.class));
				log.info(gson.toJson(myMeta, Metadata.class));

				// stream gets the idStream from the Json
				col = db.getCollection(Config.getInstance().getCollectionSupportStream());
				DBObject dbObject = (DBObject) JSON.parse(gson.toJson(strOut, StreamOut.class));

				DBObject uniqueStream = new BasicDBObject("idStream", strOut.getIdStream());
				uniqueStream.put("streams.stream.deploymentVersion", strOut.getStreams().getStream().getDeploymentVersion());

				dbObject.removeField("id");
				// if the stream with that id and version exists .. update it,
				// otherwise insert the new one.
				// upsert:true multi:false
				col.update(uniqueStream, dbObject, true, false);

				// Create api in the store
				String apiName = "";
				//Boolean updateOperation = false;
				try {
					// Insert
					apiName = StoreService.createApiforStream(newStream, myMeta.getDatasetCode(), false, json);
				} catch (Exception duplicate) {
					if (duplicate.getMessage().toLowerCase().contains("duplicate")) {
						// Update
						apiName = StoreService.createApiforStream(newStream, myMeta.getDatasetCode(), true, json);
						//updateOperation = true;
					} else
						throw duplicate;
				}
				/*if (!updateOperation){
					//L'operazione di Publish deve essere eseguita solo alla prima installazione
					if (newStream.getPublishStream() != 0) {
						StoreService.publishStore("1.0", apiName, "admin");
						Set<String> tenantSet = new TreeSet<String>();
						
						CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(Config.getInstance().getStoreUsername(), Config.getInstance().getStorePassword());
						if (newStream.getTenantssharing() != null) {
							
							for (Tenantsharing tenantSh : newStream.getTenantssharing().getTenantsharing()) {
								tenantSet.add(tenantSh.getTenantCode());
								String appName = "userportal_" + tenantSh.getTenantCode();
								
								SubscriptionAPIResponse listSubscriptions = ApiManagerFacade.listSubscription(httpClient, appName);
								ApiManagerFacade.subscribeApi(httpClient, apiName, appName);
								
								//StoreService.addSubscriptionForTenant(apiName, appName);
							}
						}
						if (!tenantSet.contains(newStream.getCodiceTenant())) {
							String appName = "userportal_" + newStream.getCodiceTenant();
							ApiManagerFacade.subscribeApi(httpClient, apiName, appName);
							
							//StoreService.addSubscriptionForTenant(apiName, appName);
						}
					}
				}*/
				try {
					StoreService.publishStore("1.0", apiName, "admin");
					CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(Config.getInstance().getStoreUsername(), Config.getInstance().getStorePassword());
					ApiManagerFacade.updateStreamSubscriptionIntoStore(httpClient, newStream.getVisibility(), newStream, apiName);
				} catch (Exception e) {
					log.error("[MetadataService::createMetadata] - ERROR in publish Api in store - message: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse(KO_RESULT).toString();
		}
		return JSON.parse(OK_RESULT).toString();
	}
	
	@DELETE
	@Path("/requestUninstallDataset/{tenant}/{idDataset}")
	@Produces(MediaType.APPLICATION_JSON)
	public String requestUninstallDataset(@PathParam("tenant") String tenant, @PathParam("idDataset") String idDataset) throws UnknownHostException {
		String datasetOuput = JSON.parse("{KO:1}").toString();
		mongo = MongoSingleton.getMongoClient();
		try{
			DB db = mongo.getDB(Config.getInstance().getDbSupport());

			//recupero DatasetType
			DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportDataset());
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("idDataset", Long.parseLong(idDataset));
			searchQuery.put("configData.current", 1);
			searchQuery.put("configData.tenantCode", tenant);
			
			DBCursor cursor = col.find(searchQuery);
			if (cursor.hasNext()) {
				DBObject updateConfig = new BasicDBObject();

				updateConfig.put("$set", new BasicDBObject("configData.current", 0));
				updateConfig.put("$set", new BasicDBObject("configData.deleted", 1));

				col.update(searchQuery, updateConfig, false, true);

				DBCollection colApi = db.getCollection(Config.getInstance().getCollectionSupportApi());

				BasicDBObject findApis = new BasicDBObject();
				findApis.put("dataset.idDataset", idDataset);
				
				DBObject configDataUpdate = new BasicDBObject();
				configDataUpdate.put("$set", new BasicDBObject("configData.deleted", 1));
				
				colApi.update(findApis, configDataUpdate, false, true);

				DBCursor datasets = col.find(searchQuery);
				while (datasets.hasNext()) {
					String apiName = (String) datasets.next().get("datasetCode");
					if (apiName != null) {
						apiName += "_odata";
						try {
							StoreService.removeStore("1.0", apiName, "admin");
						} catch (Exception ex) {
							log.info("Impossible to remove " + apiName + ex.getMessage());
						}
					}
					
					datasetOuput = JSON.parse(OK_RESULT).toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			datasetOuput = JSON.parse(KO_RESULT).toString();
		}
		return datasetOuput;
		
	}

	@POST
	@Path("/deleteDatasetLogically")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteDatasetLogically(final String datasetInput) throws UnknownHostException {

		mongo = MongoSingleton.getMongoClient();

		Gson gson = JSonHelper.getInstance();
		log.info("Json Mapping");
		// match @nil elements
		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null");
		try {
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if (pojoStreams != null && pojoStreams.getStreams() != null && pojoStreams.getStreams().getStream() != null) {

				Stream newStream = pojoStreams.getStreams().getStream();
				DB db = mongo.getDB(Config.getInstance().getDbSupport());

				// Update of the streams

				DBCollection colStream = db.getCollection(Config.getInstance().getCollectionSupportStream());

				DBObject findStream = new BasicDBObject();
				findStream.put("idStream", newStream.getIdStream());

				DBCursor cursor = colStream.find(findStream);

				Long idDataset = null;
				DBObject oldStream = null;
				if (cursor.hasNext()) {

					oldStream = cursor.next();

					DBObject configData = (DBObject) oldStream.get("configData");

					DBObject configDataUpdate = new BasicDBObject();
					configDataUpdate.put("$set", new BasicDBObject("configData.deleted", 1));
					colStream.update(findStream, configDataUpdate, false, true);

					BasicDBObject findDatasets = new BasicDBObject();

					idDataset = ((Number) configData.get("idDataset")).longValue();
					findDatasets.put("idDataset", idDataset);

					DBObject updateConfig = new BasicDBObject();

					updateConfig.put("$set", new BasicDBObject("configData.current", 0));
					updateConfig.put("$set", new BasicDBObject("configData.deleted", 1));

					DBCollection colDataset = db.getCollection(Config.getInstance().getCollectionSupportDataset());

					colDataset.update(findDatasets, updateConfig, false, true);

					DBCollection colApi = db.getCollection(Config.getInstance().getCollectionSupportApi());

					BasicDBObject findApis = new BasicDBObject();
					findApis.put("dataset.idDataset", idDataset);

					colApi.update(findApis, configDataUpdate, false, true);

					DBCursor datasets = colDataset.find(findDatasets);
					while (datasets.hasNext()) {
						String apiName = (String) datasets.next().get("datasetCode");
						if (apiName != null) {
							apiName += "_odata";
							try {
								StoreService.removeStore("1.0", apiName, "admin");
							} catch (Exception ex) {
								log.info("Impossible to remove " + apiName + ex.getMessage());
							}
						}
						break;
					}
				}
				String tenant = newStream.getCodiceTenant();
				String sensor = newStream.getCodiceVirtualEntity();
				String stream = newStream.getCodiceStream();
				String apiName = tenant + "." + sensor + "_" + stream + "_stream";
				try {
					StoreService.removeStore("1.0", apiName, "admin");
				} catch (Exception ex) {
					log.info("Impossible to remove " + apiName + ex.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse(KO_RESULT).toString();
		}

		return JSON.parse(OK_RESULT).toString();
	}

	private static Long insertDocumentWithKey(DBCollection col, DBObject obj, String key, Integer maxRetry) throws Exception {
		Long id = 0L;
		try {
			BasicDBObject sortobj = new BasicDBObject();
			sortobj.append(key, -1);
			DBObject doc = col.find().sort(sortobj).limit(1).one();
			log.info(doc);
			if (doc != null && doc.get(key) != null)
				id = ((Number) doc.get(key)).longValue() + 1;
			else {
				id = 1L;
			}
			obj.put(key, id);
			col.insert(obj);
		} catch (Exception e) {
			if (maxRetry > 0) {
				return insertDocumentWithKey(col, obj, key, --maxRetry);
			} else {
				throw e;
			}
		}
		return id;
	}
}
