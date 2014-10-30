package org.csi.yucca.storage.datamanagementapi.model;

import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Trash extends AbstractEntity {
	private String trashCollection;
	private String trashHost;
	private String trashPort;
	private String trashDatabase;
	private TrashInfo[] trashInfo;

	public Trash() {
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.toJson(this);
	}

	public String getTrashCollection() {
		return trashCollection;
	}

	public void setTrashCollection(String trashCollection) {
		this.trashCollection = trashCollection;
	}

	public String getTrashHost() {
		return trashHost;
	}

	public void setTrashHost(String trashHost) {
		this.trashHost = trashHost;
	}

	public String getTrashPort() {
		return trashPort;
	}

	public void setTrashPort(String trashPort) {
		this.trashPort = trashPort;
	}

	public String getTrashDatabase() {
		return trashDatabase;
	}

	public void setTrashDatabase(String trashDatabase) {
		this.trashDatabase = trashDatabase;
	}

	public TrashInfo[] getTrashInfo() {
		return trashInfo;
	}

	public void setTrashInfo(TrashInfo[] trashInfo) {
		this.trashInfo = trashInfo;
	}

}
