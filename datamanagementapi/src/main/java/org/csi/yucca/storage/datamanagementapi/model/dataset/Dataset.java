package org.csi.yucca.storage.datamanagementapi.model.dataset;

import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Dataset extends AbstractEntity {
	private String id;
	private ConfigData configData;
	private Metadata metadata;

	public Dataset() {
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.toJson(this);
	}

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

	public Metadata getDataset() {
		return metadata;
	}

	public void setDataset(Metadata metadata) {
		this.metadata = metadata;
	}

	public static Dataset fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Dataset.class);
	}

}
