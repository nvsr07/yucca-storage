package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class Apis {

	private String apiName;
    private String apiVersion;
    private String apiProvider;
    private String description;
    private String subscribedTier;
    private String status;
    
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	public String getApiProvider() {
		return apiProvider;
	}
	public void setApiProvider(String apiProvider) {
		this.apiProvider = apiProvider;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSubscribedTier() {
		return subscribedTier;
	}
	public void setSubscribedTier(String subscribedTier) {
		this.subscribedTier = subscribedTier;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
