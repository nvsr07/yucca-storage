package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import com.google.gson.annotations.Expose;

public class OpendataInfo {
	@Expose
    private Boolean isOpendata;
	@Expose
    private String author;
	@Expose
    private Long dataUpdateDate;
	@Expose
    private String language;
	public Boolean getIsOpendata() {
		return isOpendata;
	}
	public void setIsOpendata(Boolean isOpendata) {
		this.isOpendata = isOpendata;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Long getDataUpdateDate() {
		return dataUpdateDate;
	}
	public void setDataUpdateDate(Long dataUpdateDate) {
		this.dataUpdateDate = dataUpdateDate;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
