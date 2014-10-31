package org.csi.yucca.storage.datamanagementapi.model;

import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Archive extends AbstractEntity {
	private String archiveCollection;
	private String archiveDatabase;
	private ArchiveInfo[] archiveInfo;

	public Archive() {
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.toJson(this);
	}

	public String getArchiveCollection() {
		return archiveCollection;
	}

	public void setArchiveCollection(String archiveCollection) {
		this.archiveCollection = archiveCollection;
	}

	public String getArchiveDatabase() {
		return archiveDatabase;
	}

	public void setArchiveDatabase(String archiveDatabase) {
		this.archiveDatabase = archiveDatabase;
	}

	public ArchiveInfo[] getArchiveInfo() {
		return archiveInfo;
	}

	public void setArchiveInfo(ArchiveInfo[] archiveInfo) {
		this.archiveInfo = archiveInfo;
	}

}
