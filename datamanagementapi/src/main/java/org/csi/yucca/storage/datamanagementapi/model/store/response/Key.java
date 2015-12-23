package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class Key {
	private String validityTime;
	private String consumerKey;
	private String accessToken;
	private String keyState;
	private String consumerSecret;
	private String enableRegenarate;
	private String accessallowdomains;

	public Key() {

	}

	public String getValidityTime() {
		return validityTime;
	}

	public void setValidityTime(String validityTime) {
		this.validityTime = validityTime;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getKeyState() {
		return keyState;
	}

	public void setKeyState(String keyState) {
		this.keyState = keyState;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public String getEnableRegenarate() {
		return enableRegenarate;
	}

	public void setEnableRegenarate(String enableRegenarate) {
		this.enableRegenarate = enableRegenarate;
	}

	public String getAccessallowdomains() {
		return accessallowdomains;
	}

	public void setAccessallowdomains(String accessallowdomains) {
		this.accessallowdomains = accessallowdomains;
	}

}
