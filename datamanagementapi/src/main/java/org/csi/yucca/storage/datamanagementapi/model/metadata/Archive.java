package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Archive extends AbstractEntity {
	private String archiveCollection;
	private String archiveDatabase;
	private ArchiveInfo[] archiveInfo;

	public Archive() {
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
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
