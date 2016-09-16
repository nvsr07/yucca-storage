package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class SubscriptionUsernameResponse {
	private boolean error;
	private UsernameResult[] result;

	public SubscriptionUsernameResponse() {

	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public UsernameResult[] getResult() {
		return result;
	}

	public void setResult(UsernameResult[] result) {
		this.result = result;
	}
}