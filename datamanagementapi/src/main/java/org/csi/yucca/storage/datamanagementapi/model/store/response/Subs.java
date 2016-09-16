package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class Subs {

	private String application;
	private int applicationId;
	private String prodKey;
	private String sandboxKey;
	
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public int getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}
	public String getProdKey() {
		return prodKey;
	}
	public void setProdKey(String prodKey) {
		this.prodKey = prodKey;
	}
	public String getSandboxKey() {
		return sandboxKey;
	}
	public void setSandboxKey(String sandboxKey) {
		this.sandboxKey = sandboxKey;
	}
}
