package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class SubscriptionResponse {
	private boolean error;
	private Apis[] apis;

	public SubscriptionUsernameResponse() {

	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Apis[] getApis() {
		return apis;
	}

	public void setApis(Apis[] apis) {
		this.apis = apis;
	}
}
