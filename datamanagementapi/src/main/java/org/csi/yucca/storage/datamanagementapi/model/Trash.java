package org.csi.yucca.storage.datamanagementapi.model;

import java.util.List;

public class Trash extends AbstractEntity{
	private String trashCollection;
	private String trashHost;
	private String trashPort;
	private String trashDatabase;
	private List<TrashInfo> trashInfo;

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

	public List<TrashInfo> getTrashInfo() {
		return trashInfo;
	}

	public void setTrashInfo(List<TrashInfo> trashInfo) {
		this.trashInfo = trashInfo;
	}

}
