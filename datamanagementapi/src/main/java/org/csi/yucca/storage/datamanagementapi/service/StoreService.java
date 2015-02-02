package org.csi.yucca.storage.datamanagementapi.service;


import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.AddStream;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.PublishApi;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.QSPStore;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.csi.yucca.storage.datamanagementapi.util.ImageProcessor;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.util.JSON;

@Path("/store")
public class StoreService {

	@Context
	ServletContext context;
	static Logger log = Logger.getLogger(StoreService.class);

	@POST
	@Path("/apiCreateApiStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiCreateApiStore(final String datasetInput) throws UnknownHostException {

		Gson gson = JSonHelper.getInstance();

		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try{
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if(pojoStreams != null && pojoStreams.getStreams()!= null && pojoStreams.getStreams().getStream()!= null){

				Stream newStream = pojoStreams.getStreams().getStream();

				/*
				 * Aggiungi Stream allo store
				 */
				String tenant = newStream.getCodiceTenant();
				String sensor = newStream.getCodiceVirtualEntity();
				String stream = newStream.getCodiceStream();

				String apiName= tenant+"."+sensor+"_"+stream;
				try{
					createApiforStream(newStream,apiName,false);
				}catch(Exception duplicate){
					if(duplicate.getMessage().toLowerCase().contains("duplicate")){
						createApiforStream(newStream,apiName,true);
					}
					else throw duplicate;
				}

				if(newStream.getPublishStream()!=0){
					publishStore("1.0", apiName, "admin");
					String appName = "userportal_"+newStream.getCodiceTenant();
					StoreService.addSubscriptionForTenant(apiName,appName);
				}
			}

		}catch (Exception e) {
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

		String json = datasetInput.replaceAll("\\{\\n*\\t*.*@nil.*:.*\\n*\\t*\\}", "null"); // match @nil elements
		try{
			POJOStreams pojoStreams = gson.fromJson(json, POJOStreams.class);
			if(pojoStreams != null && pojoStreams.getStreams()!= null && pojoStreams.getStreams().getStream()!= null){

				Stream newStream = pojoStreams.getStreams().getStream();
				/*
				 * Aggiungi Stream allo store
				 */

				String tenant = newStream.getCodiceTenant();
				String sensor = newStream.getCodiceVirtualEntity();
				String stream = newStream.getCodiceStream();

				try{
					createStream(newStream,false);
				}catch(Exception duplicate){
					if(duplicate.getMessage().toLowerCase().contains("duplicate")){
						createStream(newStream,true);
					}
					else throw duplicate; 
				}
				String apiName= tenant+"."+sensor+"_"+stream+"_stream";
				if(newStream.getPublishStream()!=0){
					publishStore("1.0", apiName, "admin");
					String appName = "userportal_"+newStream.getCodiceTenant();
					StoreService.addSubscriptionForTenant(apiName,appName);
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}
	
	public static boolean addSubscriptionForTenant(String apiName,String appName) throws Exception {

		QSPStore subscription = new QSPStore();
		
		subscription.setVar("apiVersion","1.0");
		subscription.setVar("apiName",apiName);
		subscription.setVar("appName",appName);
		subscription.setVar("P","");
		
		subscription.run();

		return true;
	}

	public static String createApiforStream(Stream newStream,String apiName,boolean update) throws Exception{

		String apiFinalName= apiName+"_odata";

		AddStream addStream = new AddStream();

		ImageProcessor processor = new ImageProcessor();
		String imageBase64 =newStream.getStreamIcon();

		String path ="images/";
		String fileName =newStream.getCodiceStream()+".png";

		processor.doProcessOdata(imageBase64, path,fileName);

		//FIXME get the list of roles(tenants) from the stream info
		if("public".equals(newStream.getVisibility())){
			addStream.setVar("visibility","public");
			addStream.setVar("roles","");
			addStream.setVar("authType","None");

		}else{
			addStream.setVar("visibility","private");
			addStream.setVar("roles",newStream.getCodiceTenant()+"_subscriber");
			addStream.setVar("authType","Application & Application User");
		}

		if(update){
			addStream.setVar("actionAPI","updateAPI");
		}else{
			addStream.setVar("actionAPI","addAPI");
		}

		addStream.setVar("icon",path+fileName);
		addStream.setVar("apiVersion","1.0");
		addStream.setVar("apiName",apiFinalName);
		addStream.setVar("context","/api/"+apiName);//ds_Voc_28;
		addStream.setVar("P","");
		addStream.setVar("endpoint","http://int-api.smartdatanet.it/odata/SmartDataOdataService.svc/"+apiName);
		addStream.setVar("desc",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("copiright",newStream.getCopyright()!=null ? newStream.getCopyright() :"");

		addStream.setVar("extra_isApi","true");
		addStream.setVar("codiceTenant",newStream.getCodiceTenant()!=null ? newStream.getCodiceTenant() :"");
		addStream.setVar("codiceStream",newStream.getCodiceStream()!=null ? newStream.getCodiceStream() :"");
		addStream.setVar("nomeStream",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("nomeTenant",newStream.getNomeTenant()!=null ? newStream.getNomeTenant() :"");
		addStream.setVar("licence",newStream.getLicence()!=null ? newStream.getLicence() :"");
		addStream.setVar("virtualEntityName",newStream.getVirtualEntityName()!=null ? newStream.getVirtualEntityName() :"");
		addStream.setVar("virtualEntityDescription",newStream.getVirtualEntityDescription()!=null ? newStream.getVirtualEntityDescription() :"");
		String tags = "";
		if(newStream.getTags()!=null){
			tags+=newStream.getTags();
		}

		if(newStream.getDomainStream()!=null){
			tags+=newStream.getDomainStream();
		}
		addStream.setVar("tags",tags);
		addStream.run();

		return apiFinalName;
	}

	public static boolean createStream(Stream newStream,boolean update) throws Exception{

		String tenant = newStream.getCodiceTenant();
		String sensor = newStream.getCodiceVirtualEntity();
		String stream = newStream.getCodiceStream();

		AddStream addStream = new AddStream();

		ImageProcessor processor = new ImageProcessor();
		String imageBase64 =newStream.getStreamIcon();
		String path ="images/";
		String fileName =newStream.getCodiceStream()+".png";
		processor.doProcessStream(imageBase64, path,fileName);

		//FIXME get the list of roles(tenants) from the stream info
		if("public".equals(newStream.getVisibility())){
			addStream.setVar("visibility","public");
			addStream.setVar("roles","");
		}else{
			addStream.setVar("visibility","private");
			addStream.setVar("roles",newStream.getCodiceTenant()+"_subscriber");
		}

		if(update){
			addStream.setVar("actionAPI","updateAPI");
		}else{
			addStream.setVar("actionAPI","addAPI");
		}

		addStream.setVar("icon",path+fileName);
		addStream.setVar("apiVersion","1.0");
		addStream.setVar("apiName",tenant+"."+sensor+"_"+stream+"_stream");
		addStream.setVar("context","/api/topic/output."+tenant+"."+sensor+"_"+stream);
		addStream.setVar("P","");
		addStream.setVar("endpoint","http://int-api.smartdatanet.it/dammiInfo");
		addStream.setVar("desc",newStream.getNomeStream());
		addStream.setVar("copiright",newStream.getCopyright()!=null ? newStream.getCopyright() :"");

		addStream.setVar("extra_isApi","true");
		addStream.setVar("codiceTenant",newStream.getCodiceTenant()!=null ? newStream.getCodiceTenant() :"");
		addStream.setVar("codiceStream",newStream.getCodiceStream()!=null ? newStream.getCodiceStream() :"");
		addStream.setVar("nomeStream",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("nomeTenant",newStream.getNomeTenant()!=null ? newStream.getNomeTenant() :"");
		addStream.setVar("licence",newStream.getLicence()!=null ? newStream.getLicence() :"");
		addStream.setVar("virtualEntityName",newStream.getVirtualEntityName()!=null ? newStream.getVirtualEntityName() :"");
		addStream.setVar("virtualEntityDescription",newStream.getVirtualEntityDescription()!=null ? newStream.getVirtualEntityDescription() :"");
		String tags = "";
		if(newStream.getTags()!=null){
			tags+=newStream.getTags();
		}

		if(newStream.getDomainStream()!=null){
			tags+=newStream.getDomainStream();
		}
		addStream.setVar("tags",tags);

		addStream.run();
		return true;
	}

	@POST
	@Path("/apiPublishStore")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiPublishStore(final String inputJson) throws UnknownHostException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject rootObj = parser.parse(inputJson).getAsJsonObject();

			//		String status = rootObj.get("status").getAsString();
			String apiVersion = rootObj.get("apiVersion").getAsString();
			String apiName = rootObj.get("apiName").getAsString();
			String provider = rootObj.get("provider").getAsString();

			publishStore(apiVersion, apiName,provider);

		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}

	public static boolean publishStore(String apiVersion,String apiName,String provider) throws Exception  {

		PublishApi publish = new PublishApi();
		publish.setVar("publishStatus", "PUBLISHED");
		publish.setVar("apiVersion",apiVersion);
		publish.setVar("apiName",apiName);
		publish.setVar("provider",provider);

		publish.run();
		return true;
	}

	public static boolean removeStore(String apiVersion,String apiName,String provider) throws Exception  {

		PublishApi publish = new PublishApi();
		publish.setVar("publishStatus", "BLOCKED");
		publish.setVar("apiVersion",apiVersion);
		publish.setVar("apiName",apiName);
		publish.setVar("provider",provider);

		publish.run();

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

		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}

}
