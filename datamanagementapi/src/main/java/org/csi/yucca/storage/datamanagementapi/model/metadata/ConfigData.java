package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConfigData extends AbstractEntity {

	private Integer idTenant;
	private String tenantCode;
	private String collection;
	private String database;
	private String type;
	private String subtype;
	private String entityNameSpace;
	private String datasetStatus;
	private String current;
	private Archive archive;

	public ConfigData() {
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.toJson(this);
	}

	public Integer getIdTenant() {
		return idTenant;
	}

	public void setIdTenant(Integer idTenant) {
		this.idTenant = idTenant;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getEntityNameSpace() {
		return entityNameSpace;
	}

	public void setEntityNameSpace(String entityNameSpace) {
		this.entityNameSpace = entityNameSpace;
	}

	public String getDatasetStatus() {
		return datasetStatus;
	}

	public void setDatasetStatus(String datasetStatus) {
		this.datasetStatus = datasetStatus;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public Archive getArchive() {
		return archive;
	}

	public void setArchive(Archive archive) {
		this.archive = archive;
	}

}