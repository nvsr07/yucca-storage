package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.model.metadata.AbstractEntity;
import org.csi.yucca.storage.datamanagementapi.model.metadata.TenantList;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class TenantsShare extends AbstractEntity {
	private TenantList[] tenantList;

	public TenantsShare() {
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public TenantList[] getTenantList() {
		return tenantList;
	}

	public void setTenantList(TenantList[] tenantList) {
		this.tenantList = tenantList;
	}


}
