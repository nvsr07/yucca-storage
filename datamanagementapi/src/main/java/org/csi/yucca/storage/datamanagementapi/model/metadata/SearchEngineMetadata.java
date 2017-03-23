package org.csi.yucca.storage.datamanagementapi.model.metadata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.solr.common.SolrInputDocument;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Element;
import org.csi.yucca.storage.datamanagementapi.model.streaminput.Stream;
import org.csi.yucca.storage.datamanagementapi.util.MetadataFiller;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class SearchEngineMetadata {
	private String id;
	private List<String> entityType;
	private String name;
	private String visibility;
	private String copyright;
	private String organizationCode;
	private String organizationDescription;
	private String domainCode;
	private String domainLangIT;
	private String domainLangEN;
	private String subdomainCode;
	private String subdomainLangIT;
	private String subdomainLangEN;
	private String licenseCode;
	private String licenceDescription;
	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getImportFileType() {
		return importFileType;
	}

	public void setImportFileType(String importFileType) {
		this.importFileType = importFileType;
	}

	private String tenantCode;
	private List<String> tenantsCode;
	private String tenantName;
	private String tenantDescription;
	private List<String> tagCode;
	private List<String> tagLangIT;
	private List<String> tagLangEN;
	private String dcatDataUpdate;
	private String dcatNomeOrg;
	private String dcatEmailOrg;
	private String dcatCreatorName;
	private String dcatCreatorType;
	private String dcatCreatorId;
	private String dcatRightsHolderName;
	private String dcatRightsHolderType;
	private String dcatRightsHolderId;
	private boolean dcatReady;
	private String datasetCode;
	private String datasetDescription;
	private String version;
	private String dataseType;
	private String datasetSubtype;
	private String streamCode;
	private String twtQuery;
	private String twtGeolocLat;
	private String twtGeolocLon;
	private String twtGeolocRadius;
	private String twtGeolocUnit;
	private String twtLang;
	private String twtLocale;
	private String twtCount;
	private String twtResultType;
	private String twtUntil;
	private String twtRatePercentage;
	private String twtLastSearchId;
	private String soCode;
	private String soName;
	private String soDescription;
	private String jsonFields;
	private String jsonSo;
	private String lat;
	private String lon;
	private List<String> sdpComponentsName;
	private ArrayList<String> phenomenon;

	
	
	
    private boolean isOpendata;
    private String opendataAuthor;
    private String opendataMetaUpdateDate;
    private String opendataLanguage;
    private String opendataUpdateDate;
	
	
	
	
	private String registrationDate;
	private String importFileType;
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean isOpendata() {
		return isOpendata;
	}

	public void setOpendata(boolean isOpendata) {
		this.isOpendata = isOpendata;
	}

	public String getOpendataAuthor() {
		return opendataAuthor;
	}

	public void setOpendataAuthor(String opendataAuthor) {
		this.opendataAuthor = opendataAuthor;
	}

	public String getOpendataMetaUpdateDate() {
		return opendataMetaUpdateDate;
	}

	public void setOpendataMetaUpdateDate(String opendataMetaUpdateDate) {
		this.opendataMetaUpdateDate = opendataMetaUpdateDate;
	}

	public String getOpendataLanguage() {
		return opendataLanguage;
	}

	public void setOpendataLanguage(String opendataLanguage) {
		this.opendataLanguage = opendataLanguage;
	}

	public String getOpendataUpdateDate() {
		return opendataUpdateDate;
	}

	public void setOpendataUpdateDate(String opendataUpdateDate) {
		this.opendataUpdateDate = opendataUpdateDate;
	}

	public SearchEngineMetadata() {
		super();
	}

	public static SearchEngineMetadata fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, SearchEngineMetadata.class);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getEntityType() {
		return entityType;
	}

	public void setEntityType(List<String> entityType) {
		this.entityType = entityType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

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

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getDomainLangIT() {
		return domainLangIT;
	}

	public void setDomainLangIT(String domainLangIT) {
		this.domainLangIT = domainLangIT;
	}

	public String getDomainLangEN() {
		return domainLangEN;
	}

	public void setDomainLangEN(String domainLangEN) {
		this.domainLangEN = domainLangEN;
	}

	public String getSubdomainCode() {
		return subdomainCode;
	}

	public void setSubdomainCode(String subdomainCode) {
		this.subdomainCode = subdomainCode;
	}

	public String getSubdomainLangIT() {
		return subdomainLangIT;
	}

	public void setSubdomainLangIT(String subdomainLangIT) {
		this.subdomainLangIT = subdomainLangIT;
	}

	public String getSubdomainLangEN() {
		return subdomainLangEN;
	}

	public void setSubdomainLangEN(String subdomainLangEN) {
		this.subdomainLangEN = subdomainLangEN;
	}

	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}

	public String getLicenceDescription() {
		return licenceDescription;
	}

	public void setLicenceDescription(String licenceDescription) {
		this.licenceDescription = licenceDescription;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public List<String> getTenantsCode() {
		return tenantsCode;
	}

	public void setTenantsCode(List<String> tenantsCode) {
		this.tenantsCode = tenantsCode;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantDescription() {
		return tenantDescription;
	}

	public void setTenantDescription(String tenantDescription) {
		this.tenantDescription = tenantDescription;
	}

	public List<String> getTagCode() {
		return tagCode;
	}

	public void setTagCode(List<String> tagCode) {
		this.tagCode = tagCode;
	}

	public List<String> getTagLangIT() {
		return tagLangIT;
	}

	public void setTagLangIT(List<String> tagLangIT) {
		this.tagLangIT = tagLangIT;
	}

	public List<String> getTagLangEN() {
		return tagLangEN;
	}

	public void setTagLangEN(List<String> tagLangEN) {
		this.tagLangEN = tagLangEN;
	}

	public String getDcatDataUpdate() {
		return dcatDataUpdate;
	}

	public void setDcatDataUpdate(String dcatDataUpdate) {
		this.dcatDataUpdate = dcatDataUpdate;
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


	public boolean isDcatReady() {
		return dcatReady;
	}

	public void setDcatReady(boolean dcatReady) {
		this.dcatReady = dcatReady;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public String getDatasetDescription() {
		return datasetDescription;
	}

	public void setDatasetDescription(String datasetDescription) {
		this.datasetDescription = datasetDescription;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDataseType() {
		return dataseType;
	}

	public void setDataseType(String dataseType) {
		this.dataseType = dataseType;
	}

	public String getDatasetSubtype() {
		return datasetSubtype;
	}

	public void setDatasetSubtype(String datasetSubtype) {
		this.datasetSubtype = datasetSubtype;
	}


	public String getStreamCode() {
		return streamCode;
	}

	public void setStreamCode(String streamCode) {
		this.streamCode = streamCode;
	}

	public String getTwtQuery() {
		return twtQuery;
	}

	public void setTwtQuery(String twtQuery) {
		this.twtQuery = twtQuery;
	}

	public String getTwtGeolocLat() {
		return twtGeolocLat;
	}

	public void setTwtGeolocLat(String twtGeolocLat) {
		this.twtGeolocLat = twtGeolocLat;
	}

	public String getTwtGeolocLon() {
		return twtGeolocLon;
	}

	public void setTwtGeolocLon(String twtGeolocLon) {
		this.twtGeolocLon = twtGeolocLon;
	}

	public String getTwtGeolocRadius() {
		return twtGeolocRadius;
	}

	public void setTwtGeolocRadius(String twtGeolocRadius) {
		this.twtGeolocRadius = twtGeolocRadius;
	}

	public String getTwtGeolocUnit() {
		return twtGeolocUnit;
	}

	public void setTwtGeolocUnit(String twtGeolocUnit) {
		this.twtGeolocUnit = twtGeolocUnit;
	}

	public String getTwtLang() {
		return twtLang;
	}

	public void setTwtLang(String twtLang) {
		this.twtLang = twtLang;
	}

	public String getTwtLocale() {
		return twtLocale;
	}

	public void setTwtLocale(String twtLocale) {
		this.twtLocale = twtLocale;
	}

	public String getTwtCount() {
		return twtCount;
	}

	public void setTwtCount(String twtCount) {
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

	public String getTwtRatePercentage() {
		return twtRatePercentage;
	}

	public void setTwtRatePercentage(String twtRatePercentage) {
		this.twtRatePercentage = twtRatePercentage;
	}

	public String getTwtLastSearchId() {
		return twtLastSearchId;
	}

	public void setTwtLastSearchId(String twtLastSearchId) {
		this.twtLastSearchId = twtLastSearchId;
	}

	public String getSoCode() {
		return soCode;
	}

	public void setSoCode(String soCode) {
		this.soCode = soCode;
	}

	public String getSoName() {
		return soName;
	}

	public void setSoName(String soName) {
		this.soName = soName;
	}

	public String getSoDescription() {
		return soDescription;
	}

	public void setSoDescription(String soDescription) {
		this.soDescription = soDescription;
	}

	public String getJsonFields() {
		return jsonFields;
	}

	public void setJsonFields(String jsonFields) {
		this.jsonFields = jsonFields;
	}

	public String getJsonSo() {
		return jsonSo;
	}

	public void setJsonSo(String jsonSo) {
		this.jsonSo = jsonSo;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public List<String> getSdpComponentsName() {
		return sdpComponentsName;
	}

	public void setSdpComponentsName(List<String> sdpComponentsName) {
		this.sdpComponentsName = sdpComponentsName;
	}

	public ArrayList<String> getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(ArrayList<String> phenomenon) {
		this.phenomenon = phenomenon;
	}
	
	

	public SolrInputDocument getSolrDocument() {
		SolrInputDocument ret = new SolrInputDocument();
		
		ret.addField("entityType", entityType);
		ret.addField("name",name);
		ret.addField("visibility",visibility);
		ret.addField("copyright",copyright);
		ret.addField("organizationCode",organizationCode);
		ret.addField("organizationDescription",organizationDescription);
		ret.addField("domainCode",domainCode);
		ret.addField("domainLangIT",domainLangIT);
		ret.addField("domainLangEN",domainLangEN);
		ret.addField("subdomainCode",subdomainCode);
		ret.addField("subdomainLangIT",subdomainLangIT);
		ret.addField("subdomainLangEN",subdomainLangEN);
		ret.addField("licenseCode",licenseCode);
		ret.addField("licenceDescription",licenceDescription);
		ret.addField("tenantCode",tenantCode);
		ret.addField("tenantsCode",tenantsCode);
		ret.addField("tenantName",tenantName);
		ret.addField("tenantDescription",tenantDescription);
		ret.addField("tagCode",tagCode);
		ret.addField("tagLangIT",tagLangIT);
		ret.addField("tagLangEN",tagLangEN);
		ret.addField("dcatDataUpdate",dcatDataUpdate);
		ret.addField("dcatNomeOrg",dcatNomeOrg);
		ret.addField("dcatEmailOrg",dcatEmailOrg);
		ret.addField("dcatCreatorName",dcatCreatorName);
		ret.addField("dcatCreatorType",dcatCreatorType);
		ret.addField("dcatCreatorId",dcatCreatorId);
		ret.addField("dcatRightsHolderName",dcatRightsHolderName);
		ret.addField("dcatRightsHolderType",dcatRightsHolderType);
		ret.addField("dcatRightsHolderId",dcatRightsHolderId);
		ret.addField("dcatReady",dcatReady);
		ret.addField("datasetCode",datasetCode);
		ret.addField("datasetDescription",datasetDescription);
		ret.addField("version",version);
		ret.addField("datasetType",dataseType);
		ret.addField("datasetSubtype",datasetSubtype);
		ret.addField("streamCode",streamCode);
		ret.addField("twtQuery",twtQuery);
		ret.addField("twtGeolocLat",twtGeolocLat);
		ret.addField("twtGeolocLon",twtGeolocLon);
		ret.addField("twtGeolocRadius",twtGeolocRadius);
		ret.addField("twtGeolocUnit",twtGeolocUnit);
		ret.addField("twtLang",twtLang);
		ret.addField("twtLocale",twtLocale);
		ret.addField("twtCount",twtCount);
		ret.addField("twtResultType",twtResultType);
		ret.addField("twtUntil",twtUntil);
		ret.addField("twtRatePercentage",twtRatePercentage);
		ret.addField("twtLastSearchId",twtLastSearchId);
		ret.addField("soCode",soCode);
		ret.addField("soName",soName);
		ret.addField("soDescription",soDescription);
		ret.addField("jsonFields",jsonFields);
		ret.addField("jsonSo",jsonSo);
		ret.addField("lat",lat);
		ret.addField("lon",lon);
		ret.addField("sdpComponentsName",sdpComponentsName);
		ret.addField("phenomenon",	phenomenon	);

		
		ret.addField("opendataAuthor",	opendataAuthor	);
		ret.addField("opendataLanguage",	opendataLanguage	);
		ret.addField("opendataMetaUpdateDate",	opendataMetaUpdateDate	);
		ret.addField("opendataUpdateDate",	opendataUpdateDate	);
		ret.addField("isOpendata",	isOpendata	);

		
		ret.addField("registrationDate",	registrationDate	);
		ret.addField("importFileType",	importFileType	);
		ret.addField("isCurrent", "1"	);

		
		
		ret.addField("id",	id	);

		return ret;
	}
	
	public void setupEngine(Stream st) {
		
		Metadata meta=MetadataFiller.fillMetadata(st);
		if (st.getSaveData()==1) {
		meta.generateNameSpace();
		meta.generateCode();
		}
		this.setupEngine(meta);
		
		
		
		

		//this.setId(dcatCreatorId);
		
		
		ArrayList<SearchEngineJsonFieldElement> arrComponents = new ArrayList<SearchEngineJsonFieldElement>();
		ArrayList<String> arraylistphen= new ArrayList<String>();
		for (Element el : st.getComponenti().getElement()) {
			SearchEngineJsonFieldElement cur = new SearchEngineJsonFieldElement();
			arraylistphen.add(el.getPhenomenon());
			cur.setComponentAlias(el.getNome());
			cur.setComponentName(el.getNome());
			cur.setDataType(el.getDataType());
			cur.setMeasureUnit(el.getMeasureUnit());
			cur.setMeasureUnitCategory(el.getMeasureUnitCategory());
			cur.setPhenomenon(el.getPhenomenon());
			cur.setPhenomenonCategory(el.getPhenomenonCategory());
			cur.setTolerance(el.getTolerance());
			arrComponents.add(cur);
			
		}
		this.setPhenomenon(arraylistphen);
		Gson gson = JSonHelper.getInstance();
		this.setJsonFields( "{\"element\": "+gson.toJson(arrComponents)+"}");
		
		
		this.setSoCode(st.getCodiceVirtualEntity());
		this.setSoDescription(st.getVirtualEntityDescription());
		this.setSoName(st.getVirtualEntityName());
		this.setStreamCode(st.getCodiceStream());
		

		this.setTwtCount(""+st.getTwtCount());
		this.setTwtGeolocLat(""+st.getTwtGeolocLat());
		this.setTwtGeolocLon(""+st.getTwtGeolocLon());
		this.setTwtGeolocRadius(""+st.getTwtGeolocRadius());
		this.setTwtGeolocUnit(""+st.getTwtGeolocUnit());
		this.setTwtLang(st.getTwtLang());
		//this.setTwtLastSearchId(st.getTwt);
		this.setTwtLocale(st.getTwtLocale());
		this.setTwtQuery(st.getTwtQuery());
		this.setTwtRatePercentage(""+st.getTwtRatePercentage());
		this.setTwtResultType(st.getTwtResultType());
		this.setTwtUntil(st.getTwtUntil());
		
		
		ArrayList<String> entity = new ArrayList<String>(Arrays.asList("stream"));
		if (st.getSaveData()==1) {
			entity.add("dataset");
		}
		this.setEntityType(entity);
		
		//TODO verificare underscore
		this.setId(tenantCode + "_" + soCode + "_" + streamCode);
	}
	
	public void setupEngine(Metadata metadata) {
		this.setCopyright(metadata.getInfo().getCopyright());
		this.setDatasetCode(metadata.getDatasetCode());
		this.setDatasetDescription(metadata.getInfo().getDescription());
		this.setDatasetSubtype(metadata.getConfigData().getSubtype());
		this.setDataseType(metadata.getConfigData().getType());
		this.setDcatCreatorId(metadata.getDcatCreatorId());
		this.setDcatCreatorName(metadata.getDcatCreatorName());
		this.setDcatCreatorType(metadata.getDcatCreatorType());
		
		
		this.setDcatDataUpdate(metadata.getDcatDataUpdate());
		this.setDcatEmailOrg(metadata.getDcatEmailOrg());
		this.setDcatNomeOrg(metadata.getDcatNomeOrg());
		this.setDcatReady(metadata.getDcatReady() == 1 ? true : false);
		this.setDcatRightsHolderId(metadata.getDcatRightsHolderId());
		this.setDcatRightsHolderName(metadata.getDcatRightsHolderName());
		this.setDcatRightsHolderType(metadata.getDcatRightsHolderType());
		this.setDomainCode(metadata.getInfo().getDataDomain());
		this.setDomainLangEN(metadata.getInfo().getDomainTranslated().get("en")); 
		this.setDomainLangIT(metadata.getInfo().getDomainTranslated().get("it")); 
		
		
		
		this.setEntityType(new ArrayList<String>(Arrays.asList("dataset")));
		
		
		this.setId(metadata.getDatasetCode());
		
		
		Gson gson = JSonHelper.getInstance();
		this.setJsonFields( gson.toJson(metadata.getInfo().getFields()));

		
		this.setLicenceDescription(metadata.getInfo().getDisclaimer()); /// TODO disclaimer?
		this.setLicenseCode(metadata.getInfo().getLicense()); //TODO licence
		this.setName(metadata.getInfo().getDatasetName());
		this.setOrganizationCode(metadata.getConfigData().getOrganizationCode());
		this.setOrganizationDescription(metadata.getConfigData().getOrganizationDescription());
		
		
		ArrayList<String> listaNomiCampi= null;
		for (Field ff : metadata.getInfo().getFields()) {
			if (listaNomiCampi==null) listaNomiCampi=new ArrayList<String>();
			listaNomiCampi.add(ff.getFieldName());
		}
		this.setSdpComponentsName(listaNomiCampi);
		
		

		this.setSubdomainCode(metadata.getInfo().getCodSubDomain());
		this.setSubdomainLangEN(metadata.getInfo().getSubDomainTranslated().get("en"));
		this.setSubdomainLangIT(metadata.getInfo().getSubDomainTranslated().get("it"));
		
		ArrayList<String> listaCodiciTag= null;
		for (Tag tg : metadata.getInfo().getTags()) {
			if (null==listaCodiciTag) {
				listaCodiciTag=new ArrayList<String>();
			}
			listaCodiciTag.add(tg.getTagCode());
		}
		this.setTagCode(listaCodiciTag);
		this.setTagLangEN(metadata.getInfo().getTagsTranslated().get("en"));
		this.setTagLangIT(metadata.getInfo().getTagsTranslated().get("it"));
		

		this.setTenantCode(metadata.getConfigData().getTenantCode());
		this.setTenantDescription(metadata.getConfigData().getTenantDescription());
		this.setTenantName(metadata.getConfigData().getTenantName());
		
		
		
		
		
		ArrayList<String> listaCodiciTenantsSh= null;
		if (null!=metadata.getInfo().getTenantssharing() && null!=metadata.getInfo().getTenantssharing().getTenantsharing()) {
			
			for (Tenantsharing ts : metadata.getInfo().getTenantssharing().getTenantsharing()) {
				if (null==listaCodiciTenantsSh) {
					listaCodiciTenantsSh=new ArrayList<String>();
				}
				listaCodiciTenantsSh.add(ts.getTenantCode());
			}
			this.setTenantsCode(listaCodiciTenantsSh);
		}
		
		
		this.setVersion(""+metadata.getDatasetVersion());
		this.setVisibility(metadata.getInfo().getVisibility());
		
		
		if(null!=metadata.getOpendata()) {
			this.setOpendata(metadata.getOpendata().isOpendata());
			if (metadata.getOpendata().isOpendata()) {
				this.setOpendataAuthor(metadata.getOpendata().getAuthor());
				this.setOpendataLanguage(metadata.getOpendata().getLanguage());
				this.setOpendataMetaUpdateDate(""+metadata.getOpendata().getMetadaUpdateDate());
				this.setOpendataUpdateDate(""+metadata.getOpendata().getDataUpdateDate());
			}
		}
		
		this.setImportFileType(metadata.getInfo().getImportFileType());
		this.setRegistrationDate(formatDate(metadata.getInfo().getRegistrationDate()));
		
	}
	
	private String formatDate(Date date) {
		String formattedDate = null;
		if (date != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			formattedDate = df.format(date);
		}
		return formattedDate;
	}
	
	
}
