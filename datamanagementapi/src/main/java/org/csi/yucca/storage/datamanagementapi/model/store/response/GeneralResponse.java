package org.csi.yucca.storage.datamanagementapi.model.store.response;

public class GeneralResponse {
	private boolean error;
	private String message;
	private Data data;

	public GeneralResponse() {

	}

	public boolean getError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
}
