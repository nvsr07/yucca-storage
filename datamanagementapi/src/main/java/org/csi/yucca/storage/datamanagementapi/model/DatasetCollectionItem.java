package org.csi.yucca.storage.datamanagementapi.model;

import org.csi.yucca.storage.datamanagementapi.util.json.IgnoredJSON;

import com.google.gson.Gson;
import com.mongodb.util.JSON;

public class DatasetCollectionItem extends AbstractEntity {
	@IgnoredJSON
	private String id;
	private ConfigData configData;
	private Dataset dataset;

	public DatasetCollectionItem(String json) {
		Gson gson = new Gson();
		gson.fromJson(JSON.serialize(json), DatasetCollectionItem.class);
	}

	public DatasetCollectionItem() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ConfigData getConfigData() {
		return configData;
	}

	public void setConfigData(ConfigData configData) {
		this.configData = configData;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

}
