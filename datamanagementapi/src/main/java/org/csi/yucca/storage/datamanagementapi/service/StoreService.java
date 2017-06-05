package org.csi.yucca.storage.datamanagementapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.AddStream;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.PublishApi;
import org.csi.yucca.storage.datamanagementapi.model.cache.TenantCache;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.metadata.SearchEngineMetadata;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Tag;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Tenantsharing;
import org.csi.yucca.storage.datamanagementapi.singleton.CloudSolrSingleton;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.KnoxSolrSingleton;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/store")
public class StoreService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(StoreService.class);

	public static int API_FIELD_MAX_LENGTH = 600;

	@POST
	@Path("/apiCreateApiStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiCreateApiStore(final String datasetInput) throws UnknownHostException {

		Gson gson = JSonHelper.getInstance();
		// match @nil elements
		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null");
		try {
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if (pojoStreams != null && pojoStreams.getStreams() != null && pojoStreams.getStreams().getStream() != null) {

				Stream newStream = pojoStreams.getStreams().getStream();

				// Aggiungi Stream allo store
				String tenant = newStream.getCodiceTenant();
				String sensor = newStream.getCodiceVirtualEntity();
				String stream = newStream.getCodiceStream();

				String apiName = tenant + "." + sensor + "_" + stream;
				try {
					createApiforStream(newStream, apiName, false, json);
				} catch (Exception duplicate) {
					if (duplicate.getMessage().toLowerCase().contains("duplicate")) {
						createApiforStream(newStream, apiName, true, json);
					} else
						throw duplicate;
				}

				/*
				 * if (newStream.getPublishStream() != 0) { publishStore("1.0",
				 * apiName, "admin"); Set<String> tenantSet = new
				 * TreeSet<String>(); if (newStream.getTenantssharing() != null)
				 * { for (Tenantsharing tenantSh :
				 * newStream.getTenantssharing().getTenantsharing()) {
				 * tenantSet.add(tenantSh.getTenantCode()); String appName =
				 * "userportal_" + tenantSh.getTenantCode();
				 * StoreService.addSubscriptionForTenant(apiName, appName); } }
				 * if (!tenantSet.contains(newStream.getCodiceTenant())) {
				 * String appName = "userportal_" + newStream.getCodiceTenant();
				 * StoreService.addSubscriptionForTenant(apiName, appName); } }
				 */

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
			return JSON.parse("{KO:1}").toString();
		}

		return JSON.parse("{OK:1}").toString();
	}

	@POST
	@Path("/apiCreateStreamStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiCreateStreamStore(final String datasetInput) throws UnknownHostException {

		Gson gson = JSonHelper.getInstance();
		MongoClient mongo = MongoSingleton.getMongoClient();
		// match @nil elements
		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null");
		try {
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if (pojoStreams != null && pojoStreams.getStreams() != null && pojoStreams.getStreams().getStream() != null) {

				Stream newStream = pojoStreams.getStreams().getStream();

				// Aggiungi Stream allo store
				String tenant = newStream.getCodiceTenant();
				String sensor = newStream.getCodiceVirtualEntity();
				String stream = newStream.getCodiceStream();
				
				log.info(";;;;;CHECK SAVE DATA -->"+newStream.getSaveData() + "("+newStream.getIdStream()+")");
if (newStream.getSaveData()==1) {
				DB db = mongo.getDB(Config.getInstance().getDbSupport());
				//FC - SOLR datasetCode
				DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportStream());
DBObject findStream = new BasicDBObject();
				DBObject sortStream = new BasicDBObject();
				findStream.put("idStream", newStream.getIdStream());
				sortStream.put("_id", -1);

				DBCursor cursor = col.find(findStream).sort(sortStream);
                
                Long idDataset = null;
				DBObject oldObjStream = null;
				// Stream oldStream = null;
				if (cursor.hasNext()) {

					oldObjStream = cursor.next();

					DBObject configData = (DBObject) oldObjStream.get("configData");
					idDataset = ((Number) configData.get("idDataset")).longValue();
					log.info(";;;;;FOUND "+idDataset);

				}  				
				newStream.setIdDataset(idDataset);
}
				try {
					createStream(newStream, false, json);
				} catch (Exception duplicate) {
					log.error("Error on createStream (maybe duplicate...)", duplicate);
					if (duplicate.getMessage().toLowerCase().contains("duplicate")) {
						createStream(newStream, true, json);
					} else
						throw duplicate;
				}
				String apiName = tenant + "." + sensor + "_" + stream + "_stream";
				/*
				 * if (newStream.getPublishStream() != 0) { publishStore("1.0",
				 * apiName, "admin"); newStream.getv Set<String> tenantSet = new
				 * TreeSet<String>(); if (newStream.getTenantssharing() != null)
				 * { for (Tenantsharing tenantSh :
				 * newStream.getTenantssharing().getTenantsharing()) {
				 * tenantSet.add(tenantSh.getTenantCode()); String appName =
				 * "userportal_" + tenantSh.getTenantCode();
				 * StoreService.addSubscriptionForTenant(apiName, appName); } }
				 * if (!tenantSet.contains(newStream.getCodiceTenant())) {
				 * String appName = "userportal_" + newStream.getCodiceTenant();
				 * StoreService.addSubscriptionForTenant(apiName, appName); } }
				 */
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
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}

	/*
	 * public static boolean addSubscriptionForTenant(String apiName, String
	 * appName) throws Exception {
	 * 
	 * QSPStore subscription = new QSPStore();
	 * 
	 * subscription.setVar("apimanConsoleAddress",
	 * Config.getInstance().getConsoleAddress());
	 * subscription.setVar("username", Config.getInstance().getStoreUsername());
	 * subscription.setVar("password", Config.getInstance().getStorePassword());
	 * subscription.setVar("httpok", Config.getInstance().getHttpOk());
	 * subscription.setVar("ok", Config.getInstance().getResponseOk());
	 * 
	 * subscription.setVar("apiVersion", "1.0"); subscription.setVar("apiName",
	 * apiName); subscription.setVar("appName", appName);
	 * subscription.setVar("P", "");
	 * 
	 * subscription.run();
	 * 
	 * return true; }
	 */

	public static String createApiforStream(Stream newStream, String apiName, boolean update, String json) throws Exception {

		String apiFinalName = apiName + "_odata";

		AddStream objStream = new AddStream();
		objStream.setProperties(update);

		// ImageProcessor processor = new ImageProcessor();
		// String imageBase64 = newStream.getStreamIcon();

		// String path = "images/";
		// String fileName = newStream.getCodiceStream() + ".png";

		// boolean addTwitter = newStream.getIdTipoVe() ==
		// Constants.VIRTUAL_ENTITY_TWITTER_TYPE_ID;
		// processor.doProcessOdata(imageBase64, path, fileName, addTwitter);

		// FIXME get the list of roles(tenants) from the stream info
		if ("public".equals(newStream.getVisibility())) {
			objStream.setVar("visibility", "public");
			objStream.setVar("roles", "");
			objStream.setVar("authType", "None");

		} else {
			objStream.setVar("visibility", "restricted");
			String ruoli = "";

			if (newStream.getTenantssharing() != null && newStream.getTenantssharing().getTenantsharing() != null) {
				for (Tenantsharing t : newStream.getTenantssharing().getTenantsharing()) {
					if (!ruoli.equals(""))
						ruoli += ",";
					ruoli += t.getTenantCode() + "_subscriber";
				}
			}
			if (!ruoli.contains(newStream.getCodiceTenant() + "_subscriber")) {
				ruoli += newStream.getCodiceTenant() + "_subscriber";
			}

			objStream.setVar("roles", ruoli);
			objStream.setVar("authType", "Application & Application User");
		}

		if (update) {
			objStream.setVar("actionAPI", "updateAPI");
		} else {
			objStream.setVar("actionAPI", "addAPI");
		}

		objStream.setVar("apimanConsoleAddress", Config.getInstance().getConsoleAddress());
		objStream.setVar("username", Config.getInstance().getStoreUsername());
		objStream.setVar("password", Config.getInstance().getStorePassword());
		objStream.setVar("httpok", Config.getInstance().getHttpOk());
		objStream.setVar("ok", Config.getInstance().getResponseOk());

		// objStream.setVar("icon", path + fileName);
		objStream.setVar("apiVersion", "1.0");
		objStream.setVar("apiName", apiFinalName);
		objStream.setVar("context", "/api/" + apiName);
		objStream.setVar("P", "");
		objStream.setVar("endpoint", Config.getInstance().getBaseApiUrl() + apiName);
		objStream.setVar("desc", newStream.getNomeStream() != null ? Util.safeSubstring(newStream.getNomeStream(), API_FIELD_MAX_LENGTH) : "");
		objStream.setVar("copiright", newStream.getCopyright() != null ? newStream.getCopyright() : "");

		objStream.setVar("extra_isApi", "false");
		objStream.setVar("extra_apiDescription", newStream.getVirtualEntityName() != null ? Util.safeSubstring(newStream.getVirtualEntityName(), API_FIELD_MAX_LENGTH) : "");
		objStream.setVar("codiceTenant", newStream.getCodiceTenant() != null ? newStream.getCodiceTenant() : "");
		objStream.setVar("codiceStream", newStream.getCodiceStream() != null ? newStream.getCodiceStream() : "");
		objStream.setVar("nomeStream", newStream.getNomeStream() != null ? newStream.getNomeStream() : "");
		objStream.setVar("nomeTenant", newStream.getNomeTenant() != null ? newStream.getNomeTenant() : "");
		objStream.setVar("licence", newStream.getLicence() != null ? Util.safeSubstring(newStream.getLicence(), API_FIELD_MAX_LENGTH) : "");
		objStream.setVar("disclaimer", newStream.getDisclaimer() != null ? Util.safeSubstring(newStream.getDisclaimer(), API_FIELD_MAX_LENGTH) : "");

		objStream.setVar("virtualEntityCode", newStream.getCodiceVirtualEntity() != null ? newStream.getCodiceVirtualEntity() : "");
		objStream.setVar("virtualEntityName", newStream.getVirtualEntityName() != null ? newStream.getVirtualEntityName() : "");
		objStream.setVar("virtualEntityDescription",
				newStream.getVirtualEntityDescription() != null ? Util.safeSubstring(newStream.getVirtualEntityDescription(), API_FIELD_MAX_LENGTH) : "");
		String tags = "";

		if (newStream.getDomainStream() != null) {
			tags += newStream.getDomainStream();
		}
		if (newStream.getStreamTags() != null && newStream.getStreamTags().getTag() != null) {
			for (Tag t : newStream.getStreamTags().getTag()) {
				tags += "," + t.getTagCode();
			}
		}

		objStream.setVar("tags", Util.safeSubstring(tags, API_FIELD_MAX_LENGTH));

		// DT Add document
		//String datasetInput = extractContentForDocument(json,newStream.getCodiceTenant() != null ? newStream.getCodiceTenant() : "");
		
		//SOLR --> NOT USED, DOC FOR STREAM ARE CREATED IN createStream 
		//objStream.setVar("content", datasetInput);

		objStream.run();

		return apiFinalName;
	}

	private static String extractContentForDocument(String json, String tenantCode) {
		Gson gson = JSonHelper.getInstance();
		POJOStreams pojoStreams2 = gson.fromJson(json, POJOStreams.class);
		pojoStreams2.getStreams().getStream().setStreamIcon("");

		if (pojoStreams2.getStreams().getStream().getStreamTags() != null) {
			// check all tags
			try {
				for (String lang : Constants.LANGUAGES_SUPPORTED) {
					ResourceBundle messages = getMessages(lang);
					for (Tag tag : pojoStreams2.getStreams().getStream().getStreamTags().getTag()) {
						if (messages.getString(tag.getTagCode()) == null)
							throw new Exception("Tag " + tag.getTagCode() + " not found");
					}

					if (pojoStreams2.getStreams().getStream().getDomainStream() != null) {
						if (messages.getString(pojoStreams2.getStreams().getStream().getDomainStream()) == null)
							throw new Exception("Domain " + pojoStreams2.getStreams().getStream().getDomainStream() + " not found");
					}

					if (pojoStreams2.getStreams().getStream().getCodSubDomain() != null) {
						if (messages.getString(pojoStreams2.getStreams().getStream().getCodSubDomain()) == null)
							throw new Exception("Subdomain " + pojoStreams2.getStreams().getStream().getCodSubDomain() + " not found");
					}
				}

			} catch (Exception e) {
				log.debug("Tag not found" + e.getMessage());
				messagesMap = new HashMap<String, ResourceBundle>();
			}

			Map<String, List<String>> tagsTranslated = new HashMap<String, List<String>>();
			Map<String, String> domainTranslated = new HashMap<String, String>();
			Map<String, String> subDomainTranslated = new HashMap<String, String>();
			for (String lang : Constants.LANGUAGES_SUPPORTED) {
				ResourceBundle messages = getMessages(lang);

				List<String> translatedTags = new LinkedList<String>();
				for (Tag tag : pojoStreams2.getStreams().getStream().getStreamTags().getTag()) {
					translatedTags.add(messages.getString(tag.getTagCode()));
				}
				tagsTranslated.put(lang, translatedTags);
				pojoStreams2.getStreams().getStream().setTagsTranslated(tagsTranslated);

				String translatedDomain = "";
				if (pojoStreams2.getStreams().getStream().getDomainStream() != null)
					translatedDomain = messages.getString(pojoStreams2.getStreams().getStream().getDomainStream());

				domainTranslated.put(lang, translatedDomain);
				pojoStreams2.getStreams().getStream().setDomainTranslated(domainTranslated);

				String translatedSubDomain = "";
				if (pojoStreams2.getStreams().getStream().getCodSubDomain() != null)
					translatedSubDomain = messages.getString(pojoStreams2.getStreams().getStream().getCodSubDomain());

				subDomainTranslated.put(lang, translatedSubDomain);
				pojoStreams2.getStreams().getStream().setSubDomainTranslated(subDomainTranslated);

			}

		}
		
		if (null!=tenantCode && tenantCode.trim().length()>0) {
			pojoStreams2.getStreams().getStream().setOrganizationCode((getTenantOrganizaionMap(tenantCode).get(tenantCode)).getOrganizationCode());
			pojoStreams2.getStreams().getStream().setOrganizationDescription(       (getTenantOrganizaionMap(tenantCode).get(tenantCode)).getOrganizationDescription()    );
			pojoStreams2.getStreams().getStream().setTenantName(    (getTenantOrganizaionMap(tenantCode).get(tenantCode)).getTenantName()      );
			pojoStreams2.getStreams().getStream().setTenantDescription(    (getTenantOrganizaionMap(tenantCode).get(tenantCode)).getTenantDescription()      );
		}

		return gson.toJson(pojoStreams2);
	}

	private static String extractMetadataContentForDocument(Metadata metadata ,String tenantCode) {
		Gson gson = JSonHelper.getInstance();
		//Metadata metadata = Metadata.fromJson(jsonMetadata);

		if (metadata.getInfo().getTags() != null) {
			Map<String, List<String>> tagsTranslated = new HashMap<String, List<String>>();
			Map<String, String> domainTranslated = new HashMap<String, String>();
			Map<String, String> subDomainTranslated = new HashMap<String, String>();
			// check all tags
			try {
				for (String lang : Constants.LANGUAGES_SUPPORTED) {
					ResourceBundle messages = getMessages(lang);
					for (org.csi.yucca.storage.datamanagementapi.model.metadata.Tag tag : metadata.getInfo().getTags()) {
						if (messages.getString(tag.getTagCode()) == null)
							throw new Exception("Tag " + tag.getTagCode() + " not found");
					}

					if (metadata.getInfo().getDataDomain()  != null) {
						if (messages.getString(metadata.getInfo().getDataDomain() ) == null)
							throw new Exception("Domain " + metadata.getInfo().getDataDomain() + " not found");
					}

					if (metadata.getInfo().getCodSubDomain() != null) {
						if (messages.getString(metadata.getInfo().getCodSubDomain()) == null)
							throw new Exception("Subdomain " + metadata.getInfo().getCodSubDomain() + " not found");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.debug("Tag not found" + e.getMessage());
				messagesMap = new HashMap<String, ResourceBundle>();
			}

			for (String lang : Constants.LANGUAGES_SUPPORTED) {
				ResourceBundle messages = getMessages(lang);
				List<String> translatedTags = new LinkedList<String>();
				for (org.csi.yucca.storage.datamanagementapi.model.metadata.Tag tag : metadata.getInfo().getTags()) {
					translatedTags.add(messages.getString(tag.getTagCode()));
				}
//				if (metadata.getInfo().getDataDomain() != null)
//					translatedTags.add(messages.getString(metadata.getInfo().getDataDomain()));

				tagsTranslated.put(lang, translatedTags);
				metadata.getInfo().setTagsTranslated(tagsTranslated);

				String translatedDomain = "";
				if (metadata.getInfo().getDataDomain() != null)
					translatedDomain = messages.getString(metadata.getInfo().getDataDomain());

				domainTranslated.put(lang, translatedDomain);
				metadata.getInfo().setDomainTranslated(domainTranslated);

				String translatedSubDomain = "";
				if (metadata.getInfo().getCodSubDomain() != null)
					translatedSubDomain = messages.getString(metadata.getInfo().getCodSubDomain());

				subDomainTranslated.put(lang, translatedSubDomain);
				metadata.getInfo().setSubDomainTranslated(subDomainTranslated);

			}
		}

		
		
		metadata.getConfigData().setOrganizationCode((getTenantOrganizaionMap(tenantCode).get(tenantCode)).getOrganizationCode());
		metadata.getConfigData().setOrganizationDescription(       (getTenantOrganizaionMap(tenantCode).get(tenantCode)).getOrganizationDescription()    );
		metadata.getConfigData().setTenantName(    (getTenantOrganizaionMap(tenantCode).get(tenantCode)).getTenantName()      );
		metadata.getConfigData().setTenantDescription(    (getTenantOrganizaionMap(tenantCode).get(tenantCode)).getTenantDescription()      );
		
		
		

		return gson.toJson(metadata);

	}

	public static String createApiforBulk(Metadata metadata, boolean update, String jsonFile) throws Exception {
		String apiFinalName=null; 
		try {

		String apiName = metadata.getDatasetCode();
		apiFinalName = metadata.getDatasetCode() + "_odata";

		AddStream addStream = new AddStream();
		addStream.setProperties(update);

		// ImageProcessor processor = new ImageProcessor();
		// String imageBase64 = metadata.getInfo().getIcon();

		// String path = "images/";
		// String fileName = metadata.getDatasetCode() + ".png";

		// boolean addTwitter = false;
		// if (metadata.getConfigData() != null &&
		// metadata.getConfigData().getSubtype() ==
		// Metadata.CONFIG_DATA_SUBTYPE_SOCIAL_DATASET)
		// addTwitter = true;

		// processor.doProcessOdata(imageBase64, path, fileName, addTwitter);

		// FIXME get the list of roles(tenants) from the stream info
		if ("public".equals(metadata.getInfo().getVisibility())) {
			addStream.setVar("visibility", "public");
			addStream.setVar("roles", "");
			addStream.setVar("authType", "None");
		} else {
			addStream.setVar("visibility", "restricted");

			String ruoli = "";

			if (metadata.getInfo().getTenantssharing() != null && metadata.getInfo().getTenantssharing().getTenantsharing() != null) {
				for (org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantsharing t : metadata.getInfo().getTenantssharing().getTenantsharing()) {
					if (!ruoli.equals(""))
						ruoli += ",";
					ruoli += t.getTenantCode() + "_subscriber";
				}
			}

			if (!ruoli.contains(metadata.getConfigData().getTenantCode() + "_subscriber")) {
				ruoli += metadata.getConfigData().getTenantCode() + "_subscriber";
			}

			addStream.setVar("roles", ruoli);
			addStream.setVar("authType", "Application & Application User");
		}

		if (update) {
			addStream.setVar("actionAPI", "updateAPI");
		} else {
			addStream.setVar("actionAPI", "addAPI");
		}

		addStream.setVar("apimanConsoleAddress", Config.getInstance().getConsoleAddress());
		addStream.setVar("username", Config.getInstance().getStoreUsername());
		addStream.setVar("password", Config.getInstance().getStorePassword());
		addStream.setVar("httpok", Config.getInstance().getHttpOk());
		addStream.setVar("ok", Config.getInstance().getResponseOk());

		// addStream.setVar("icon", path + fileName);
		addStream.setVar("apiVersion", "1.0");
		addStream.setVar("apiName", apiFinalName);
		addStream.setVar("context", "/api/" + apiName);// ds_Voc_28;
		addStream.setVar("P", "");
		addStream.setVar("endpoint", Config.getInstance().getBaseApiUrl() + apiName);
		addStream.setVar("desc", metadata.getInfo().getDescription() != null ? Util.safeSubstring(metadata.getInfo().getDescription(), API_FIELD_MAX_LENGTH) : "");
		addStream.setVar("copiright", metadata.getInfo().getCopyright() != null ? Util.safeSubstring(metadata.getInfo().getCopyright(), API_FIELD_MAX_LENGTH) : "");

		addStream.setVar("extra_isApi", "false");
		addStream.setVar("extra_apiDescription", metadata.getInfo().getDatasetName() != null ? metadata.getInfo().getDatasetName() : "");
		addStream.setVar("codiceTenant", metadata.getConfigData().getTenantCode() != null ? metadata.getConfigData().getTenantCode() : "");
		addStream.setVar("codiceStream", "");
		addStream.setVar("nomeStream", "");
		addStream.setVar("nomeTenant", metadata.getConfigData().getTenantCode() != null ? metadata.getConfigData().getTenantCode() : "");
		addStream.setVar("licence", metadata.getInfo().getLicense() != null ? Util.safeSubstring(metadata.getInfo().getLicense(), API_FIELD_MAX_LENGTH) : "");
		addStream.setVar("disclaimer", metadata.getInfo().getDisclaimer() != null ? Util.safeSubstring(metadata.getInfo().getDisclaimer(), API_FIELD_MAX_LENGTH) : "");
		addStream.setVar("virtualEntityName", "");
		addStream.setVar("virtualEntityDescription", "");

		String tags = "";

		if (metadata.getInfo().getDataDomain() != null) {
			tags += metadata.getInfo().getDataDomain();
		}
		List<String> tagCodes = null;
		if (metadata.getInfo().getTags() != null) {
			tagCodes = new LinkedList<String>();
			for (org.csi.yucca.storage.datamanagementapi.model.metadata.Tag t : metadata.getInfo().getTags()) {
				tags += "," + t.getTagCode();
				tagCodes.add(t.getTagCode());
			}
		}

		addStream.setVar("tags", Util.safeSubstring(tags, API_FIELD_MAX_LENGTH));

		// DT Add document ? Why restart from jsonFile? we lost init
		//String contentJson = extractMetadataContentForDocument(jsonFile,metadata.getConfigData().getTenantCode() != null ? metadata.getConfigData().getTenantCode() : "");
		String contentJson = extractMetadataContentForDocument(metadata,metadata.getConfigData().getTenantCode() != null ? metadata.getConfigData().getTenantCode() : "");
		
		
		//SOLR
		//addStream.setVar("content", contentJson);
		Metadata metadatan = Metadata.fromJson(contentJson);
		metadatan.setDatasetCode(metadata.getDatasetCode());
		SearchEngineMetadata newdocument = new SearchEngineMetadata();
		newdocument.setupEngine(metadatan);
		Gson gson = JSonHelper.getInstance();
		String newJsonDoc= gson.toJson(newdocument);

		
		
//		CloudSolrClient solrServer =  CloudSolrSingleton.getServer();
//		solrServer.setDefaultCollection(Config.getInstance().getSolrCollection());
		SolrInputDocument doc = newdocument.getSolrDocument();
		
		 
		if ("KNOX".equalsIgnoreCase(Config.getInstance().getSolrTypeAccess()))
		{
			SolrClient solrServer= null;
			solrServer = KnoxSolrSingleton.getServer();
			log.info("[StoreService::createApiForBulk] - --KNOX------" + doc.toString());
			log.info("[StoreService::createApiForBulk] - --user------" + Config.getInstance().getSolrUsername());
			log.info("[StoreService::createApiForBulk] - --pwd------" + Config.getInstance().getSolrPassword());
			log.info("[StoreService::createApiForBulk] - --collection------" + Config.getInstance().getSolrCollection());
			
 
			
			solrServer.add(Config.getInstance().getSolrCollection(),doc);
			//solrServer.add(doc);
			solrServer.commit();
		}
		else {
			CloudSolrClient solrServer = CloudSolrSingleton.getServer();
			solrServer.setDefaultCollection(Config.getInstance().getSolrCollection());
			log.info("[StoreService::createApiForBulk] - ---------------------" + doc.toString());
			solrServer.add(Config.getInstance().getSolrCollection(),doc);
			solrServer.commit();
		}
		
		
		addStream.run();

		} catch (Exception e) {
			log.info("[StoreService::createApiForBulk] ERROREEEEE ");
			e.printStackTrace();throw e;
		}

		return apiFinalName;
	}

	private static boolean createStream(Stream newStream, boolean update, String json) throws Exception {

		String tenant = newStream.getCodiceTenant();
		String sensor = newStream.getCodiceVirtualEntity();
		String stream = newStream.getCodiceStream();

		AddStream addStream = new AddStream();
		addStream.setProperties(update);

		// ImageProcessor processor = new ImageProcessor();
		// String imageBase64 = newStream.getStreamIcon();
		// String path = "images/";
		// String fileName = newStream.getCodiceStream() + ".png";
		// boolean addTwitter = newStream.getIdTipoVe() ==
		// Constants.VIRTUAL_ENTITY_TWITTER_TYPE_ID;
		// processor.doProcessStream(imageBase64, path, fileName, addTwitter);

		// FIXME get the list of roles(tenants) from the stream info
		if ("public".equals(newStream.getVisibility())) {
			addStream.setVar("visibility", "public");
			addStream.setVar("roles", "");
			addStream.setVar("authType", "None");

		} else {
			addStream.setVar("visibility", "restricted");
			String ruoli = "";

			if (newStream.getTenantssharing() != null && newStream.getTenantssharing().getTenantsharing() != null) {
				for (Tenantsharing t : newStream.getTenantssharing().getTenantsharing()) {
					if (!ruoli.equals(""))
						ruoli += ",";
					ruoli += t.getTenantCode() + "_subscriber";
				}
			}

			if (!ruoli.contains(newStream.getCodiceTenant() + "_subscriber")) {
				ruoli += newStream.getCodiceTenant() + "_subscriber";
			}
			addStream.setVar("roles", ruoli);
			addStream.setVar("authType", "Application & Application User");
		}

		if (update) {
			addStream.setVar("actionAPI", "updateAPI");
		} else {
			addStream.setVar("actionAPI", "addAPI");
		}

		addStream.setVar("apimanConsoleAddress", Config.getInstance().getConsoleAddress());
		addStream.setVar("username", Config.getInstance().getStoreUsername());
		addStream.setVar("password", Config.getInstance().getStorePassword());
		addStream.setVar("httpok", Config.getInstance().getHttpOk());
		addStream.setVar("ok", Config.getInstance().getResponseOk());

		// addStream.setVar("icon", path + fileName);
		addStream.setVar("icon", "");
		addStream.setVar("apiVersion", "1.0");
		addStream.setVar("apiName", tenant + "." + sensor + "_" + stream + "_stream");
		addStream.setVar("context", "/api/topic/output." + tenant + "." + sensor + "_" + stream);
		addStream.setVar("P", "");
		addStream.setVar("endpoint", Config.getInstance().getDammiInfo());
		addStream.setVar("desc", newStream.getNomeStream() != null ? newStream.getNomeStream() : "");
		addStream.setVar("copiright", newStream.getCopyright() != null ? newStream.getCopyright() : "");

		addStream.setVar("extra_isApi", "false");
		addStream.setVar("extra_apiDescription", newStream.getVirtualEntityName() != null ? newStream.getVirtualEntityName() : "");
		addStream.setVar("codiceTenant", newStream.getCodiceTenant() != null ? newStream.getCodiceTenant() : "");
		addStream.setVar("codiceStream", newStream.getCodiceStream() != null ? newStream.getCodiceStream() : "");
		addStream.setVar("nomeStream", newStream.getNomeStream() != null ? newStream.getNomeStream() : "");
		addStream.setVar("nomeTenant", newStream.getNomeTenant() != null ? newStream.getNomeTenant() : "");
		addStream.setVar("licence", newStream.getLicence() != null ? newStream.getLicence() : "");
		addStream.setVar("disclaimer", newStream.getDisclaimer() != null ? newStream.getDisclaimer() : "");

		addStream.setVar("virtualEntityCode", newStream.getCodiceVirtualEntity() != null ? newStream.getCodiceVirtualEntity() : "");
		addStream.setVar("virtualEntityName", newStream.getVirtualEntityName() != null ? newStream.getVirtualEntityName() : "");
		addStream.setVar("virtualEntityDescription", newStream.getVirtualEntityDescription() != null ? newStream.getVirtualEntityDescription() : "");

		addStream.setVar("extra_latitude", "");
		addStream.setVar("extra_longitude", "");
		if (newStream.getVirtualEntityPositions() != null) {
			if (newStream.getVirtualEntityPositions().getPosition() != null) {
				List<org.csi.yucca.storage.datamanagementapi.model.streaminput.Position> position = newStream.getVirtualEntityPositions().getPosition();
				if (position.get(0) != null) {
					addStream.setVar("extra_latitude", position.get(0).getLat().toString());
					addStream.setVar("extra_longitude", position.get(0).getLon().toString());
				}
			}
		}

		String tags = "";

		if (newStream.getDomainStream() != null) {
			tags += newStream.getDomainStream();
		}
		if (newStream.getStreamTags() != null && newStream.getStreamTags().getTag() != null) {
			for (Tag t : newStream.getStreamTags().getTag()) {
				tags += "," + t.getTagCode();
			}
		}

		addStream.setVar("tags", tags);

		String datasetInput = extractContentForDocument(json,newStream.getCodiceTenant() != null ? newStream.getCodiceTenant() : "");
		//addStream.setVar("content", "{ \"nodata\" : \"nodata\" }");
		
		Gson gson = JSonHelper.getInstance();
		POJOStreams pojoStreams2 = gson.fromJson(datasetInput, POJOStreams.class);	
		if (newStream.getSaveData()==1) {
			log.info(";;;;;SETTING "+newStream.getIdDataset());
			pojoStreams2.getStreams().getStream().setIdDataset(newStream.getIdDataset());
		}

		//SOLR
				SearchEngineMetadata newdocument = new SearchEngineMetadata();
				newdocument.setupEngine(pojoStreams2.getStreams().getStream());
				String newJsonDoc= gson.toJson(newdocument);
				//addStream.setVar("content", newJsonDoc);
				//CloudSolrClient solrServer =  CloudSolrSingleton.getServer();
				//solrServer.setDefaultCollection("sdp_int_metasearch2");
				//solrServer.setDefaultCollection(Config.getInstance().getSolrCollection());
				

				
				
				
				SolrInputDocument doc = newdocument.getSolrDocument();
				
				
				if ("KNOX".equalsIgnoreCase(Config.getInstance().getSolrTypeAccess()))
				{
					SolrClient solrServer= null;
					solrServer = KnoxSolrSingleton.getServer();
					log.info("[StoreService::createApiForBulk] - ---------------------" + doc.toString());
					solrServer.add(Config.getInstance().getSolrCollection(),doc);
					solrServer.commit();
				}
				else {
					CloudSolrClient solrServer = CloudSolrSingleton.getServer();
					solrServer.setDefaultCollection(Config.getInstance().getSolrCollection());
					log.info("[StoreService::createApiForBulk] - ---------------------" + doc.toString());
					solrServer.add(Config.getInstance().getSolrCollection(),doc);
					solrServer.commit();
				}
				
		
		addStream.run();
		return true;
	}

	private static Map<String, ResourceBundle> messagesMap = new HashMap<String, ResourceBundle>();
	private static Map<String, TenantCache> tenantOrganizaionMap = new HashMap<String, TenantCache>();

	private static Map<String, TenantCache> getTenantOrganizaionMap(String tenantCode) {

		if (tenantOrganizaionMap.get(tenantCode) == null) {
			try {
				tenantOrganizaionMap.putAll(loadTenants());
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}
		return tenantOrganizaionMap;
	}
	
	
	private static ResourceBundle getMessages(String lang) {

		if (messagesMap.get(lang) == null) {
			Locale locale = new Locale(lang);

			// messagesMap.put(lang,
			// ResourceBundle.getBundle("/i18n/MessagesBundle", locale));
			String tagResource = "";
			String domainResource = "";
			String subDomainResource = "";

			tagResource = formatMessages(locale, "tags");
			domainResource = formatMessages(locale, "domains");
			subDomainResource = formatMessages(locale, "subdomains");
			try {
				messagesMap.put(lang, new PropertyResourceBundle(new StringReader(tagResource + "\n" + domainResource + "\n" + subDomainResource)));
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}
		return messagesMap.get(lang);
	}

	public static Properties parsePropertiesString(String s) {
		// grr at load() returning void rather than the Properties object
		// so this takes 3 lines instead of "return new Properties().load(...);"
		final Properties p = new Properties();
		try {
			p.load(new StringReader(s));
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return p;
	}

	private static JSONObject loadMessages(Locale currentLocale, String element) {
		InputStream is = null;
		JSONObject json = null;
		try {
			String tagsDomainsURL = Config.getInstance().getApiAdminServiceUrl();
			is = new URL(tagsDomainsURL + "/misc/stream"+element+"/").openStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = null;
			jsonText = readAll(rd);
			json = new JSONObject(jsonText);

			is.close();
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return json;
	}

	private static HashMap<String, TenantCache> loadTenants() {
		InputStream is = null;
		JSONObject json = null;
		HashMap<String, TenantCache> ret= new HashMap<String, TenantCache>();
		try {
			String tagsDomainsURL = Config.getInstance().getApiAdminServiceUrl();
			is = new URL(tagsDomainsURL + "/tenants/").openStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = null;
			jsonText = readAll(rd);
			json = new JSONObject(jsonText);

			is.close();
			
			
			
			JSONArray elements = json.getJSONObject("tenants").getJSONArray("tenant");
			TenantCache tc= null;
			for (int i = 0; i < elements.length(); i++) {
				String codice = elements.getJSONObject(i).getString("organizationCode");
				String codiceTenant = elements.getJSONObject(i).getString("tenantCode");
				String desc = elements.getJSONObject(i).getString("organizationDescription");
				String tenantdesc = elements.getJSONObject(i).getString("tenantDescription");
				String tenantName=elements.getJSONObject(i).getString("tenantName");

				tc= new TenantCache();
				tc.setOrganizationCode(codice);
				tc.setOrganizationDescription(desc);
				tc.setTenantCode(codiceTenant);
				tc.setTenantDescription(tenantdesc);
				tc.setTenantName(tenantName);
				
				
				
				ret.put(codiceTenant, tc);
			}
			
			
			
		} catch (JSONException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return ret;
	}
	
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	protected static String formatMessages(Locale locale, String element) {

		JSONObject messages = loadMessages(locale, element);

		log.debug("[StoreService::formatMessages] - START");
		StringBuffer sb = new StringBuffer("");
		String loc = locale.getLanguage().substring(0, 1).toUpperCase() + locale.getLanguage().substring(1);

		String label1 = (element.equals("tags") ? "streamTags" : (element.equals("domains") ? "streamDomains" : "streamSubDomains"));
		String label2 = (element.equals("tags") ? "tagCode" : (element.equals("domains") ? "codDomain" : "codSubDomain"));

		try {
			JSONObject streamTags = messages.getJSONObject(label1);
			JSONArray elements = streamTags.getJSONArray("element");
			for (int i = 0; i < elements.length(); i++) {
				String tagCode = elements.getJSONObject(i).getString(label2);
				String langEl = elements.getJSONObject(i).getString("lang" + loc);
				sb.append(tagCode + " = " + langEl + "\n");
			}

		} catch (JSONException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			log.debug("[StoreService::formatMessages] - END");
		}
		return sb.toString();
	}

	@POST
	@Path("/apiPublishStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiPublishStore(final String inputJson) throws UnknownHostException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject rootObj = parser.parse(inputJson).getAsJsonObject();

			// String status = rootObj.get("status").getAsString();
			String apiVersion = rootObj.get("apiVersion").getAsString();
			String apiName = rootObj.get("apiName").getAsString();
			String provider = rootObj.get("provider").getAsString();

			publishStore(apiVersion, apiName, provider);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}

	public static boolean publishStore(String apiVersion, String apiName, String provider) throws Exception {

		PublishApi publish = new PublishApi();

		publish.setVar("apimanConsoleAddress", Config.getInstance().getConsoleAddress());
		publish.setVar("username", Config.getInstance().getStoreUsername());
		publish.setVar("password", Config.getInstance().getStorePassword());
		publish.setVar("httpok", Config.getInstance().getHttpOk());
		publish.setVar("ok", Config.getInstance().getResponseOk());

		publish.setVar("publishStatus", "PUBLISHED");
		publish.setVar("apiVersion", apiVersion);
		publish.setVar("apiName", apiName);
		publish.setVar("provider", provider);

		publish.run();
		return true;
	}

	public static boolean removeStore(String apiVersion, String apiName, String provider) throws Exception {

		PublishApi publish = new PublishApi();

		publish.setVar("apimanConsoleAddress", Config.getInstance().getConsoleAddress());
		publish.setVar("username", Config.getInstance().getStoreUsername());
		publish.setVar("password", Config.getInstance().getStorePassword());
		publish.setVar("httpok", Config.getInstance().getHttpOk());
		publish.setVar("ok", Config.getInstance().getResponseOk());

		publish.setVar("publishStatus", "BLOCKED");
		publish.setVar("apiVersion", apiVersion);
		publish.setVar("apiName", apiName);
		publish.setVar("provider", provider);

		publish.run();

		
		
		// OLD when used document in api manager
//		RemoveDoc removeDoc = new RemoveDoc();
//		removeDoc.setVar("apimanConsoleAddress", Config.getInstance().getConsoleAddress());
//		removeDoc.setVar("username", Config.getInstance().getStoreUsername());
//		removeDoc.setVar("password", Config.getInstance().getStorePassword());
//		removeDoc.setVar("httpok", Config.getInstance().getHttpOk());
//		removeDoc.setVar("ok", Config.getInstance().getResponseOk());
//
//		removeDoc.setVar("publishStatus", "BLOCKED");
//		removeDoc.setVar("apiVersion", apiVersion);
//		removeDoc.setVar("apiName", apiName);
//		removeDoc.setVar("provider", provider);
//		removeDoc.setVar("P", "");
//		removeDoc.run();

		return true;
	}

	@POST
	@Path("/apiRemoveStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiRemoveStore(final String inputJson) throws UnknownHostException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject rootObj = parser.parse(inputJson).getAsJsonObject();
			String apiVersion = rootObj.get("apiVersion").getAsString();
			String apiName = rootObj.get("apiName").getAsString();
			String provider = rootObj.get("provider").getAsString();

			removeStore(apiVersion, apiName, provider);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}

}
