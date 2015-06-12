
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Tenantssharing {

    @Expose
    private List<Tenantsharing> tenantsharing = new ArrayList<Tenantsharing>();

    /**
     * 
     * @return
     *     The tenantsharing
     */
    public List<Tenantsharing> getTenantsharing() {
        return tenantsharing;
    }

    /**
     * 
     * @param tenantsharing
     *     The tenantsharing
     */
    public void setTenantsharing(List<Tenantsharing> tenantsharing) {
        this.tenantsharing = tenantsharing;
    }

}
