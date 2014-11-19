
package org.csi.yucca.storage.datamanagementapi.model.api;

import com.google.gson.annotations.Expose;

public class ConfigData {

    @Expose
    private Long idTenant;
    @Expose
    private String tenantCode;
    @Expose
    private String type;
    @Expose
    private String subtype;
    @Expose
    private String entityNameSpace;

    /**
     * 
     * @return
     *     The idTenant
     */
    public Long getIdTenant() {
        return idTenant;
    }

    /**
     * 
     * @param idTenant
     *     The idTenant
     */
    public void setIdTenant(Long idTenant) {
        this.idTenant = idTenant;
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
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The subtype
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * 
     * @param subtype
     *     The subtype
     */
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    /**
     * 
     * @return
     *     The entityNameSpace
     */
    public String getEntityNameSpace() {
        return entityNameSpace;
    }

    /**
     * 
     * @param entityNameSpace
     *     The entityNameSpace
     */
    public void setEntityNameSpace(String entityNameSpace) {
        this.entityNameSpace = entityNameSpace;
    }

}
