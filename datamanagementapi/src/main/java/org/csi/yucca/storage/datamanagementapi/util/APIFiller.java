package org.csi.yucca.storage.datamanagementapi.util;

import java.util.ArrayList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.api.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.api.Dataset;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;

public class APIFiller {

	
	static public MyApi fillApi(Stream stream,Integer id) {
		
		System.out.println("FILL API OBJECT");
		
		MyApi api = new MyApi();
		Double idapi = Math.random()*100000;
		
		
		String apiCode = "ds_"+stream.getCodiceStream()+"-"+id;//apiCode
		api.setIdApi(idapi.intValue());
		api.setApiDescription("Dataset " +stream.getNomeStream());
		api.setApiName("Dataset " +stream.getNomeStream());
		api.setApiCode(apiCode);
		
		
		ConfigData configData = new ConfigData();
		
		configData.setIdTenant(stream.getIdTenant());
		configData.setTenantCode(stream.getCodiceTenant());
		configData.setType("api");
		configData.setSubtype("apiMultiStream");
		configData.setEntityNameSpace("it.csi.smartdata.odata.iotnet."+stream.getCodiceTenant()+"."+apiCode);
		
		api.setConfigData(configData );
		
		List<Dataset> dataset = new ArrayList<Dataset>();
		Dataset ds = new Dataset();
		
		ds.setDatasetVersion(stream.getDeploymentVersion());
		ds.setIdDataset(id);
		ds.setIdTenant(stream.getIdTenant());
		ds.setStreamCode(stream.getCodiceStream());
		ds.setIdStream(stream.getIdStream());
		ds.setTenantCode(stream.getCodiceTenant());
		ds.setVirtualEntityCode(stream.getCodiceVirtualEntity());
		dataset.add(ds);		
	
		api.setDataset(dataset );
		
		return api;
	}
	
}
