package org.csi.yucca.storage.datamanagementapi.service;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.model.store.response.GeneralResponse;
import org.csi.yucca.storage.datamanagementapi.model.tenantin.TenantIn;
import org.csi.yucca.storage.datamanagementapi.model.tenantout.TenantOut;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import org.csi.yucca.storage.datamanagementapi.util.TenantFiller;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@Path("/tenant")
public class InstallTenantService {

	// private static final Integer MAX_RETRY = 5;
	MongoClient mongo;
	// Map<String,String> mongoParams = null;

	@Context
	ServletContext context;
	private String storeBaseUrl;
	private String apiAdminServiceUrl;

	private CloseableHttpClient httpclient;

	private Gson gson;
	static Logger log = Logger.getLogger(InstallTenantService.class);

	@POST
	@Path("/insert")
	@Produces(MediaType.APPLICATION_JSON)
	public String createTenant(final String tenantInput) throws UnknownHostException {

		// mongoParams = ConfigParamsSingleton.getInstance().getParams();
		mongo = MongoSingleton.getMongoClient();

		gson = JSonHelper.getInstance();

		String json = tenantInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match
																							// @nil
																							// elements
		try {
			TenantIn tenantin = gson.fromJson(json, TenantIn.class);
			if (tenantin != null && tenantin.getTenants() != null && tenantin.getTenants().getTenant() != null) {

				DB db = mongo.getDB(Config.getInstance().getDbSupport());
				DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportTenant());
				TenantOut myTenant = TenantFiller.fillTenant(tenantin.getTenants().getTenant());

				DBObject dbObject = (DBObject) JSON.parse(gson.toJson(myTenant, TenantOut.class));
				col.insert(dbObject);

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}

		return JSON.parse("{OK:1}").toString();
	}

	@POST
	@Path("/addAdminApplication")
	@Produces(MediaType.APPLICATION_JSON)
	public String addAdminApplication(@QueryParam("tenantCode") String tenantCode, @QueryParam("username") String username,
			@QueryParam("password") String password) throws UnknownHostException {

		try {
			resgisterToStoreInit(username, password);
			addApplication(tenantCode);
			logoutFromStore(username, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{success:0, message:'Error: " + e.getMessage() + "'}").toString();
		}
		return JSON.parse("{success:1, message:'Add Application completed successfully'}").toString();
	}

	@POST
	@Path("/subscribeAdminApi")
	@Produces(MediaType.APPLICATION_JSON)
	public String subscribeAdminApiInStore(@QueryParam("tenantCode") String tenantCode, @QueryParam("username") String username,
			@QueryParam("password") String password) throws UnknownHostException {

		try {
			resgisterToStoreInit(username, password);
			subscribeAdminApi(tenantCode);
			logoutFromStore(username, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{success:0, message:'Error: " + e.getMessage() + "'}").toString();
		}
		return JSON.parse("{success:1, message:'Subscribe Admin Api completed successfully'}").toString();
	}

	@POST
	@Path("/generetateAdminKey")
	@Produces(MediaType.APPLICATION_JSON)
	public String generetateAdminKey(@QueryParam("tenantCode") String tenantCode, @QueryParam("username") String username,
			@QueryParam("password") String password) throws UnknownHostException {

		try {
			resgisterToStoreInit(username, password);
			GeneralResponse generetateKeyResponse = generetateKey(tenantCode);
			String clientKey = generetateKeyResponse.getData().getKey().getConsumerKey();
			String clientSecret = generetateKeyResponse.getData().getKey().getConsumerSecret();
			// insert key
			insertKey(tenantCode, clientKey, clientSecret);
			logoutFromStore(username, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{success:0, message:'Error: " + e.getMessage() + "'}").toString();
		}
		return JSON.parse("{success:1, message:'Generate Admin Key completed successfully'}").toString();
	}

	private void resgisterToStoreInit(String username, String password) throws Exception {
		storeBaseUrl = Config.getInstance().getStoreBaseUrl();
		apiAdminServiceUrl = Config.getInstance().getApiAdminServiceUrl();
		httpclient = HttpClients.createDefault();
		gson = JSonHelper.getInstance();

		// login
		loginOnStore(username, password);
	}

	private void loginOnStore(String username, String password) throws Exception {
		log.debug("[InstallTenantService::loginOnStore] username " + username);

		List<NameValuePair> loginParams = new LinkedList<NameValuePair>();
		loginParams.add(new BasicNameValuePair("action", "login"));
		loginParams.add(new BasicNameValuePair("username", username));
		loginParams.add(new BasicNameValuePair("password", password));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/user/login/ajax/login.jag";
		String response = makeHttpPost(url, loginParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Login with user " + username + " failed: " + generalResponse.getMessage());
	}

	private void addApplication(String tenantCode) throws Exception {
		log.debug("[InstallTenantService::addApplication] tenantCode " + tenantCode);
		List<NameValuePair> addApplicationParams = new LinkedList<NameValuePair>();
		addApplicationParams.add(new BasicNameValuePair("action", "addApplication"));
		addApplicationParams.add(new BasicNameValuePair("application", "userportal_" + tenantCode));
		addApplicationParams.add(new BasicNameValuePair("tier", "Unlimited"));
		addApplicationParams.add(new BasicNameValuePair("description", ""));
		addApplicationParams.add(new BasicNameValuePair("callbackUrl", ""));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/application/application-add/ajax/application-add.jag";
		String response = makeHttpPost(url, addApplicationParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Add Application for " + tenantCode + " failed: " + generalResponse.getMessage());

	}

	private void subscribeAdminApi(String tenantCode) throws Exception {
		log.debug("[InstallTenantService::subscribeAdminApi] tenantCode: " + tenantCode);
		List<NameValuePair> subscribeAdminApiParams = new LinkedList<NameValuePair>();
		subscribeAdminApiParams.add(new BasicNameValuePair("action", "addAPISubscription"));
		subscribeAdminApiParams.add(new BasicNameValuePair("name", "admin_api"));
		subscribeAdminApiParams.add(new BasicNameValuePair("version", "1.0"));
		subscribeAdminApiParams.add(new BasicNameValuePair("tier", "Unlimited"));
		subscribeAdminApiParams.add(new BasicNameValuePair("applicationName", "userportal_" + tenantCode));
		subscribeAdminApiParams.add(new BasicNameValuePair("application", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("provider", "admin"));
		subscribeAdminApiParams.add(new BasicNameValuePair("keytype", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("callbackUrl", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("authorizedDomains", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("validityTime", ""));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/subscription/subscription-add/ajax/subscription-add.jag";
		String response = makeHttpPost(url, subscribeAdminApiParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Add Application for " + tenantCode + " failed: " + generalResponse.getMessage());
	}

	private GeneralResponse generetateKey(String tenantCode) throws Exception {
		log.debug("[InstallTenantService::generetateKey] tenantCode: " + tenantCode);
		List<NameValuePair> generetateKeyParams = new LinkedList<NameValuePair>();
		generetateKeyParams.add(new BasicNameValuePair("action", "generateApplicationKey"));
		generetateKeyParams.add(new BasicNameValuePair("name", ""));
		generetateKeyParams.add(new BasicNameValuePair("version", ""));
		generetateKeyParams.add(new BasicNameValuePair("tier", ""));
		generetateKeyParams.add(new BasicNameValuePair("applicationName", ""));
		generetateKeyParams.add(new BasicNameValuePair("application", "userportal_" + tenantCode));
		generetateKeyParams.add(new BasicNameValuePair("provider", "admin"));
		generetateKeyParams.add(new BasicNameValuePair("keytype", "PRODUCTION"));
		generetateKeyParams.add(new BasicNameValuePair("callbackUrl", ""));
		generetateKeyParams.add(new BasicNameValuePair("authorizedDomains", "ALL"));
		generetateKeyParams.add(new BasicNameValuePair("validityTime", "999999999"));

		GeneralResponse generalResponse = null;
		String url = storeBaseUrl + "site/blocks/subscription/subscription-add/ajax/subscription-add.jag";
		String response = makeHttpPost(url, generetateKeyParams);
		if (response != null)
			generalResponse = gson.fromJson(response, GeneralResponse.class);
		if (generalResponse.getError())
			throw new Exception("Add Application for " + tenantCode + " failed: " + generalResponse.getMessage());
		if (generalResponse.getData() == null || generalResponse.getData().getKey() == null || generalResponse.getData().getKey().getConsumerKey() == null
				|| generalResponse.getData().getKey().getConsumerSecret() == null)
			throw new Exception("Add Application for " + tenantCode + " failed: Invalid consumerKey and consumerSecret - " + generalResponse.getMessage());

		return generalResponse;
	}

	private void insertKey(String tenantCode, String clientKey, String clientSecret) throws Exception {
		log.debug("[InstallTenantService::insertKey] tenantCode: " + clientKey);
		String paramJson = "{ \"tenant\": {\"clientKey\": \"" + clientKey + "\", \"clientSecret\": \"" + clientSecret + "\"}}";

		String url = apiAdminServiceUrl + "/secdata/clientcred/" + tenantCode;

		HttpEntity response = makeHttpPut(url, paramJson);
		if (response != null)
			EntityUtils.toString(response); // {"generatedId":{"element":{"idGenerato":36}}}

	}

	private void logoutFromStore(String username, String password) throws Exception {
		log.debug("[InstallTenantService::logoutFromStore] username " + username);

		List<NameValuePair> logoutParams = new LinkedList<NameValuePair>();
		logoutParams.add(new BasicNameValuePair("action", "logout"));
		logoutParams.add(new BasicNameValuePair("username", username));
		logoutParams.add(new BasicNameValuePair("password", password));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/user/login/ajax/login.jag";
		String response = makeHttpPost(url, logoutParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Logout with user " + username + " failed: " + generalResponse.getMessage());
	}

	private String makeHttpPost(String url, List<NameValuePair> params) throws Exception {
		log.debug("[InstallTenantService::makeHttpPost] url " + url + " params " + explainParams(params));
		HttpPost postMethod = new HttpPost(url);
		postMethod.setEntity(new UrlEncodedFormEntity(params));

		CloseableHttpResponse response = httpclient.execute(postMethod);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			try {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			} finally {
				response.close();
			}
		} else {
			log.error("[InstallTenantService::makeHttpPost] ERROR Status code " + statusCode);
			throw new HttpException("ERROR: Status code " + statusCode);
		}
	}

	private HttpEntity makeHttpPut(String url, String paramJson) throws Exception {
		log.debug("[InstallTenantService::makeHttpPut] url " + url + " paramJson " + paramJson);
		HttpPut putMethod = new HttpPut(url);
		StringEntity params = new StringEntity(paramJson, "UTF-8");
		params.setContentType("application/json");
		putMethod.addHeader("content-type", "application/json");
		putMethod.setEntity(params);

		CloseableHttpResponse response = httpclient.execute(putMethod);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			try {
				HttpEntity entity = response.getEntity();
				return entity;
			} finally {
				response.close();
			}
		} else {
			log.error("[InstallTenantService::makeHttpPut] ERROR Status code " + statusCode);
			throw new HttpException("ERROR: Status code " + statusCode);
		}
	}

	private String explainParams(List<NameValuePair> params) {
		String result = "";
		if (params != null)
			for (NameValuePair nameValuePair : params) {
				result += nameValuePair.getName() + "=" + nameValuePair.getValue() + "&";
			}
		return result;
	}

}
