
package org.csi.yucca.storage.datamanagementapi.model.streamOutput;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
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
    private Integer idVirtualEntity;
    @Expose
    private Integer idCategoriaVe;
    @Expose
    private Integer idTipoVe;
    @Expose
    private String virtualEntityName;
    @Expose
    private String virtualEntityDescription;
    @Expose
    private String virtualEntityCode;
    @Expose
    private String virtualEntityType;
    @Expose
    private String virtualEntityCategory;
    @Expose
    private String lastUpdate;
    @Expose
    private String lastMessage;
    @Expose
    private String streamStatus;
    @Expose
    private String domainStream;
    @Expose
    private String codSubDomain;
    @Expose
    private String licence;
    @Expose
    private Object disclaimer;
    @Expose
    private String copyright;
    @Expose
    private String visibility;
    @Expose
    private Integer saveData;
    @Expose
    private Object fabricControllerOutcome;
    @Expose
    private Integer deploymentVersion;
    @Expose
    private String deploymentStatusCode;
    @Expose
    private String deploymentStatusDesc;
    @Expose
    private Boolean publishStream;
    @Expose
    private Double fps;
    @Expose
    private Integer privacyAcceptance;
    @Expose
    private String registrationDate;
    @Expose
    private String requesterName;
    @Expose
    private String requesterSurname;
    @Expose
    private String requesterMail;
    @Expose
    private String streamIcon;
    @Expose
    private String internalQuery;
    @Expose
    private StreamInternalChildren streamInternalChildren;
    @Expose
    private Components components;
    @Expose
    private StreamTags streamTags;
    @Expose
    private VirtualEntityPositions virtualEntityPositions;
    
	public static Stream fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, Stream.class);
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

	@Expose
    private Tenantssharing tenantssharing;


    /* YUCCA-505 */
    @Expose
    private String externalReference;       
    
    @Expose
    private OpendataInfo opendata;
    
    
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
     *     The virtualEntityCode
     */
    public String getVirtualEntityCode() {
        return virtualEntityCode;
    }

    /**
     * 
     * @param virtualEntityCode
     *     The virtualEntityCode
     */
    public void setVirtualEntityCode(String virtualEntityCode) {
        this.virtualEntityCode = virtualEntityCode;
    }

    /**
     * 
     * @return
     *     The virtualEntityType
     */
    public String getVirtualEntityType() {
        return virtualEntityType;
    }

    /**
     * 
     * @param virtualEntityType
     *     The virtualEntityType
     */
    public void setVirtualEntityType(String virtualEntityType) {
        this.virtualEntityType = virtualEntityType;
    }

    /**
     * 
     * @return
     *     The virtualEntityCategory
     */
    public String getVirtualEntityCategory() {
        return virtualEntityCategory;
    }

    /**
     * 
     * @param virtualEntityCategory
     *     The virtualEntityCategory
     */
    public void setVirtualEntityCategory(String virtualEntityCategory) {
        this.virtualEntityCategory = virtualEntityCategory;
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
     *     The streamStatus
     */
    public String getStreamStatus() {
        return streamStatus;
    }

    /**
     * 
     * @param streamStatus
     *     The streamStatus
     */
    public void setStreamStatus(String streamStatus) {
        this.streamStatus = streamStatus;
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
    public Object getDisclaimer() {
        return disclaimer;
    }

    /**
     * 
     * @param disclaimer
     *     The disclaimer
     */
    public void setDisclaimer(Object disclaimer) {
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
     *     The fabricControllerOutcome
     */
    public Object getFabricControllerOutcome() {
        return fabricControllerOutcome;
    }

    /**
     * 
     * @param fabricControllerOutcome
     *     The fabricControllerOutcome
     */
    public void setFabricControllerOutcome(Object fabricControllerOutcome) {
        this.fabricControllerOutcome = fabricControllerOutcome;
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
    public Boolean getPublishStream() {
        return publishStream;
    }

    /**
     * 
     * @param publishStream
     *     The publishStream
     */
    public void setPublishStream(Boolean publishStream) {
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
     *     The privacyAcceptance
     */
    public Integer getPrivacyAcceptance() {
        return privacyAcceptance;
    }

    /**
     * 
     * @param privacyAcceptance
     *     The privacyAcceptance
     */
    public void setPrivacyAcceptance(Integer privacyAcceptance) {
        this.privacyAcceptance = privacyAcceptance;
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
     *     The requesterName
     */
    public String getRequesterName() {
        return requesterName;
    }

    /**
     * 
     * @param requesterName
     *     The requesterName
     */
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    /**
     * 
     * @return
     *     The requesterSurname
     */
    public String getRequesterSurname() {
        return requesterSurname;
    }

    /**
     * 
     * @param requesterSurname
     *     The requesterSurname
     */
    public void setRequesterSurname(String requesterSurname) {
        this.requesterSurname = requesterSurname;
    }

    /**
     * 
     * @return
     *     The requesterMail
     */
    public String getRequesterMail() {
        return requesterMail;
    }

    /**
     * 
     * @param requesterMail
     *     The requesterMail
     */
    public void setRequesterMail(String requesterMail) {
        this.requesterMail = requesterMail;
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

    /**
     * 
     * @return
     *     The components
     */
    public Components getComponents() {
        return components;
    }

    /**
     * 
     * @param components
     *     The components
     */
    public void setComponents(Components components) {
        this.components = components;
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

	public VirtualEntityPositions getVirtualEntityPositions() {
		return virtualEntityPositions;
	}

	public void setVirtualEntityPositions(VirtualEntityPositions virtualEntityPositions) {
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
	@Expose
    private String virtualEntitySlug;
	
	public String getVirtualEntitySlug() {
		return virtualEntitySlug;
	}

	public void setVirtualEntitySlug(String virtualEntitySlug) {
		this.virtualEntitySlug = virtualEntitySlug;
	}

}
