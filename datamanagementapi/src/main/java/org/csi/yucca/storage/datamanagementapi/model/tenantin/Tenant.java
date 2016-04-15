
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
     *     The organizationCode
     */
    public String getOrganizationCode() {
		return organizationCode;
	}

    /**
     * 
     * @param organizationCode
     *     The organizationCode
     */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	@Expose
    private Integer maxDatasetNum;
    
    @Expose
    private Integer maxStreamsNum;
    
    @Expose
    private String organizationCode;
    
	/**
     * 
     * @return
     *     The maxDatasetNum
     */
    public Integer getMaxDatasetNum() {
		return maxDatasetNum;
	}

    /**
     * 
     * @param maxDatasetNum
     *     The maxDatasetNum
     */
	public void setMaxDatasetNum(Integer maxDatasetNum) {
		this.maxDatasetNum = maxDatasetNum;
	}

	/**
     * 
     * @return
     *     The maxStreamsNum
     */
	public Integer getMaxStreamsNum() {
		return maxStreamsNum;
	}

    /**
     * 
     * @param maxStreamsNum
     *     The maxStreamsNum
     */
	public void setMaxStreamsNum(Integer maxStreamsNum) {
		this.maxStreamsNum = maxStreamsNum;
	}

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
