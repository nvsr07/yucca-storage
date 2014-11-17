package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MetadataWithExtraAttribute {

	private String apiPath;
	private String apiMetadataPath;
	private String apiMeasurePath;
	private Metadata metadata;

	public static MetadataWithExtraAttribute fromJson(String json) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.fromJson(json, MetadataWithExtraAttribute.class);
	}

	public MetadataWithExtraAttribute() {
	}

	public MetadataWithExtraAttribute(Metadata metadata, MyApi api, String baseApiPath) {
		this.setMetadata(metadata);
		setApiPath(baseApiPath + api.getApiCode() + "/");
		setApiMetadataPath(getApiPath() + "$metadata");
		setApiMeasurePath(getApiPath() + "$measure");

	}

	public String toJson() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.toJson(this);
	}

	public String getApiPath() {
		return apiPath;
	}

	public String getApiMetadataPath() {
		return apiMetadataPath;
	}

	public String getApiMeasurePath() {
		return apiMeasurePath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public void setApiMetadataPath(String apiMetadataPath) {
		this.apiMetadataPath = apiMetadataPath;
	}

	public void setApiMeasurePath(String apiMeasurePath) {
		this.apiMeasurePath = apiMeasurePath;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

}
