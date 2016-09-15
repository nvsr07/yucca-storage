package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class Subscription {

	private String name;
	private String provider;
	private String version;
	private String status;
	private String tier;
	private String subStatus;
	private String thumburl;
	private String context;
	private String prodKey;
	private String prodConsumerKey;
	private String prodConsumerSecret;
	private String prodAuthorizedDomains;
	private long prodValidityTime;
	private String sandboxKey;
	private String sandboxConsumerKey;
	private String sandboxConsumerSecret;
	private String sandAuthorizedDomains;
	private long sandValidityTime;
	private String hasMultipleEndpoints;
	
    public String toString() {
    	return "    ===> name = " + name + "\n";
    }
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTier() {
		return tier;
	}
	public void setTier(String tier) {
		this.tier = tier;
	}
	public String getSubStatus() {
		return subStatus;
	}
	public void setSubStatus(String subStatus) {
		this.subStatus = subStatus;
	}
	public String getThumburl() {
		return thumburl;
	}
	public void setThumburl(String thumburl) {
		this.thumburl = thumburl;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getProdKey() {
		return prodKey;
	}
	public void setProdKey(String prodKey) {
		this.prodKey = prodKey;
	}
	public String getProdConsumerKey() {
		return prodConsumerKey;
	}
	public void setProdConsumerKey(String prodConsumerKey) {
		this.prodConsumerKey = prodConsumerKey;
	}
	public String getProdConsumerSecret() {
		return prodConsumerSecret;
	}
	public void setProdConsumerSecret(String prodConsumerSecret) {
		this.prodConsumerSecret = prodConsumerSecret;
	}
	public String getProdAuthorizedDomains() {
		return prodAuthorizedDomains;
	}
	public void setProdAuthorizedDomains(String prodAuthorizedDomains) {
		this.prodAuthorizedDomains = prodAuthorizedDomains;
	}
	public long getProdValidityTime() {
		return prodValidityTime;
	}
	public void setProdValidityTime(long prodValidityTime) {
		this.prodValidityTime = prodValidityTime;
	}
	public String getSandboxKey() {
		return sandboxKey;
	}
	public void setSandboxKey(String sandboxKey) {
		this.sandboxKey = sandboxKey;
	}
	public String getSandboxConsumerKey() {
		return sandboxConsumerKey;
	}
	public void setSandboxConsumerKey(String sandboxConsumerKey) {
		this.sandboxConsumerKey = sandboxConsumerKey;
	}
	public String getSandboxConsumerSecret() {
		return sandboxConsumerSecret;
	}
	public void setSandboxConsumerSecret(String sandboxConsumerSecret) {
		this.sandboxConsumerSecret = sandboxConsumerSecret;
	}
	public String getSandAuthorizedDomains() {
		return sandAuthorizedDomains;
	}
	public void setSandAuthorizedDomains(String sandAuthorizedDomains) {
		this.sandAuthorizedDomains = sandAuthorizedDomains;
	}
	public long getSandValidityTime() {
		return sandValidityTime;
	}
	public void setSandValidityTime(long sandValidityTime) {
		this.sandValidityTime = sandValidityTime;
	}
	public String getHasMultipleEndpoints() {
		return hasMultipleEndpoints;
	}
	public void setHasMultipleEndpoints(String hasMultipleEndpoints) {
		this.hasMultipleEndpoints = hasMultipleEndpoints;
	}
    	
    	
}
