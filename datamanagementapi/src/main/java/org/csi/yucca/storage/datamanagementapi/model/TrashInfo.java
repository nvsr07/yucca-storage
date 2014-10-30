package org.csi.yucca.storage.datamanagementapi.model;

import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TrashInfo extends AbstractEntity {
	public String trashRevision;
	private String trashDate;

	public TrashInfo() {
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		return gson.toJson(this);
	}

	public String getTrashRevision() {
		return trashRevision;
	}

	public void setTrashRevision(String trashRevision) {
		this.trashRevision = trashRevision;
	}

	public String getTrashDate() {
		return trashDate;
	}

	public void setTrashDate(String trashDate) {
		this.trashDate = trashDate;
	}

}
