package org.csi.yucca.storage.datamanagementapi.service.response;

import java.util.LinkedList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class CreateDatasetResponse {

	public static final int STATUS_DATASET_NOT_CREATED = 0;
	public static final int STATUS_DATASET_CREATED = 1;
	public static final int STATUS_DATASET_PUT_INTO_STORE = 2;
	public static final int STATUS_DATASET_DATA_UPLOAD = 3;
	
	private List<ErrorMessage> errors;
	private Metadata metadata;
	private MyApi api;
	private int datasetStatus = STATUS_DATASET_NOT_CREATED;

	public CreateDatasetResponse() {
		super();
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public List<ErrorMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorMessage> errors) {
		this.errors = errors;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public MyApi getApi() {
		return api;
	}

	public void setApi(MyApi api) {
		this.api = api;
	}

	public void addErrorMessage(ErrorMessage errorMessage) {
		if (errors == null)
			errors = new LinkedList<ErrorMessage>();
		errors.add(errorMessage);
	}

	public int getDatasetStatus() {
		return datasetStatus;
	}

	public void setDatasetStatus(int datasetStatus) {
		this.datasetStatus = datasetStatus;
	}
}
