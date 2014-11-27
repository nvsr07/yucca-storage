package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class MetadataWithExtraAttribute {

	private String apiUrl;
	private String apiMetadataUrl;
	private String apiMeasureUrl;
	private Metadata metadata;

	public static MetadataWithExtraAttribute fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, MetadataWithExtraAttribute.class);
	}

	public MetadataWithExtraAttribute() {
	}

	public MetadataWithExtraAttribute(Metadata metadata, MyApi api, String baseApiUrl) {
		this.setMetadata(metadata);
		setApiUrl(baseApiUrl + api.getApiCode() + "/");
		setApiMetadataUrl(getApiUrl() + "$metadata");
		setApiMeasureUrl(getApiUrl() + "$measure");

	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public String getApiMetadataUrl() {
		return apiMetadataUrl;
	}

	public String getApiMeasureUrl() {
		return apiMeasureUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public void setApiMetadataUrl(String apiMetadataUrl) {
		this.apiMetadataUrl = apiMetadataUrl;
	}

	public void setApiMeasureUrl(String apiMeasureUrl) {
		this.apiMeasureUrl = apiMeasureUrl;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

}
