package org.csi.yucca.storage.datamanagementapi.model.metadata;

import java.util.Date;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Opendata {

	private boolean isOpendata;
	private String sourceId;
	private String author;
	private Long dataUpdateDate;
	private Date metadaCreateDate;
	private Date metadaUpdateDate;
	private String language;
	private String opendataupdatefrequency;

	public static Opendata fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, Opendata.class);
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public Opendata() {
		super();
	}

	public boolean isOpendata() {
		return isOpendata;
	}

	public void setOpendata(boolean isOpendata) {
		this.isOpendata = isOpendata;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Long getDataUpdateDate() {
		return dataUpdateDate;
	}

	public void setDataUpdateDate(Long dataUpdateDate) {
		this.dataUpdateDate = dataUpdateDate;
	}

	public Date getMetadaCreateDate() {
		return metadaCreateDate;
	}

	public void setMetadaCreateDate(Date metadaCreateDate) {
		this.metadaCreateDate = metadaCreateDate;
	}

	public Date getMetadaUpdateDate() {
		return metadaUpdateDate;
	}

	public void setMetadaUpdateDate(Date metadaUpdateDate) {
		this.metadaUpdateDate = metadaUpdateDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOpendataupdatefrequency() {
		return opendataupdatefrequency;
	}

	public void setOpendataupdatefrequency(String opendataupdatefrequency) {
		this.opendataupdatefrequency = opendataupdatefrequency;
	}

}