package org.csi.yucca.storage.datamanagementapi.util;

import java.util.ArrayList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.api.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.api.Dataset;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;

public class APIFiller {

	
	static public MyApi fillApi(Stream stream) {
		
		System.out.println("FILL API OBJECT");
		
		MyApi api = new MyApi();
		Double idapi = Math.random();
		api.setIdApi(idapi.intValue());
		api.setApiDescription("Api for "+stream.getNomeStream());
		api.setApiName("Api for"+stream.getCodiceStream());
		
		ConfigData configData = new ConfigData();
		
		configData.setIdTenant(stream.getIdTenant());
		configData.setTenantCode(stream.getCodiceTenant());
		configData.setType("api");
		configData.setSubtype("apiMultiStream");
		configData.setEntityNameSpace("it.csi.smartdata.odata.iotnet"+stream.getCodiceTenant()+stream.getCodiceStream());
		
		api.setConfigData(configData );
		
		List<Dataset> dataset = new ArrayList<Dataset>();
		Dataset ds = new Dataset();
		
		ds.setDatasetVersion(stream.getDeploymentVersion());
		ds.setIdDataset(stream.getCodiceStream());
		ds.setIdTenant(stream.getIdTenant());
		ds.setStreamCode(stream.getCodiceStream());
		ds.setTenantCode(stream.getCodiceTenant());
		ds.setVirtualEntityCode(stream.getCodiceVirtualEntity());
		dataset.add(ds);		
	
		api.setDataset(dataset );
		
		return api;
	}
	
}
