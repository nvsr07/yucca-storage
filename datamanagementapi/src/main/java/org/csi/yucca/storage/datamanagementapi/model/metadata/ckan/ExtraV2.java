package org.csi.yucca.storage.datamanagementapi.model.metadata.ckan;

import java.util.LinkedList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class ExtraV2 {

	private String package_id;
	private String topic;
	private String hidden_field;
	private String metadata_created;
	private String metadata_modified;
	private String package_created;
	private String package_modified;
	private String tag;
	private String title;
	private String description;
	private String license_id;
	private String package_type;
	private List<String> resource;

	public ExtraV2() {
		super();
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getHidden_field() {
		return hidden_field;
	}

	public void setHidden_field(String hidden_field) {
		this.hidden_field = hidden_field;
	}

	public String getMetadata_created() {
		return metadata_created;
	}

	public void setMetadata_created(String metadata_created) {
		this.metadata_created = metadata_created;
	}

	public String getMetadata_modified() {
		return metadata_modified;
	}

	public void setMetadata_modified(String metadata_modified) {
		this.metadata_modified = metadata_modified;
	}

	public String getPackage_created() {
		return package_created;
	}

	public void setPackage_created(String package_created) {
		this.package_created = package_created;
	}

	public String getPackage_modified() {
		return package_modified;
	}

	public void setPackage_modified(String package_modified) {
		this.package_modified = package_modified;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLicense_id() {
		return license_id;
	}

	public void setLicense_id(String license_id) {
		this.license_id = license_id;
	}

	public String getPackage_type() {
		return package_type;
	}

	public void setPackage_type(String package_type) {
		this.package_type = package_type;
	}

	public String getPackage_id() {
		return package_id;
	}

	public void setPackage_id(String package_id) {
		this.package_id = package_id;
	}

	public List<String> getResource() {
		return resource;
	}

	public void setResource(List<String> resource) {
		this.resource = resource;
	}

	public void addResource(Resource newResource) {
		if (resource == null)
			resource = new LinkedList<String>();
		resource.add("format=" + newResource.getFormat() + "||url=" + newResource.getUrl());
	}

}
