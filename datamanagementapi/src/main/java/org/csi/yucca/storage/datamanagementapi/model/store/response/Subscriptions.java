package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class Subscriptions {

	private int id;
    private String name;
    private String callbackUrl;
    private String prodKey;
    private String prodConsumerKey;
    private String prodConsumerSecret;
    private String prodRegenarateOption;
    private String prodAuthorizedDomains;
    private long prodValidityTime;
    private String sandboxKey;
    private String sandboxConsumerKey;
    private String sandboxConsumerSecret;
    private String sandboxKeyState;
    private String sandboxAuthorizedDomains;
    private long sandValidityTime;
    private Subscription[] subscriptions;
    
    public String toString() {
    	return "Tenant subscription ==> name = " + name + ", list of subscription: " + subscriptions.toString();
    }
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
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
	public String getProdRegenarateOption() {
		return prodRegenarateOption;
	}
	public void setProdRegenarateOption(String prodRegenarateOption) {
		this.prodRegenarateOption = prodRegenarateOption;
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
	public String getSandboxKeyState() {
		return sandboxKeyState;
	}
	public void setSandboxKeyState(String sandboxKeyState) {
		this.sandboxKeyState = sandboxKeyState;
	}
	public String getSandboxAuthorizedDomains() {
		return sandboxAuthorizedDomains;
	}
	public void setSandboxAuthorizedDomains(String sandboxAuthorizedDomains) {
		this.sandboxAuthorizedDomains = sandboxAuthorizedDomains;
	}
	public long getSandValidityTime() {
		return sandValidityTime;
	}
	public void setSandValidityTime(long sandValidityTime) {
		this.sandValidityTime = sandValidityTime;
	}
	public Subscription[] getSubscriptions() {
		return subscriptions;
	}
	public void setSubscriptions(Subscription[] subscriptions) {
		this.subscriptions = subscriptions;
	}
}
