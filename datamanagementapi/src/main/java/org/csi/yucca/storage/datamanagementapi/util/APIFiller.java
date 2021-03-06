package org.csi.yucca.storage.datamanagementapi.util;

import java.util.ArrayList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.api.ConfigData;
import org.csi.yucca.storage.datamanagementapi.model.api.Dataset;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;

public class APIFiller {

	static public MyApi fillApi(Stream stream, Metadata metadata) {

		System.out.println("FILL API OBJECT");

		MyApi api = new MyApi();
		
		//String apiCode = "ds_" + stream.getCodiceStream() + "_" + idDataset;// apiCode
		String apiCode = metadata.getDatasetCode();
		api.setApiDescription("Dataset " + stream.getNomeStream());
		api.setApiName("Dataset " + stream.getNomeStream());
		api.setApiCode(apiCode);

		ConfigData configData = new ConfigData();

		configData.setIdTenant(stream.getIdTenant());
		configData.setTenantCode(stream.getCodiceTenant());
		configData.setType("api");
		configData.setSubtype("apiMultiStream");
		if (stream.getIdTipoVe() == Constants.VIRTUAL_ENTITY_TWITTER_TYPE_ID) {
			configData.setSubtype("apiMultiSocial");
		} else
			configData.setSubtype("apiMultiStream");
		//configData.setEntityNameSpace("it.csi.smartdata.odata." + stream.getCodiceTenant() + "." + apiCode);
		

		api.setConfigData(configData);

		List<Dataset> dataset = new ArrayList<Dataset>();
		Dataset ds = new Dataset();

		ds.setDatasetVersion(stream.getDeploymentVersion());
		ds.setIdDataset(metadata.getIdDataset());
		ds.setIdTenant(stream.getIdTenant());
		ds.setStreamCode(stream.getCodiceStream());
		ds.setIdStream(stream.getIdStream());
		ds.setTenantCode(stream.getCodiceTenant());
		ds.setVirtualEntityCode(stream.getCodiceVirtualEntity());
		dataset.add(ds);

		api.setDataset(dataset);
		api.generateNameSpace();
		return api;
	}

}
