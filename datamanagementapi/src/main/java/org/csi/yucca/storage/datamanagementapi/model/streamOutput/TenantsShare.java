package org.csi.yucca.storage.datamanagementapi.model.streamOutput;


import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class TenantsShare {

    @Expose
    private List<TenantList> tenantList = new ArrayList<TenantList>();

    /**
     * 
     * @return
     *     The tenantList
     */
    public List<TenantList> getTenantList() {
        return tenantList;
    }

    /**
     * 
     * @param tenantList
     *     The tenantList
     */
    public void setTenantList(List<TenantList> tenantList) {
        this.tenantList = tenantList;
    }

}
