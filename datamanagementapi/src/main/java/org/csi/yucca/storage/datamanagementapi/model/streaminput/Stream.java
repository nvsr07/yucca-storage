
package org.csi.yucca.storage.datamanagementapi.model.streaminput;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;


public class Stream {

	

	@Expose
    private Integer twtMaxStreamsOfVE;
	@Expose
    private Integer twtRatePercentage;
	@Expose
    private Integer twtCount;
	@Expose
    private String twtResultType;
	@Expose
    private String twtUntil;
	@Expose
    private String twtLocale;
    @Expose
    private String twtLang;
	@Expose
    private String twtGeolocUnit;
	@Expose
    private String twtQuery;
	@Expose
	private Double twtGeolocLat;
	@Expose
	private Double twtGeolocLon;
	@Expose
	private Double twtGeolocRadius;
	@Expose
    private Long idTenant;
    @Expose
    private Integer idVirtualEntity;
    @Expose
    private Integer idStream;
    @Expose
    private Integer idCategoriaVe;
    @Expose
    private Integer idTipoVe;
    @Expose
    private String nomeTenant;
    @Expose
    private String codiceTenant;
    @Expose
    private String virtualEntityName;
    @Expose
    private String virtualEntityDescription;
    @Expose
    private String codiceStream;
    @Expose
    private String nomeStream;
    @Expose
    private String codiceVirtualEntity;
    @Expose
    private String lastUpdate;
    @Expose
    private String lastMessage;
    @Expose
    private String statoStream;
    @Expose
    private String domainStream;
    @Expose
    private String codSubDomain;
    @Expose
    private String licence;
    @Expose
    private String disclaimer;
    @Expose
    private String copyright;
    @Expose
    private String visibility;
    @Expose
    private Object tags;
    @Expose
    private String tipoVirtualEntity;
    @Expose
    private String categoriaVirtualEntity;
    @Expose
    private Integer saveData;
    @Expose
    private String esitoFabricController;
    @Expose
    private Integer deploymentVersion;
    @Expose
    private String deploymentStatusCode;
    @Expose
    private String deploymentStatusDesc;
    @Expose
    private Integer publishStream;
    @Expose
    private Double fps;
    @Expose
    private String registrationDate;
    @Expose
    private String nomeRichiedente;
    @Expose
    private String cognomeRichiedente;
    @Expose
    private String mailRichiedente;
    @Expose
    private Integer accettazionePrivacy;
    @Expose
    private String internalQuery;
    @Expose
    private String streamIcon;       
    @Expose
    private Componenti componenti;
    @Expose
    private StreamTags streamTags;
    @Expose
    private StreamInternalChildren streamInternalChildren;
    @Expose 
    private VirtualEntityPositions virtualEntityPositions;
    @Expose
    private Tenantssharing tenantssharing;
    @Expose
    private String externalReference;       
    @Expose
    private OpendataInfo opendata;   
    @Expose
    private int dcatReady;  
    @Expose
	private String dcatCreatorName;  
    @Expose
	private String dcatCreatorType;  
    @Expose
	private String dcatCreatorId;  
    @Expose
	private String dcatRightsHolderName;  
    @Expose
	private String dcatRightsHolderType;  
    @Expose
	private String dcatRightsHolderId;  
    @Expose
	private String dcatNomeOrg;  
    @Expose
	private String dcatEmailOrg;

    @Expose
	private Map<String, List<String>> tagsTranslated;
    @Expose
	private Map<String, String> domainTranslated;
    @Expose
	private Map<String, String> subDomainTranslated;
    
    @Expose
	private String organizationCode;
    
    @Expose
	private String organizationDescription;
	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getOrganizationDescription() {
		return organizationDescription;
	}

	public void setOrganizationDescription(String organizationDescription) {
		this.organizationDescription = organizationDescription;
	}

    public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public OpendataInfo getOpendata() {
		return opendata;
	}

	public void setOpendata(OpendataInfo opendata) {
		this.opendata = opendata;
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
     *     The idVirtualEntity
     */
    public Integer getIdVirtualEntity() {
        return idVirtualEntity;
    }

    /**
     * 
     * @param idVirtualEntity
     *     The idVirtualEntity
     */
    public void setIdVirtualEntity(Integer idVirtualEntity) {
        this.idVirtualEntity = idVirtualEntity;
    }

    /**
     * 
     * @return
     *     The idStream
     */
    public Integer getIdStream() {
        return idStream;
    }

    /**
     * 
     * @param idStream
     *     The idStream
     */
    public void setIdStream(Integer idStream) {
        this.idStream = idStream;
    }

    /**
     * 
     * @return
     *     The idCategoriaVe
     */
    public Integer getIdCategoriaVe() {
        return idCategoriaVe;
    }

    /**
     * 
     * @param idCategoriaVe
     *     The idCategoriaVe
     */
    public void setIdCategoriaVe(Integer idCategoriaVe) {
        this.idCategoriaVe = idCategoriaVe;
    }

    /**
     * 
     * @return
     *     The idTipoVe
     */
    public Integer getIdTipoVe() {
        return idTipoVe;
    }

    /**
     * 
     * @param idTipoVe
     *     The idTipoVe
     */
    public void setIdTipoVe(Integer idTipoVe) {
        this.idTipoVe = idTipoVe;
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

    /**
     * 
     * @return
     *     The virtualEntityName
     */
    public String getVirtualEntityName() {
        return virtualEntityName;
    }

    /**
     * 
     * @param virtualEntityName
     *     The virtualEntityName
     */
    public void setVirtualEntityName(String virtualEntityName) {
        this.virtualEntityName = virtualEntityName;
    }

    /**
     * 
     * @return
     *     The virtualEntityDescription
     */
    public String getVirtualEntityDescription() {
        return virtualEntityDescription;
    }

    /**
     * 
     * @param virtualEntityDescription
     *     The virtualEntityDescription
     */
    public void setVirtualEntityDescription(String virtualEntityDescription) {
        this.virtualEntityDescription = virtualEntityDescription;
    }

    /**
     * 
     * @return
     *     The codiceStream
     */
    public String getCodiceStream() {
        return codiceStream;
    }

    /**
     * 
     * @param codiceStream
     *     The codiceStream
     */
    public void setCodiceStream(String codiceStream) {
        this.codiceStream = codiceStream;
    }

    /**
     * 
     * @return
     *     The nomeStream
     */
    public String getNomeStream() {
        return nomeStream;
    }

    /**
     * 
     * @param nomeStream
     *     The nomeStream
     */
    public void setNomeStream(String nomeStream) {
        this.nomeStream = nomeStream;
    }

    /**
     * 
     * @return
     *     The codiceVirtualEntity
     */
    public String getCodiceVirtualEntity() {
        return codiceVirtualEntity;
    }

    /**
     * 
     * @param codiceVirtualEntity
     *     The codiceVirtualEntity
     */
    public void setCodiceVirtualEntity(String codiceVirtualEntity) {
        this.codiceVirtualEntity = codiceVirtualEntity;
    }

    /**
     * 
     * @return
     *     The lastUpdate
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * 
     * @param lastUpdate
     *     The lastUpdate
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * 
     * @return
     *     The lastMessage
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * 
     * @param lastMessage
     *     The lastMessage
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * 
     * @return
     *     The statoStream
     */
    public String getStatoStream() {
        return statoStream;
    }

    /**
     * 
     * @param statoStream
     *     The statoStream
     */
    public void setStatoStream(String statoStream) {
        this.statoStream = statoStream;
    }

    /**
     * 
     * @return
     *     The domainStream
     */
    public String getDomainStream() {
        return domainStream;
    }

    /**
     * 
     * @param domainStream
     *     The domainStream
     */
    public void setDomainStream(String domainStream) {
        this.domainStream = domainStream;
    }
    

    /**
     * 
     * @return
     *     The codSubDomain
     */
    public String getCodSubDomain() {
        return codSubDomain;
    }

    /**
     * 
     * @param codSubDomain
     *     The codSubDomain
     */
    public void setCodSubDomain(String codSubDomain) {
        this.codSubDomain = codSubDomain;
    }
    
    /**
     * 
     * @return
     *     The licence
     */
    public String getLicence() {
        return licence;
    }

    /**
     * 
     * @param licence
     *     The licence
     */
    public void setLicence(String licence) {
        this.licence = licence;
    }

    /**
     * 
     * @return
     *     The disclaimer
     */
    public String getDisclaimer() {
        return disclaimer;
    }

    /**
     * 
     * @param disclaimer
     *     The disclaimer
     */
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    /**
     * 
     * @return
     *     The copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * 
     * @param copyright
     *     The copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * 
     * @return
     *     The visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * 
     * @param visibility
     *     The visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * 
     * @return
     *     The tags
     */
    public Object getTags() {
        return tags;
    }

    /**
     * 
     * @param tags
     *     The tags
     */
    public void setTags(Object tags) {
        this.tags = tags;
    }

    /**
     * 
     * @return
     *     The tipoVirtualEntity
     */
    public String getTipoVirtualEntity() {
        return tipoVirtualEntity;
    }

    /**
     * 
     * @param tipoVirtualEntity
     *     The tipoVirtualEntity
     */
    public void setTipoVirtualEntity(String tipoVirtualEntity) {
        this.tipoVirtualEntity = tipoVirtualEntity;
    }

    /**
     * 
     * @return
     *     The categoriaVirtualEntity
     */
    public String getCategoriaVirtualEntity() {
        return categoriaVirtualEntity;
    }

    /**
     * 
     * @param categoriaVirtualEntity
     *     The categoriaVirtualEntity
     */
    public void setCategoriaVirtualEntity(String categoriaVirtualEntity) {
        this.categoriaVirtualEntity = categoriaVirtualEntity;
    }

    /**
     * 
     * @return
     *     The saveData
     */
    public Integer getSaveData() {
        return saveData;
    }

    /**
     * 
     * @param saveData
     *     The saveData
     */
    public void setSaveData(Integer saveData) {
        this.saveData = saveData;
    }

    /**
     * 
     * @return
     *     The esitoFabricController
     */
    public String getEsitoFabricController() {
        return esitoFabricController;
    }

    /**
     * 
     * @param esitoFabricController
     *     The esitoFabricController
     */
    public void setEsitoFabricController(String esitoFabricController) {
        this.esitoFabricController = esitoFabricController;
    }

    /**
     * 
     * @return
     *     The deploymentVersion
     */
    public Integer getDeploymentVersion() {
        return deploymentVersion;
    }

    /**
     * 
     * @param deploymentVersion
     *     The deploymentVersion
     */
    public void setDeploymentVersion(Integer deploymentVersion) {
        this.deploymentVersion = deploymentVersion;
    }

    /**
     * 
     * @return
     *     The deploymentStatusCode
     */
    public String getDeploymentStatusCode() {
        return deploymentStatusCode;
    }

    /**
     * 
     * @param deploymentStatusCode
     *     The deploymentStatusCode
     */
    public void setDeploymentStatusCode(String deploymentStatusCode) {
        this.deploymentStatusCode = deploymentStatusCode;
    }

    /**
     * 
     * @return
     *     The deploymentStatusDesc
     */
    public String getDeploymentStatusDesc() {
        return deploymentStatusDesc;
    }

    /**
     * 
     * @param deploymentStatusDesc
     *     The deploymentStatusDesc
     */
    public void setDeploymentStatusDesc(String deploymentStatusDesc) {
        this.deploymentStatusDesc = deploymentStatusDesc;
    }

    /**
     * 
     * @return
     *     The publishStream
     */
    public Integer getPublishStream() {
        return publishStream;
    }

    /**
     * 
     * @param publishStream
     *     The publishStream
     */
    public void setPublishStream(Integer publishStream) {
        this.publishStream = publishStream;
    }

    /**
     * 
     * @return
     *     The fps
     */
    public Double getFps() {
        return fps;
    }

    /**
     * 
     * @param fps
     *     The fps
     */
    public void setFps(Double fps) {
        this.fps = fps;
    }

    /**
     * 
     * @return
     *     The registrationDate
     */
    public String getRegistrationDate() {
        return registrationDate;
    }

    /**
     * 
     * @param registrationDate
     *     The registrationDate
     */
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * 
     * @return
     *     The nomeRichiedente
     */
    public String getNomeRichiedente() {
        return nomeRichiedente;
    }

    /**
     * 
     * @param nomeRichiedente
     *     The nomeRichiedente
     */
    public void setNomeRichiedente(String nomeRichiedente) {
        this.nomeRichiedente = nomeRichiedente;
    }

    /**
     * 
     * @return
     *     The cognomeRichiedente
     */
    public String getCognomeRichiedente() {
        return cognomeRichiedente;
    }

    /**
     * 
     * @param cognomeRichiedente
     *     The cognomeRichiedente
     */
    public void setCognomeRichiedente(String cognomeRichiedente) {
        this.cognomeRichiedente = cognomeRichiedente;
    }

    /**
     * 
     * @return
     *     The mailRichiedente
     */
    public String getMailRichiedente() {
        return mailRichiedente;
    }

    /**
     * 
     * @param mailRichiedente
     *     The mailRichiedente
     */
    public void setMailRichiedente(String mailRichiedente) {
        this.mailRichiedente = mailRichiedente;
    }

    /**
     * 
     * @return
     *     The accettazionePrivacy
     */
    public Integer getAccettazionePrivacy() {
        return accettazionePrivacy;
    }

    /**
     * 
     * @param accettazionePrivacy
     *     The accettazionePrivacy
     */
    public void setAccettazionePrivacy(Integer accettazionePrivacy) {
        this.accettazionePrivacy = accettazionePrivacy;
    }

    /**
     * 
     * @return
     *     The internalQuery
     */
    public String getInternalQuery() {
        return internalQuery;
    }

    /**
     * 
     * @param internalQuery
     *     The internalQuery
     */
    public void setInternalQuery(String internalQuery) {
        this.internalQuery = internalQuery;
    }

    /**
     * 
     * @return
     *     The componenti
     */
    public Componenti getComponenti() {
        return componenti;
    }

    /**
     * 
     * @param componenti
     *     The componenti
     */
    public void setComponenti(Componenti componenti) {
        this.componenti = componenti;
    }

    /**
     * 
     * @return
     *     The streamTags
     */
    public StreamTags getStreamTags() {
        return streamTags;
    }

    /**
     * 
     * @param streamTags
     *     The streamTags
     */
    public void setStreamTags(StreamTags streamTags) {
        this.streamTags = streamTags;
    }

    /**
     * 
     * @return
     *     The streamInternalChildren
     */
    public StreamInternalChildren getStreamInternalChildren() {
        return streamInternalChildren;
    }

    /**
     * 
     * @param streamInternalChildren
     *     The streamInternalChildren
     */
    public void setStreamInternalChildren(StreamInternalChildren streamInternalChildren) {
        this.streamInternalChildren = streamInternalChildren;
    }

	public VirtualEntityPositions getVirtualEntityPositions() {
		return virtualEntityPositions;
	}

	public void setVirtualEntityPositions(
			VirtualEntityPositions virtualEntityPositions) {
		this.virtualEntityPositions = virtualEntityPositions;
	}

	public String getStreamIcon() {
		return streamIcon;
	}

	public void setStreamIcon(String streamIcon) {
		this.streamIcon = streamIcon;
	}

	public Tenantssharing getTenantssharing() {
		return tenantssharing;
	}

	public void setTenantssharing(Tenantssharing tenantssharing) {
		this.tenantssharing = tenantssharing;
	}

	public Map<String, List<String>> getTagsTranslated() {
		return tagsTranslated;
	}

	public void setTagsTranslated(Map<String, List<String>> tagsTranslated) {
		this.tagsTranslated = tagsTranslated;
	}

	public Integer getTwtRatePercentage() {
		return twtRatePercentage;
	}

	public void setTwtRatePercentage(Integer twtRatePercentage) {
		this.twtRatePercentage = twtRatePercentage;
	}

	public Integer getTwtCount() {
		return twtCount;
	}

	public void setTwtCount(Integer twtCount) {
		this.twtCount = twtCount;
	}

	public String getTwtResultType() {
		return twtResultType;
	}

	public void setTwtResultType(String twtResultType) {
		this.twtResultType = twtResultType;
	}

	public String getTwtUntil() {
		return twtUntil;
	}

	public void setTwtUntil(String twtUntil) {
		this.twtUntil = twtUntil;
	}

	public String getTwtLocale() {
		return twtLocale;
	}

	public void setTwtLocale(String twtLocale) {
		this.twtLocale = twtLocale;
	}

	public String getTwtLang() {
		return twtLang;
	}

	public void setTwtLang(String twtLang) {
		this.twtLang = twtLang;
	}

	public String getTwtGeolocUnit() {
		return twtGeolocUnit;
	}

	public void setTwtGeolocUnit(String twtGeolocUnit) {
		this.twtGeolocUnit = twtGeolocUnit;
	}

	public String getTwtQuery() {
		return twtQuery;
	}

	public void setTwtQuery(String twtQuery) {
		this.twtQuery = twtQuery;
	}

	public Double getTwtGeolocLat() {
		return twtGeolocLat;
	}

	public void setTwtGeolocLat(Double twtGeolocLat) {
		this.twtGeolocLat = twtGeolocLat;
	}

	public Double getTwtGeolocLon() {
		return twtGeolocLon;
	}

	public void setTwtGeolocLon(Double twtGeolocLon) {
		this.twtGeolocLon = twtGeolocLon;
	}

	public Double getTwtGeolocRadius() {
		return twtGeolocRadius;
	}

	public void setTwtGeolocRadius(Double twtGeolocRadius) {
		this.twtGeolocRadius = twtGeolocRadius;
	}

	public Integer getTwtMaxStreamsOfVE() {
		return twtMaxStreamsOfVE;
	}

	public void setTwtMaxStreamsOfVE(Integer twtMaxStreamsOfVE) {
		this.twtMaxStreamsOfVE = twtMaxStreamsOfVE;
	}
	
	public Map<String, String> getDomainTranslated() {
		return domainTranslated;
	}

	public void setDomainTranslated(Map<String, String> domainTranslated) {
		this.domainTranslated = domainTranslated;
	}
	
	
	public Map<String, String> getSubDomainTranslated() {
		return subDomainTranslated;
	}

	public void setSubDomainTranslated(Map<String, String> subDomainTranslated) {
		this.subDomainTranslated = subDomainTranslated;
	}


	@Expose
    private String virtualEntitySlug;
	
	public String getVirtualEntitySlug() {
		return virtualEntitySlug;
	}

	public void setVirtualEntitySlug(String virtualEntitySlug) {
		this.virtualEntitySlug = virtualEntitySlug;
	}

	public int getDcatReady() {
		return dcatReady;
	}

	public void setDcatReady(int dcatReady) {
		this.dcatReady = dcatReady;
	}

	public void setDcatReady(Boolean dcatReady) {
		this.dcatReady = (dcatReady) ? 1 : 0;
	}

	public String getDcatCreatorName() {
		return dcatCreatorName;
	}

	public void setDcatCreatorName(String dcatCreatorName) {
		this.dcatCreatorName = dcatCreatorName;
	}

	public String getDcatCreatorType() {
		return dcatCreatorType;
	}

	public void setDcatCreatorType(String dcatCreatorType) {
		this.dcatCreatorType = dcatCreatorType;
	}

	public String getDcatCreatorId() {
		return dcatCreatorId;
	}

	public void setDcatCreatorId(String dcatCreatorId) {
		this.dcatCreatorId = dcatCreatorId;
	}

	public String getDcatRightsHolderName() {
		return dcatRightsHolderName;
	}

	public void setDcatRightsHolderName(String dcatRightsHolderName) {
		this.dcatRightsHolderName = dcatRightsHolderName;
	}

	public String getDcatRightsHolderType() {
		return dcatRightsHolderType;
	}

	public void setDcatRightsHolderType(String dcatRightsHolderType) {
		this.dcatRightsHolderType = dcatRightsHolderType;
	}

	public String getDcatRightsHolderId() {
		return dcatRightsHolderId;
	}

	public void setDcatRightsHolderId(String dcatRightsHolderId) {
		this.dcatRightsHolderId = dcatRightsHolderId;
	}

	public String getDcatNomeOrg() {
		return dcatNomeOrg;
	}

	public void setDcatNomeOrg(String dcatNomeOrg) {
		this.dcatNomeOrg = dcatNomeOrg;
	}

	public String getDcatEmailOrg() {
		return dcatEmailOrg;
	}

	public void setDcatEmailOrg(String dcatEmailOrg) {
		this.dcatEmailOrg = dcatEmailOrg;
	}
	
}
