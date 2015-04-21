package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.model.metadata.AbstractEntity;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Tenantsharing;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Tenantssharing extends AbstractEntity {
	private Tenantsharing[] tenantsharing;

	public Tenantssharing() {
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public Tenantsharing[] getTenantsharing() {
		return tenantsharing;
	}

	public void setTenantsharing(Tenantsharing[] tenantsharing) {
		this.tenantsharing = tenantsharing;
	}


}
