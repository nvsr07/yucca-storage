package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class SubscriptionByUsernameResponse {
	private boolean error;
	private Subs[] subscriptions;

	public SubscriptionByUsernameResponse() {

	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Subs[] getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Subs[] subscriptions) {
		this.subscriptions = subscriptions;
	}
}
