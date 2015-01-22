package org.csi.yucca.storage.datamanagementapi.service;


import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.AddStream;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.LoginOut;
import org.csi.yucca.storage.datamanagementapi.apimanager.store.PublishApi;
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
				createApiforStream(newStream,apiName);
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
				createStream(newStream);
			}

		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}

		return JSON.parse("{OK:1}").toString();
	}

	public static boolean createApiforStream(Stream newStream,String apiName) throws ClientProtocolException, IOException{

		AddStream addStream = new AddStream();

		ImageProcessor processor = new ImageProcessor();
		String imageBase64 =newStream.getStreamIcon();
		String path ="D:/";
		String fileName =newStream.getCodiceStream();

		processor.doProcessOdata(imageBase64, path,fileName);

		//FIXME get the list of roles(tenants) from the stream info
		if("public".equals(newStream.getVisibility())){
			addStream.setVar("visibility","public");
			addStream.setVar("roles","");
		}else{
			addStream.setVar("visibility","private");
			addStream.setVar("roles",newStream.getCodiceTenant()+"_subscriber");
		}

		addStream.setVar("icon",path+fileName+"-odata.png");
		addStream.setVar("apiVersion",newStream.getDeploymentVersion()+"");
		addStream.setVar("apiName",apiName+"_odata");
		addStream.setVar("context","/api/"+apiName);//ds_Voc_28;
		addStream.setVar("P","");
		addStream.setVar("endpoint","http://api.smartdatanet.it/odata/SmartDataOdataService.svc/"+apiName);
		addStream.setVar("desc",newStream.getNomeStream()+"");
		addStream.setVar("copiright",newStream.getCopyright()+"");

		addStream.run();
		return true;
	}

	public static boolean createStream(Stream newStream) throws ClientProtocolException, IOException{

		String tenant = newStream.getCodiceTenant();
		String sensor = newStream.getCodiceVirtualEntity();
		String stream = newStream.getCodiceStream();

		AddStream addStream = new AddStream();


		ImageProcessor processor = new ImageProcessor();
		String imageBase64 =newStream.getStreamIcon();
		String path ="D:/";
		String fileName =newStream.getCodiceStream();
		processor.doProcessStream(imageBase64, path,fileName);

		//FIXME get the list of roles(tenants) from the stream info
		if("public".equals(newStream.getVisibility())){
			addStream.setVar("visibility","public");
			addStream.setVar("roles","");
		}else{
			addStream.setVar("visibility","private");
			addStream.setVar("roles",newStream.getCodiceTenant()+"_subscriber");
		}

		addStream.setVar("icon",path+fileName);
		addStream.setVar("apiVersion",newStream.getDeploymentVersion()+"");
		addStream.setVar("apiName",tenant+"."+sensor+"_"+stream+"_stream");
		addStream.setVar("context","/api/topic/output."+tenant+"."+sensor+"_"+stream);
		addStream.setVar("P","");
		addStream.setVar("endpoint","http://api.smartdatanet.it/dammiInfo");
		addStream.setVar("desc",newStream.getNomeStream());
		addStream.setVar("copiright",newStream.getCopyright());

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
			
			PublishApi publish = new PublishApi();
			publish.setVar("publishStatus", "PUBLISHED");
			publish.setVar("apiVersion",apiVersion);
			publish.setVar("apiName",apiName);
			publish.setVar("provider",provider);
			
			publish.run();

		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
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
			
			PublishApi publish = new PublishApi();
			publish.setVar("publishStatus", "BLOCKED");
			publish.setVar("apiVersion",apiVersion);
			publish.setVar("apiName",apiName);
			publish.setVar("provider",provider);
			
			publish.run();
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			return JSON.parse("{KO:1}").toString();
		}
		return JSON.parse("{OK:1}").toString();
	}

}
