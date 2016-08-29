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
import org.apache.http.client.HttpClient;
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


	static Logger log = Logger.getLogger(InstallTenantService.class);

	@POST
	@Path("/insert")
	@Produces(MediaType.APPLICATION_JSON)
	public String createTenant(final String tenantInput) throws UnknownHostException {

		// mongoParams = ConfigParamsSingleton.getInstance().getParams();
		mongo = MongoSingleton.getMongoClient();
		Gson gson = JSonHelper.getInstance();

		String json = tenantInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try {
			TenantIn tenantin = gson.fromJson(json, TenantIn.class);
			if (tenantin != null && tenantin.getTenants() != null && tenantin.getTenants().getTenant() != null) {

				DB db = mongo.getDB(Config.getInstance().getDbSupport());
				DBCollection col = db.getCollection(Config.getInstance().getCollectionSupportTenant());
				TenantOut myTenant = TenantFiller.fillTenant(tenantin.getTenants().getTenant());

				log.debug("[InstallTenantService::createTenant] myTenant = " + myTenant);
				
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
			CloseableHttpClient c = ApiManagerFacade.registerToStoreInit(username, password);
			ApiManagerFacade.addApplication(c,"userportal_" + tenantCode);
			ApiManagerFacade.logoutFromStore(c,username, password);
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
			CloseableHttpClient c = ApiManagerFacade.registerToStoreInit(username, password);
			ApiManagerFacade.subscribeApi(c,"admin_api", "userportal_" + tenantCode);
			ApiManagerFacade.logoutFromStore(c,username, password);
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
			CloseableHttpClient c = ApiManagerFacade.registerToStoreInit(username, password);
			GeneralResponse generetateKeyResponse = ApiManagerFacade.generateKey(c, "userportal_" + tenantCode);
			String clientKey = generetateKeyResponse.getData().getKey().getConsumerKey();
			String clientSecret = generetateKeyResponse.getData().getKey().getConsumerSecret();
			// insert key
			ApiManagerFacade.insertKey(c, tenantCode, clientKey, clientSecret);
			ApiManagerFacade.logoutFromStore(c, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{success:0, message:'Error: " + e.getMessage() + "'}").toString();
		}
		return JSON.parse("{success:1, message:'Generate Admin Key completed successfully'}").toString();
	}


}
