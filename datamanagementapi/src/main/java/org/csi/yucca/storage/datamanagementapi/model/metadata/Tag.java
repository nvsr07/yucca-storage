package org.csi.yucca.storage.datamanagementapi.model.metadata;

import java.io.Serializable;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Tag extends AbstractEntity  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tagCode;

	public Tag() {
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public String getTagCode() {
		return tagCode;
	}

	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}
	

}
