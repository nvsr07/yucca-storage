package org.csi.yucca.storage.datamanagementapi.model.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class MyApi {

	private String id;

	@Expose
	private Long idApi;
	@Expose
	private String apiName;
	@Expose
	private String apiCode;
	@Expose
	private String apiDescription;
	@Expose
	private ConfigData configData;
	@Expose
	private List<Dataset> dataset = new ArrayList<Dataset>();

	/**
	 * 
	 * @return The idApi
	 */
	public Long getIdApi() {
		return idApi;
	}

	/**
	 * 
	 * @param idApi
	 *            The idApi
	 */
	public void setIdApi(Long idApi) {
		this.idApi = idApi;
	}

	/**
	 * 
	 * @return The apiName
	 */
	public String getApiName() {
		return apiName;
	}

	/**
	 * 
	 * @param apiName
	 *            The apiName
	 */
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	/**
	 * 
	 * @return The apiDescription
	 */
	public String getApiDescription() {
		return apiDescription;
	}

	/**
	 * 
	 * @param apiDescription
	 *            The apiDescription
	 */
	public void setApiDescription(String apiDescription) {
		this.apiDescription = apiDescription;
	}

	/**
	 * 
	 * @return The configData
	 */
	public ConfigData getConfigData() {
		return configData;
	}

	/**
	 * 
	 * @param configData
	 *            The configData
	 */
	public void setConfigData(ConfigData configData) {
		this.configData = configData;
	}

	/**
	 * 
	 * @return The dataset
	 */
	public List<Dataset> getDataset() {
		return dataset;
	}

	/**
	 * 
	 * @param dataset
	 *            The dataset
	 */
	public void setDataset(List<Dataset> dataset) {
		this.dataset = dataset;
	}

	public String getApiCode() {
		return apiCode;
	}

	public void setApiCode(String apiCode) {
		this.apiCode = apiCode;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static MyApi fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, MyApi.class);
	}
	
	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public void generateNameSpace() {
		if (getApiCode() != null && getConfigData() != null) {
			String nameSpace = Constants.API_NAMESPACE_BASE + "." + getConfigData().getTenantCode() + "." + getApiCode();
			getConfigData().setEntityNameSpace(nameSpace);
		}
	}

	
	public static MyApi createFromMetadataDataset(Metadata metadata){
		MyApi api = new MyApi();
		if(metadata!=null){
			if(metadata.getInfo()!=null && metadata.getInfo().getDescription()!=null)
				api.setApiDescription("API - " + metadata.getInfo().getDescription());
			
			api.setApiCode(metadata.getDatasetCode());
			ConfigData configData = new ConfigData();
			configData.setIdTenant(metadata.getConfigData().getIdTenant());
			configData.setTenantCode(metadata.getConfigData().getTenantCode());
			configData.setEntityNameSpace(metadata.getConfigData().getEntityNameSpace());
			
			api.setConfigData(configData);
			
			Dataset dataset = new Dataset();
			dataset.setIdDataset(metadata.getIdDataset());
			dataset.setIdTenant(metadata.getConfigData().getIdTenant());
			dataset.setTenantCode(metadata.getConfigData().getTenantCode());
			dataset.setDatasetVersion(metadata.getDatasetVersion());
			List<Dataset> datasetList = new LinkedList<Dataset>();
			datasetList.add(dataset);
			api.setDataset(datasetList);
		}
		return api;
	}
	



}
