package org.csi.yucca.storage.datamanagementapi.model;

public class ConfigData extends AbstractEntity{
	private String idDataset;
	private String tenant;
	private String collection;
	private String host;
	private String port;
	private String database;
	private String type;
	private String subtype;
	private String entityNameSpace;
	private String datasetversion;
	private String current;
	private Trash trash;

	public String getIdDataset() {
		return idDataset;
	}

	public void setIdDataset(String idDataset) {
		this.idDataset = idDataset;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
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

	public String getDatasetversion() {
		return datasetversion;
	}

	public void setDatasetversion(String datasetversion) {
		this.datasetversion = datasetversion;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public Trash getTrash() {
		return trash;
	}

	public void setTrash(Trash trash) {
		this.trash = trash;
	}
}