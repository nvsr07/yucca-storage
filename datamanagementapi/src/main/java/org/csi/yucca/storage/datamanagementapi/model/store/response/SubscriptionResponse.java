package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class SubscriptionResponse {
	private boolean error;
	private Subscriptions[] subscriptions;

	public SubscriptionResponse() {

	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Subscriptions[] getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Subscriptions[] subscriptions) {
		this.subscriptions = subscriptions;
	}
}
