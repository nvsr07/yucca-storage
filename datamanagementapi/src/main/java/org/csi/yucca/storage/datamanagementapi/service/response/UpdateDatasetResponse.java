package org.csi.yucca.storage.datamanagementapi.service.response;

import java.util.LinkedList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class UpdateDatasetResponse {
	private List<ErrorMessage> errors;
	private Metadata metadata;

	public UpdateDatasetResponse() {
		super();
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public List<ErrorMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorMessage> errors) {
		this.errors = errors;
	}

	public void addErrorMessage(ErrorMessage errorMessage) {
		if (errors == null)
			errors = new LinkedList<ErrorMessage>();
		errors.add(errorMessage);
	}
	
	public boolean hasError(){
		return getErrors()!=null && getErrors().size()>0;
	}
}
