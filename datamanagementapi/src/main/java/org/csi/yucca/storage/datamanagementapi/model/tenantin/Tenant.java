
package org.csi.yucca.storage.datamanagementapi.model.tenantin;

import com.google.gson.annotations.Expose;

public class Tenant {

    @Expose
    private Long idTenant;
    @Expose
    private String nomeTenant;
    @Expose
    private String tenantDescription;
    @Expose
    private String codiceTenant;

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
     *     The nomeTenant
     */
    public String getNomeTenant() {
        return nomeTenant;
    }

    /**
     * 
     * @param nomeTenant
     *     The nomeTenant
     */
    public void setNomeTenant(String nomeTenant) {
        this.nomeTenant = nomeTenant;
    }

    /**
     * 
     * @return
     *     The tenantDescription
     */
    public String getTenantDescription() {
        return tenantDescription;
    }

    /**
     * 
     * @param tenantDescription
     *     The tenantDescription
     */
    public void setTenantDescription(String tenantDescription) {
        this.tenantDescription = tenantDescription;
    }

    /**
     * 
     * @return
     *     The codiceTenant
     */
    public String getCodiceTenant() {
        return codiceTenant;
    }

    /**
     * 
     * @param codiceTenant
     *     The codiceTenant
     */
    public void setCodiceTenant(String codiceTenant) {
        this.codiceTenant = codiceTenant;
    }

}
