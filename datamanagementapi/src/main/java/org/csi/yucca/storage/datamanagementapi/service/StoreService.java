package org.csi.yucca.storage.datamanagementapi.service;



import java.net.UnknownHostException;
import java.util.Set;
import java.util.TreeSet;

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
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.POJOStreams;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Tag;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Tenantsharing;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
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
					Set<String> tenantSet = new TreeSet<String>();
					if(newStream.getTenantssharing()!=null){
						for( Tenantsharing tenantSh : newStream.getTenantssharing().getTenantsharing()){
							tenantSet.add(tenantSh.getTenantCode());
							String appName = "userportal_"+tenantSh.getTenantCode();
							StoreService.addSubscriptionForTenant(apiName,appName);
						}						
					}
					if(!tenantSet.contains(newStream.getCodiceTenant())){
						String appName = "userportal_"+newStream.getCodiceTenant();
						StoreService.addSubscriptionForTenant(apiName,appName);
					}
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
					Set<String> tenantSet = new TreeSet<String>();
					if(newStream.getTenantssharing()!=null){
						for( Tenantsharing tenantSh : newStream.getTenantssharing().getTenantsharing()){
							tenantSet.add(tenantSh.getTenantCode());
							String appName = "userportal_"+tenantSh.getTenantCode();
							StoreService.addSubscriptionForTenant(apiName,appName);
						}						
					}
					if(!tenantSet.contains(newStream.getCodiceTenant())){
						String appName = "userportal_"+newStream.getCodiceTenant();
						StoreService.addSubscriptionForTenant(apiName,appName);
					}
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

		subscription.setVar("apimanConsoleAddress",Config.getInstance().getConsoleAddress());
		subscription.setVar("username",Config.getInstance().getStoreUsername());
		subscription.setVar("password",Config.getInstance().getStorePassword());
		subscription.setVar("httpok",Config.getInstance().getHttpOk());
		subscription.setVar("ok",Config.getInstance().getResponseOk());

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
			addStream.setVar("visibility","restricted");
			String ruoli ="";

			if(newStream.getTenantssharing()!=null && newStream.getTenantssharing().getTenantsharing()!=null){
				for(Tenantsharing t : newStream.getTenantssharing().getTenantsharing()){
					if(!ruoli.equals(""))
						ruoli+=",";
					ruoli+=t.getTenantCode()+"_subscriber";
				}
			}
			if(!ruoli.contains(newStream.getCodiceTenant()+"_subscriber")){
				ruoli += newStream.getCodiceTenant()+"_subscriber";
			}

			addStream.setVar("roles",ruoli);
			addStream.setVar("authType","Application & Application User");
		}

		if(update){
			addStream.setVar("actionAPI","updateAPI");
		}else{
			addStream.setVar("actionAPI","addAPI");
		}

		addStream.setVar("apimanConsoleAddress",Config.getInstance().getConsoleAddress());
		addStream.setVar("username",Config.getInstance().getStoreUsername());
		addStream.setVar("password",Config.getInstance().getStorePassword());
		addStream.setVar("httpok",Config.getInstance().getHttpOk());
		addStream.setVar("ok",Config.getInstance().getResponseOk());

		addStream.setVar("icon",path+fileName);
		addStream.setVar("apiVersion","1.0");
		addStream.setVar("apiName",apiFinalName);
		addStream.setVar("context","/api/"+apiName);
		addStream.setVar("P","");
		addStream.setVar("endpoint",Config.getInstance().getBaseApiUrl()+apiName);
		addStream.setVar("desc",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("copiright",newStream.getCopyright()!=null ? newStream.getCopyright() :"");

		addStream.setVar("extra_isApi","false");
		addStream.setVar("extra_apiDescription",newStream.getVirtualEntityName()!=null ? newStream.getVirtualEntityName() :"");
		addStream.setVar("codiceTenant",newStream.getCodiceTenant()!=null ? newStream.getCodiceTenant() :"");
		addStream.setVar("codiceStream",newStream.getCodiceStream()!=null ? newStream.getCodiceStream() :"");
		addStream.setVar("nomeStream",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("nomeTenant",newStream.getNomeTenant()!=null ? newStream.getNomeTenant() :"");
		addStream.setVar("licence",newStream.getLicence()!=null ? newStream.getLicence() :"");
		addStream.setVar("disclaimer",newStream.getDisclaimer()!=null ? newStream.getDisclaimer() :"");

		addStream.setVar("virtualEntityCode",newStream.getCodiceVirtualEntity()!=null ? newStream.getCodiceVirtualEntity() :"");
		addStream.setVar("virtualEntityName",newStream.getVirtualEntityName()!=null ? newStream.getVirtualEntityName() :"");
		addStream.setVar("virtualEntityDescription",newStream.getVirtualEntityDescription()!=null ? newStream.getVirtualEntityDescription() :"");
		String tags = "";

		if(newStream.getDomainStream()!=null){
			tags+=newStream.getDomainStream();
		}
		if(newStream.getStreamTags()!=null && newStream.getStreamTags().getTag()!=null){
			for(Tag t : newStream.getStreamTags().getTag())
				tags+=","+t.getTagCode();
		}


		addStream.setVar("tags",tags);
		addStream.run();

		return apiFinalName;
	}
	public static String createApiforBulk(Metadata metadata,boolean update) throws Exception{

		String apiName= metadata.getDatasetCode();
		String apiFinalName= metadata.getDatasetCode()+"_odata";

		AddStream addStream = new AddStream();

		ImageProcessor processor = new ImageProcessor();
		String imageBase64 =metadata.getInfo().getIcon();

		String path ="images/";
		String fileName =metadata.getDatasetCode()+".png";

		processor.doProcessOdata(imageBase64, path,fileName);

		//FIXME get the list of roles(tenants) from the stream info
		if("public".equals(metadata.getInfo().getVisibility())){
			addStream.setVar("visibility","public");
			addStream.setVar("roles","");
			addStream.setVar("authType","None");
		}else{
			addStream.setVar("visibility","restricted");

			String ruoli ="";

			if(metadata.getInfo().getTenantssharing()!=null && metadata.getInfo().getTenantssharing().getTenantsharing()!=null){
				for(org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantsharing t : metadata.getInfo().getTenantssharing().getTenantsharing()){
					if(!ruoli.equals(""))
						ruoli+=",";
					ruoli+=t.getTenantCode()+"_subscriber";
				}
			}

			if(!ruoli.contains(metadata.getConfigData().getTenantCode()+"_subscriber")){
				ruoli += metadata.getConfigData().getTenantCode()+"_subscriber";
			}

			addStream.setVar("roles",ruoli);
			addStream.setVar("authType","Application & Application User");
		}




		if(update){
			addStream.setVar("actionAPI","updateAPI");
		}else{
			addStream.setVar("actionAPI","addAPI");
		}

		addStream.setVar("apimanConsoleAddress",Config.getInstance().getConsoleAddress());
		addStream.setVar("username",Config.getInstance().getStoreUsername());
		addStream.setVar("password",Config.getInstance().getStorePassword());
		addStream.setVar("httpok",Config.getInstance().getHttpOk());
		addStream.setVar("ok",Config.getInstance().getResponseOk());

		addStream.setVar("icon",path+fileName);
		addStream.setVar("apiVersion","1.0");
		addStream.setVar("apiName",apiFinalName);
		addStream.setVar("context","/api/"+apiName);//ds_Voc_28;
		addStream.setVar("P","");
		addStream.setVar("endpoint",Config.getInstance().getBaseApiUrl()+apiName);
		addStream.setVar("desc",metadata.getInfo().getDescription()!=null ? metadata.getInfo().getDescription() :"");
		addStream.setVar("copiright",metadata.getInfo().getCopyright()!=null ? metadata.getInfo().getCopyright() :"");

		addStream.setVar("extra_isApi","false");
		addStream.setVar("extra_apiDescription",metadata.getInfo().getDescription()!=null ? metadata.getInfo().getDescription() :"");
		addStream.setVar("codiceTenant",metadata.getConfigData().getTenantCode()!=null ? metadata.getConfigData().getTenantCode() :"");
		addStream.setVar("codiceStream","");
		addStream.setVar("nomeStream","");
		addStream.setVar("nomeTenant",metadata.getConfigData().getTenantCode()!=null ? metadata.getConfigData().getTenantCode() :"");
		addStream.setVar("licence",metadata.getInfo().getLicense()!=null ? metadata.getInfo().getLicense() :"");
		addStream.setVar("disclaimer",metadata.getInfo().getDisclaimer()!=null ? metadata.getInfo().getDisclaimer() :"");
		addStream.setVar("virtualEntityName","");
		addStream.setVar("virtualEntityDescription","");

		String tags = "";
		if(metadata.getInfo().getDataDomain()!=null){
			tags+=metadata.getInfo().getDataDomain();
		}
		if(metadata.getInfo().getTags()!=null){
			for(org.csi.yucca.storage.datamanagementapi.model.metadata.Tag t : metadata.getInfo().getTags())
				tags+=","+t.getTagCode();
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
			addStream.setVar("authType","None");

		}else{
			addStream.setVar("visibility","restricted");
			String ruoli ="";

			if(newStream.getTenantssharing()!=null && newStream.getTenantssharing().getTenantsharing()!=null){
				for(Tenantsharing t : newStream.getTenantssharing().getTenantsharing()){
					if(!ruoli.equals(""))
						ruoli+=",";
					ruoli+=t.getTenantCode()+"_subscriber";
				}
			}

			if(!ruoli.contains(newStream.getCodiceTenant()+"_subscriber")){
				ruoli += newStream.getCodiceTenant()+"_subscriber";
			}
			addStream.setVar("roles",ruoli);
			addStream.setVar("authType","Application & Application User");
		}

		if(update){
			addStream.setVar("actionAPI","updateAPI");
		}else{
			addStream.setVar("actionAPI","addAPI");
		}

		addStream.setVar("apimanConsoleAddress",Config.getInstance().getConsoleAddress());
		addStream.setVar("username",Config.getInstance().getStoreUsername());
		addStream.setVar("password",Config.getInstance().getStorePassword());
		addStream.setVar("httpok",Config.getInstance().getHttpOk());
		addStream.setVar("ok",Config.getInstance().getResponseOk());

		addStream.setVar("icon",path+fileName);
		addStream.setVar("apiVersion","1.0");
		addStream.setVar("apiName",tenant+"."+sensor+"_"+stream+"_stream");
		addStream.setVar("context","/api/topic/output."+tenant+"."+sensor+"_"+stream);
		addStream.setVar("P","");
		addStream.setVar("endpoint",Config.getInstance().getDammiInfo());
		addStream.setVar("desc",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("copiright",newStream.getCopyright()!=null ? newStream.getCopyright() :"");

		addStream.setVar("extra_isApi","false");
		addStream.setVar("extra_apiDescription",newStream.getVirtualEntityName()!=null ? newStream.getVirtualEntityName() :"");
		addStream.setVar("codiceTenant",newStream.getCodiceTenant()!=null ? newStream.getCodiceTenant() :"");
		addStream.setVar("codiceStream",newStream.getCodiceStream()!=null ? newStream.getCodiceStream() :"");
		addStream.setVar("nomeStream",newStream.getNomeStream()!=null ? newStream.getNomeStream() :"");
		addStream.setVar("nomeTenant",newStream.getNomeTenant()!=null ? newStream.getNomeTenant() :"");
		addStream.setVar("licence",newStream.getLicence()!=null ? newStream.getLicence() :"");
		addStream.setVar("disclaimer",newStream.getDisclaimer()!=null ? newStream.getDisclaimer() :"");

		addStream.setVar("virtualEntityCode",newStream.getCodiceVirtualEntity()!=null ? newStream.getCodiceVirtualEntity() :"");
		addStream.setVar("virtualEntityName",newStream.getVirtualEntityName()!=null ? newStream.getVirtualEntityName() :"");
		addStream.setVar("virtualEntityDescription",newStream.getVirtualEntityDescription()!=null ? newStream.getVirtualEntityDescription() :"");
		String tags = "";

		if(newStream.getDomainStream()!=null){
			tags+=newStream.getDomainStream();
		}
		if(newStream.getStreamTags()!=null && newStream.getStreamTags().getTag()!=null){
			for(Tag t : newStream.getStreamTags().getTag())
				tags+=","+t.getTagCode();
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

		publish.setVar("apimanConsoleAddress",Config.getInstance().getConsoleAddress());
		publish.setVar("username",Config.getInstance().getStoreUsername());
		publish.setVar("password",Config.getInstance().getStorePassword());
		publish.setVar("httpok",Config.getInstance().getHttpOk());
		publish.setVar("ok",Config.getInstance().getResponseOk());

		publish.setVar("publishStatus", "PUBLISHED");
		publish.setVar("apiVersion",apiVersion);
		publish.setVar("apiName",apiName);
		publish.setVar("provider",provider);

		publish.run();
		return true;
	}

	public static boolean removeStore(String apiVersion,String apiName,String provider) throws Exception  {

		PublishApi publish = new PublishApi();

		publish.setVar("apimanConsoleAddress",Config.getInstance().getConsoleAddress());
		publish.setVar("username",Config.getInstance().getStoreUsername());
		publish.setVar("password",Config.getInstance().getStorePassword());
		publish.setVar("httpok",Config.getInstance().getHttpOk());
		publish.setVar("ok",Config.getInstance().getResponseOk());

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
