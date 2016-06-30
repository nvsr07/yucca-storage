
package org.csi.yucca.storage.datamanagementapi.model.tenantout;

import com.google.gson.annotations.Expose;

public class TenantOut {

    @Expose
    private Long idTenant;
    @Expose
    private String tenantName;
    @Expose
    private Object tenantDescription;
    @Expose
    private String tenantCode;
    @Expose
    private String dataCollectionName;
    @Expose
    private String dataCollectionDb;
    @Expose
    private String measuresCollectionName;
    @Expose
    private String measuresCollectionDb;
    @Expose
    private String socialCollectionName;
    @Expose
    private String socialCollectionDb;
    @Expose
    private String mediaCollectionName;
    @Expose
    private String mediaCollectionDb;
    @Expose
    private String archiveDataCollectionName;
    @Expose
    private String archiveDataCollectionDb;
    @Expose
    private String archiveMeasuresCollectionName;
    @Expose
    private String archiveMeasuresCollectionDb;
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
    
    public String getOrganizationCode() {
		return organizationCode;
	}
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}
    
    
    public Integer getMaxDatasetNum() {
		return maxDatasetNum;
	}

	public void setMaxDatasetNum(Integer maxDatasetNum) {
		this.maxDatasetNum = maxDatasetNum;
	}

	public Integer getMaxStreamsNum() {
		return maxStreamsNum;
	}

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
     *     The dataCollectionName
     */
    public String getDataCollectionName() {
        return dataCollectionName;
    }

    /**
     * 
     * @param dataCollectionName
     *     The dataCollectionName
     */
    public void setDataCollectionName(String dataCollectionName) {
        this.dataCollectionName = dataCollectionName;
    }

    /**
     * 
     * @return
     *     The dataCollectionDb
     */
    public String getDataCollectionDb() {
        return dataCollectionDb;
    }

    /**
     * 
     * @param dataCollectionDb
     *     The dataCollectionDb
     */
    public void setDataCollectionDb(String dataCollectionDb) {
        this.dataCollectionDb = dataCollectionDb;
    }

    /**
     * 
     * @return
     *     The measuresCollectionName
     */
    public String getMeasuresCollectionName() {
        return measuresCollectionName;
    }

    /**
     * 
     * @param measuresCollectionName
     *     The measuresCollectionName
     */
    public void setMeasuresCollectionName(String measuresCollectionName) {
        this.measuresCollectionName = measuresCollectionName;
    }

    /**
     * 
     * @return
     *     The measuresCollectionDb
     */
    public String getMeasuresCollectionDb() {
        return measuresCollectionDb;
    }

    /**
     * 
     * @param measuresCollectionDb
     *     The measuresCollectionDb
     */
    public void setMeasuresCollectionDb(String measuresCollectionDb) {
        this.measuresCollectionDb = measuresCollectionDb;
    }

    /**
     * 
     * @return
     *     The socialCollectionName
     */
    public String getSocialCollectionName() {
        return socialCollectionName;
    }

    /**
     * 
     * @param socialCollectionName
     *     The socialCollectionName
     */
    public void setSocialCollectionName(String socialCollectionName) {
        this.socialCollectionName = socialCollectionName;
    }

    /**
     * 
     * @return
     *     The socialCollectionDb
     */
    public String getSocialCollectionDb() {
        return socialCollectionDb;
    }

    /**
     * 
     * @param socialCollectionDb
     *     The socialCollectionDb
     */
    public void setSocialCollectionDb(String socialCollectionDb) {
        this.socialCollectionDb = socialCollectionDb;
    }

    /**
     * 
     * @return
     *     The mediaCollectionName
     */
    public String getMediaCollectionName() {
        return mediaCollectionName;
    }

    /**
     * 
     * @param mediaCollectionName
     *     The mediaCollectionName
     */
    public void setMediaCollectionName(String mediaCollectionName) {
        this.mediaCollectionName = mediaCollectionName;
    }

    /**
     * 
     * @return
     *     The mediaCollectionDb
     */
    public String getMediaCollectionDb() {
        return mediaCollectionDb;
    }

    /**
     * 
     * @param mediaCollectionDb
     *     The mediaCollectionDb
     */
    public void setMediaCollectionDb(String mediaCollectionDb) {
        this.mediaCollectionDb = mediaCollectionDb;
    }

    /**
     * 
     * @return
     *     The archiveDataCollectionName
     */
    public String getArchiveDataCollectionName() {
        return archiveDataCollectionName;
    }

    /**
     * 
     * @param archiveDataCollectionName
     *     The archiveDataCollectionName
     */
    public void setArchiveDataCollectionName(String archiveDataCollectionName) {
        this.archiveDataCollectionName = archiveDataCollectionName;
    }

    /**
     * 
     * @return
     *     The archiveDataCollectionDb
     */
    public String getArchiveDataCollectionDb() {
        return archiveDataCollectionDb;
    }

    /**
     * 
     * @param archiveDataCollectionDb
     *     The archiveDataCollectionDb
     */
    public void setArchiveDataCollectionDb(String archiveDataCollectionDb) {
        this.archiveDataCollectionDb = archiveDataCollectionDb;
    }

    /**
     * 
     * @return
     *     The archiveMeasuresCollectionName
     */
    public String getArchiveMeasuresCollectionName() {
        return archiveMeasuresCollectionName;
    }

    /**
     * 
     * @param archiveMeasuresCollectionName
     *     The archiveMeasuresCollectionName
     */
    public void setArchiveMeasuresCollectionName(String archiveMeasuresCollectionName) {
        this.archiveMeasuresCollectionName = archiveMeasuresCollectionName;
    }

    /**
     * 
     * @return
     *     The archiveMeasuresCollectionDb
     */
    public String getArchiveMeasuresCollectionDb() {
        return archiveMeasuresCollectionDb;
    }

    /**
     * 
     * @param archiveMeasuresCollectionDb
     *     The archiveMeasuresCollectionDb
     */
    public void setArchiveMeasuresCollectionDb(String archiveMeasuresCollectionDb) {
        this.archiveMeasuresCollectionDb = archiveMeasuresCollectionDb;
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
