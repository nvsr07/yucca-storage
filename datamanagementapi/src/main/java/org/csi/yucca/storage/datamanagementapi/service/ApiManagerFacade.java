package org.csi.yucca.storage.datamanagementapi.service;

import java.util.LinkedList;
import java.util.List;

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
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class ApiManagerFacade {

	private static String storeBaseUrl = Config.getInstance().getStoreBaseUrl();
	private static String apiAdminServiceUrl = Config.getInstance().getApiAdminServiceUrl();

	private static Gson gson = JSonHelper.getInstance();

		static Logger log = Logger.getLogger(InstallTenantService.class);
	

	public static CloseableHttpClient registerToStoreInit(String username, String password) throws Exception {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		// login
		loginOnStore(httpclient, username, password);
		
		return httpclient;
	}

	private static void loginOnStore(CloseableHttpClient httpclient, String username, String password) throws Exception {
		log.debug("[InstallTenantService::loginOnStore] username " + username);

		List<NameValuePair> loginParams = new LinkedList<NameValuePair>();
		loginParams.add(new BasicNameValuePair("action", "login"));
		loginParams.add(new BasicNameValuePair("username", username));
		loginParams.add(new BasicNameValuePair("password", password));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/user/login/ajax/login.jag";
		String response = makeHttpPost(httpclient, url, loginParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Login with user " + username + " failed: " + generalResponse.getMessage());
	}

	public static void addApplication(CloseableHttpClient httpclient, String applicationCode) throws Exception {
		log.debug("[InstallTenantService::addApplication] applicationCode " + applicationCode);
		List<NameValuePair> addApplicationParams = new LinkedList<NameValuePair>();
		addApplicationParams.add(new BasicNameValuePair("action", "addApplication"));
		addApplicationParams.add(new BasicNameValuePair("application", applicationCode));
		addApplicationParams.add(new BasicNameValuePair("tier", "Unlimited"));
		addApplicationParams.add(new BasicNameValuePair("description", ""));
		addApplicationParams.add(new BasicNameValuePair("callbackUrl", ""));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/application/application-add/ajax/application-add.jag";
		String response = makeHttpPost(httpclient, url, addApplicationParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Add Application for " + applicationCode + " failed: " + generalResponse.getMessage());

	}

	public static void subscribeApi(CloseableHttpClient httpclient,String apiName,  String appName) throws Exception {
		log.debug("[InstallTenantService::subscribeAdminApi] appName: " + appName);
		List<NameValuePair> subscribeAdminApiParams = new LinkedList<NameValuePair>();
		subscribeAdminApiParams.add(new BasicNameValuePair("action", "addAPISubscription"));
		subscribeAdminApiParams.add(new BasicNameValuePair("name",apiName));
		subscribeAdminApiParams.add(new BasicNameValuePair("version", "1.0"));
		subscribeAdminApiParams.add(new BasicNameValuePair("tier", "Unlimited"));
		subscribeAdminApiParams.add(new BasicNameValuePair("applicationName", appName));
		subscribeAdminApiParams.add(new BasicNameValuePair("application", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("provider", "admin"));
		subscribeAdminApiParams.add(new BasicNameValuePair("keytype", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("callbackUrl", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("authorizedDomains", ""));
		subscribeAdminApiParams.add(new BasicNameValuePair("validityTime", ""));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/subscription/subscription-add/ajax/subscription-add.jag";
		String response = makeHttpPost(httpclient, url, subscribeAdminApiParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Add Application " + appName + " for "+apiName+" failed: " + generalResponse.getMessage());
	}

	public static GeneralResponse generateKey(CloseableHttpClient httpclient, String application) throws Exception {
		log.debug("[InstallTenantService::generetateKey] application: " + application);
		List<NameValuePair> generetateKeyParams = new LinkedList<NameValuePair>();
		generetateKeyParams.add(new BasicNameValuePair("action", "generateApplicationKey"));
		generetateKeyParams.add(new BasicNameValuePair("name", ""));
		generetateKeyParams.add(new BasicNameValuePair("version", ""));
		generetateKeyParams.add(new BasicNameValuePair("tier", ""));
		generetateKeyParams.add(new BasicNameValuePair("applicationName", ""));
		generetateKeyParams.add(new BasicNameValuePair("application", application));
		generetateKeyParams.add(new BasicNameValuePair("provider", "admin"));
		generetateKeyParams.add(new BasicNameValuePair("keytype", "PRODUCTION"));
		generetateKeyParams.add(new BasicNameValuePair("callbackUrl", ""));
		generetateKeyParams.add(new BasicNameValuePair("authorizedDomains", "ALL"));
		generetateKeyParams.add(new BasicNameValuePair("validityTime", "999999999"));

		GeneralResponse generalResponse = null;
		String url = storeBaseUrl + "site/blocks/subscription/subscription-add/ajax/subscription-add.jag";
		String response = makeHttpPost(httpclient, url, generetateKeyParams);
		if (response != null)
			generalResponse = gson.fromJson(response, GeneralResponse.class);
		if (generalResponse.getError())
			throw new Exception("Add Application for " + application + " failed: " + generalResponse.getMessage());
		if (generalResponse.getData() == null || generalResponse.getData().getKey() == null || generalResponse.getData().getKey().getConsumerKey() == null
				|| generalResponse.getData().getKey().getConsumerSecret() == null)
			throw new Exception("Add Application for " + application + " failed: Invalid consumerKey and consumerSecret - " + generalResponse.getMessage());

		return generalResponse;
	}

	public static void insertKey(CloseableHttpClient httpclient, String tenantCode, String clientKey, String clientSecret) throws Exception {
		log.debug("[InstallTenantService::insertKey] tenantCode: " + clientKey);
		String paramJson = "{ \"tenant\": {\"clientKey\": \"" + clientKey + "\", \"clientSecret\": \"" + clientSecret + "\"}}";

		String url = apiAdminServiceUrl + "/secdata/clientcred/" + tenantCode;

		HttpEntity response = makeHttpPut(httpclient, url, paramJson);
		if (response != null)
			EntityUtils.toString(response); // {"generatedId":{"element":{"idGenerato":36}}}

	}

	public static void logoutFromStore(CloseableHttpClient httpclient, String username, String password) throws Exception {
		log.debug("[InstallTenantService::logoutFromStore] username " + username);

		List<NameValuePair> logoutParams = new LinkedList<NameValuePair>();
		logoutParams.add(new BasicNameValuePair("action", "logout"));
		logoutParams.add(new BasicNameValuePair("username", username));
		logoutParams.add(new BasicNameValuePair("password", password));

		boolean result = false;
		String url = storeBaseUrl + "site/blocks/user/login/ajax/login.jag";
		String response = makeHttpPost(httpclient, url, logoutParams);
		GeneralResponse generalResponse = gson.fromJson(response, GeneralResponse.class);
		result = !generalResponse.getError();
		if (!result)
			throw new Exception("Logout with user " + username + " failed: " + generalResponse.getMessage());
		httpclient.close();
	}

	private static String makeHttpPost(CloseableHttpClient httpclient, String url, List<NameValuePair> params) throws Exception {
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

	private static HttpEntity makeHttpPut(CloseableHttpClient httpclient, String url, String paramJson) throws Exception {
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

	public static String explainParams(List<NameValuePair> params) {
		String result = "";
		if (params != null)
			for (NameValuePair nameValuePair : params) {
				result += nameValuePair.getName() + "=" + nameValuePair.getValue() + "&";
			}
		return result;
	}
}
