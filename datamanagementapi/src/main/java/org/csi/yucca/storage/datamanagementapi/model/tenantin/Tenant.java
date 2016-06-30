
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
	@Expose
    private Integer maxDatasetNum;
    @Expose
    private Integer maxStreamsNum;
    @Expose
    private String organizationCode;
    @Expose
    private String tenantPassword;
    @Expose
    private String tenantType;
    @Expose
    private String codDeploymentStatus;
    @Expose
    private String dataAttivazione;
    @Expose
    private String dataDisattivazione;
    @Expose
    private Integer numGiorniAttivo;
    @Expose
    private Integer idEcosystem;
    @Expose
    private String userName;
    @Expose
    private String userLastName;
    @Expose
    private String userEmail;
    @Expose
    private String userTypeAuth;
    @Expose
    private String userFirstName;

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

	public String getTenantPassword() {
		return tenantPassword;
	}

	public void setTenantPassword(String tenantPassword) {
		this.tenantPassword = tenantPassword;
	}

	public String getTenantType() {
		return tenantType;
	}

	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}

	public String getCodDeploymentStatus() {
		return codDeploymentStatus;
	}

	public void setCodDeploymentStatus(String codDeploymentStatus) {
		this.codDeploymentStatus = codDeploymentStatus;
	}

	public String getDataAttivazione() {
		return dataAttivazione;
	}

	public void setDataAttivazione(String dataAttivazione) {
		this.dataAttivazione = dataAttivazione;
	}

	public String getDataDisattivazione() {
		return dataDisattivazione;
	}

	public void setDataDisattivazione(String dataDisattivazione) {
		this.dataDisattivazione = dataDisattivazione;
	}

	public Integer getNumGiorniAttivo() {
		return numGiorniAttivo;
	}

	public void setNumGiorniAttivo(Integer numGiorniAttivo) {
		this.numGiorniAttivo = numGiorniAttivo;
	}

	public Integer getIdEcosystem() {
		return idEcosystem;
	}

	public void setIdEcosystem(Integer idEcosystem) {
		this.idEcosystem = idEcosystem;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserTypeAuth() {
		return userTypeAuth;
	}

	public void setUserTypeAuth(String userTypeAuth) {
		this.userTypeAuth = userTypeAuth;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

}
