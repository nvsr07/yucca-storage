
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

public class TenantList {

    @Expose
    private Integer idTenant;
    @Expose
    private String tenantName;
    @Expose
    private Object tenantDescription;
    @Expose
    private String tenantCode;
    @Expose
    private Integer isOwner;

    /**
     * 
     * @return
     *     The idTenant
     */
    public Integer getIdTenant() {
        return idTenant;
    }

    /**
     * 
     * @param idTenant
     *     The idTenant
     */
    public void setIdTenant(Integer idTenant) {
        this.idTenant = idTenant;
    }

    /**
     * 
     * @return
     *     The tenantName
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * 
     * @param tenantName
     *     The tenantName
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /**
     * 
     * @return
     *     The tenantDescription
     */
    public Object getTenantDescription() {
        return tenantDescription;
    }

    /**
     * 
     * @param tenantDescription
     *     The tenantDescription
     */
    public void setTenantDescription(Object tenantDescription) {
        this.tenantDescription = tenantDescription;
    }

    /**
     * 
     * @return
     *     The tenantCode
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * 
     * @param tenantCode
     *     The tenantCode
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    /**
     * 
     * @return
     *     The isOwner
     */
    public Integer getIsOwner() {
        return isOwner;
    }

    /**
     * 
     * @param isOwner
     *     The isOwner
     */
    public void setIsOwner(Integer isOwner) {
        this.isOwner = isOwner;
    }

}
