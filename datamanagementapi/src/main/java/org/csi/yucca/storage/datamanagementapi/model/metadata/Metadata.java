package org.csi.yucca.storage.datamanagementapi.model.metadata;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.ImageProcessor;
import org.csi.yucca.storage.datamanagementapi.util.Util;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Metadata extends AbstractEntity {

	public static final String CONFIG_DATA_TYPE_DATASET = "dataset";
	public static final String CONFIG_DATA_SUBTYPE_BULK_DATASET = "bulkDataset";
	public static final String CONFIG_DATA_SUBTYPE_BINARY_DATASET = "binaryDataset";
	public static final String CONFIG_DATA_SUBTYPE_STREAM_DATASET = "streamDataset";
	public static final String CONFIG_DATA_SUBTYPE_SOCIAL_DATASET = "socialDataset";
	public static final String CONFIG_DATA_TYPE_API = "api";
	public static final String CONFIG_DATA_SUBTYPE_API_MULTI_BULK = "apiMultiBulk";

	public static final String CONFIG_DATA_DEFAULT_COLLECTION_SOCIAL = "social";

	private String id;
	private Long idDataset; // max dei presenti (maggiore di un milione)

	private String datasetCode; // trim del nome senza caratteri speciali, max
								// 12 _ idDataset
	private Integer datasetVersion;

	private ConfigData configData;
	private Info info;
	private Opendata opendata;

	private int dcatReady;
	private String dcatCreatorName;
	private String dcatCreatorType;
	private String dcatCreatorId;
	private String dcatRightsHolderName;
	private String dcatRightsHolderType;
	private String dcatRightsHolderId;
	private String dcatNomeOrg;
	private String dcatEmailOrg;
	private String dcatDataUpdate;

	private boolean availableSpeed;
	private boolean isTransformed;
	private boolean availableHive;
	private String dbHiveSchema;
	private String dbHiveTable;

	public boolean getAvailableSpeed() {
		return availableSpeed;
	}

	public void setAvailableSpeed(boolean availableSpeed) {
		this.availableSpeed = availableSpeed;
	}

	public boolean getIsTransformed() {
		return isTransformed;
	}

	public void setIsTransformed(boolean isTransformed) {
		this.isTransformed = isTransformed;
	}

	public boolean getAvailableHive() {
		return availableHive;
	}

	public void setAvailableHive(boolean availableHive) {
		this.availableHive = availableHive;
	}

	public String getDbHiveSchema() {
		return dbHiveSchema;
	}

	public void setDbHiveSchema(String dbHiveSchema) {
		this.dbHiveSchema = dbHiveSchema;
	}

	public String getDbHiveTable() {
		return dbHiveTable;
	}

	public void setDbHiveTable(String dbHiveTable) {
		this.dbHiveTable = dbHiveTable;
	}

	public String getDcatDataUpdate() {
		return dcatDataUpdate;
	}

	public void setDcatDataUpdate(String dcatDataUpdate) {
		this.dcatDataUpdate = dcatDataUpdate;
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

	public static Metadata fromJson(String json) {
		Gson gson = JSonHelper.getInstance();
		return gson.fromJson(json, Metadata.class);
	}

	public Metadata() {
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ConfigData getConfigData() {
		return configData;
	}

	public void setConfigData(ConfigData configData) {
		this.configData = configData;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public Long getIdDataset() {
		return idDataset;
	}

	public void setIdDataset(Long idDataset) {
		this.idDataset = idDataset;
	}

	public String getDatasetCode() {
		return datasetCode;
	}

	public void setDatasetCode(String datasetCode) {
		this.datasetCode = datasetCode;
	}

	public Integer getDatasetVersion() {
		return datasetVersion;
	}

	public void setDatasetVersion(Integer datasetVersion) {
		this.datasetVersion = datasetVersion;
	}

	public Opendata getOpendata() {
		return opendata;
	}

	public void setOpendata(Opendata opendata) {
		this.opendata = opendata;
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

	public void generateCode() {
		String code = null;

		// "ds_debsStream_123", // per Stream "ds"_<streamCode>_<idDataset>, per
		// Bulk NoSpec(Max12(Trim(<info.datasetName>)))_<idDataset>
		if (idDataset != null) { // trim del nome senza caratteri speciali, max
									// 12 _ idDataset

			String prefix = "";
			if (getConfigData() != null
					&& (CONFIG_DATA_SUBTYPE_STREAM_DATASET.equals(getConfigData().getSubtype()) || CONFIG_DATA_SUBTYPE_SOCIAL_DATASET.equals(getConfigData().getSubtype())))
				prefix = "ds_";
			else if (getConfigData() != null && CONFIG_DATA_SUBTYPE_BINARY_DATASET.equals(getConfigData().getSubtype()))
				prefix = "bn_";

			String datasetNameSafe = "";
			if (getInfo() != null)
				datasetNameSafe = Util.safeSubstring(Util.cleanStringCamelCase(getInfo().getDatasetName()), 12);

			code = prefix + datasetNameSafe + "_" + idDataset;

		}
		setDatasetCode(code);
	}

	public void generateNameSpace() {
		if (idDataset != null && getConfigData() != null) {
			String nameSpace = Constants.API_NAMESPACE_BASE + "." + getConfigData().getTenantCode() + "." + getDatasetCode();
			getConfigData().setEntityNameSpace(nameSpace);
		}
	}

	public static Metadata createBinaryMetadata(Metadata parentMetadata) {
		Metadata binaryMetadata = new Metadata();
		binaryMetadata.setDatasetVersion(1);

		Info binaryMetadataInfo = new Info();
		Field[] binaryFields = binaryDatasetBaseFields();
		binaryMetadataInfo.setDatasetName(parentMetadata.getInfo().getDatasetName());
		binaryMetadataInfo.setFields(binaryFields);
		binaryMetadata.setInfo(binaryMetadataInfo);

		ConfigData binaryMetadataConfigData = new ConfigData();
		binaryMetadataConfigData.setArchive(parentMetadata.getConfigData().getArchive());
		binaryMetadataConfigData.setCollection(parentMetadata.getConfigData().getCollection());
		binaryMetadataConfigData.setCurrent(parentMetadata.getConfigData().getCurrent());
		binaryMetadataConfigData.setDatabase(parentMetadata.getConfigData().getDatabase());
		binaryMetadataConfigData.setDatasetStatus(parentMetadata.getConfigData().getDatasetStatus());
		binaryMetadataConfigData.setEntityNameSpace(parentMetadata.getConfigData().getEntityNameSpace());
		binaryMetadataConfigData.setIdTenant(parentMetadata.getConfigData().getIdTenant());
		binaryMetadataConfigData.setTenantCode(parentMetadata.getConfigData().getTenantCode());
		binaryMetadataConfigData.setType(Metadata.CONFIG_DATA_TYPE_DATASET);
		binaryMetadataConfigData.setSubtype(Metadata.CONFIG_DATA_SUBTYPE_BINARY_DATASET);

		binaryMetadata.setConfigData(binaryMetadataConfigData);

		return binaryMetadata;
	}

	public static Field[] binaryDatasetBaseFields() {

		Field idBinaryField = new Field();
		idBinaryField.setDataType("long");
		idBinaryField.setFieldName("idBinary");
		idBinaryField.setFieldAlias("Id ");

		Field filenameBinaryField = new Field();
		filenameBinaryField.setDataType("string");
		filenameBinaryField.setFieldName("filenameBinary");
		filenameBinaryField.setFieldAlias("File Name");

		Field aliasNameBinaryField = new Field();
		aliasNameBinaryField.setDataType("string");
		aliasNameBinaryField.setFieldName("aliasNameBinary");
		aliasNameBinaryField.setFieldAlias("File Alias");

		Field sizeBinaryField = new Field();
		sizeBinaryField.setDataType("string");
		sizeBinaryField.setFieldName("sizeBinary");
		sizeBinaryField.setFieldAlias("File Size");

		Field insertDateBinaryField = new Field();
		insertDateBinaryField.setDataType("date");
		insertDateBinaryField.setFieldName("insertDateBinary");
		insertDateBinaryField.setFieldAlias("Insert Date");

		Field lastUpdateDateBinaryField = new Field();
		lastUpdateDateBinaryField.setDataType("date");
		lastUpdateDateBinaryField.setFieldName("lastUpdateDateBinary");
		lastUpdateDateBinaryField.setFieldAlias("Last Update");

		Field contentTypeBinaryField = new Field();
		contentTypeBinaryField.setDataType("string");
		contentTypeBinaryField.setFieldName("contentTypeBinary");
		contentTypeBinaryField.setFieldAlias("Content Type");

		Field pathBinaryField = new Field();
		pathBinaryField.setDataType("string");
		pathBinaryField.setFieldName("urlDownloadBinary");
		pathBinaryField.setFieldAlias("Download url");

		Field metadataBinaryField = new Field();
		metadataBinaryField.setDataType("string");
		metadataBinaryField.setFieldName("metadataBinary");
		metadataBinaryField.setFieldAlias("Metadata");

		return new Field[] { idBinaryField, filenameBinaryField, aliasNameBinaryField, sizeBinaryField, insertDateBinaryField, lastUpdateDateBinaryField, contentTypeBinaryField,
				pathBinaryField, metadataBinaryField };
	}

	public byte[] readDatasetIconBytes() throws IOException {
		String imageBase64 = this.getInfo().getIcon();
		BufferedImage imag = null;

		if (imageBase64 != null) {
			String[] imageBase64Array = imageBase64.split(",");

			String imageBase64Clean;
			if (imageBase64Array.length > 1) {
				imageBase64Clean = imageBase64Array[1];
			} else {
				imageBase64Clean = imageBase64Array[0];
			}

			byte[] bytearray = Base64.decodeBase64(imageBase64Clean.getBytes());
			imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		}
		if (imageBase64 == null || imag == null) {
			imag = ImageIO.read(ImageProcessor.class.getClassLoader().getResourceAsStream(Constants.DEFAULT_IMAGE));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(imag, "png", baos);
		baos.flush();
		byte[] iconBytes = baos.toByteArray();
		baos.close();
		return iconBytes;
	}

	public boolean hasFieldNameDuplicate() {
		Set<String> hashSet = new HashSet<String>();
		boolean hasDuplicate = false;
		if (this.getInfo() != null && this.getInfo().getFields() != null) {
			for (Field field : this.getInfo().getFields()) {
				if (!hashSet.add(field.getFieldName())) {
					hasDuplicate = true;
					break;
				}
			}
		}
		return hasDuplicate;
	}

	public boolean hasField(Field fieldTocheck) {
		if (this.getInfo() != null && this.getInfo().getFields() != null)
			for (Field field : this.getInfo().getFields()) {
				if (field.getFieldName().equals(field.getFieldName()))
					return true;
			}
		return false;
	}

	public boolean hasFieldWithSameSourceColumnName(Field fieldTocheck) {
		if (fieldTocheck != null && fieldTocheck.getSourceColumnName() != null && this.getInfo() != null && this.getInfo().getFields() != null)
			for (Field field : this.getInfo().getFields()) {
				if (field.getSourceColumnName() != null && field.getSourceColumnName().equals(fieldTocheck.getSourceColumnName()))
					return true;
			}
		return false;
	}

	public Field getFieldFromSourceColumnName(String sourceColumnName) {
		if (sourceColumnName != null && this.getInfo() != null && this.getInfo().getFields() != null)
			for (Field field : this.getInfo().getFields()) {
				if (field.getSourceColumnName() != null && field.getSourceColumnName().equals(sourceColumnName))
					return field;
			}
		return null;
	}

	public Field getFieldFromFieldName(String fieldName) {
		if (fieldName != null && this.getInfo() != null && this.getInfo().getFields() != null)
			for (Field field : this.getInfo().getFields()) {
				if (field.getFieldName() != null && field.getFieldName().equals(fieldName))
					return field;
			}
		return null;
	}

	public static Metadata slimmify(Metadata metadata) {
		Metadata metadataSlim = new Metadata();
		metadataSlim.setDatasetCode(metadata.getDatasetCode());
		metadataSlim.setDatasetVersion(metadata.getDatasetVersion());
		if (metadata != null) {
			if (metadata.getInfo() != null) {
				Info info = new Info();
				if(metadata.getInfo().getIcon()==null)
					info.setIcon("img/dataset-icon-default.png");
				else if(metadata.getInfo().getIcon().equals("img/stream-icon-default.png") || metadata.getInfo().getIcon().equals("img/dataset-icon-default.png"))
					info.setIcon(metadata.getInfo().getIcon());
					
					
				//info.setIcon(metadata.getInfo().getIcon());
				info.setDatasetName(metadata.getInfo().getDatasetName());
				info.setDataDomain(metadata.getInfo().getDataDomain());
				info.setCodSubDomain(metadata.getInfo().getCodSubDomain());
				info.setBinaryIdDataset(metadata.getInfo().getBinaryIdDataset());
				info.setRegistrationDate(metadata.getInfo().getRegistrationDate());
				metadataSlim.setInfo(info);
			}
			if (metadata.getConfigData() != null) {
				ConfigData configData = new ConfigData();
				configData.setType(metadata.getConfigData().getType());
				configData.setSubtype(metadata.getConfigData().getSubtype());
				configData.setDeleted(metadata.getConfigData().getDeleted());
				metadataSlim.setConfigData(configData);
			}

		}
		return metadataSlim;
	}

}
