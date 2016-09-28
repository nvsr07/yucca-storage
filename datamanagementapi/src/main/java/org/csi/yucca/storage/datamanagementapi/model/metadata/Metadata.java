package org.csi.yucca.storage.datamanagementapi.model.metadata;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
	private Dcat dcat;

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

	public Dcat getDcat() {
		return dcat;
	}

	public void setDcat(Dcat dcat) {
		this.dcat = dcat;
	}

	public void generateCode() {
		String code = null;

		// "ds_debsStream_123", // per Stream "ds"_<streamCode>_<idDataset>, per
		// Bulk NoSpec(Max12(Trim(<info.datasetName>)))_<idDataset>
		if (idDataset != null) { // trim del nome senza caratteri speciali, max
									// 12 _ idDataset

			String prefix = "";
			if (getConfigData() != null && (CONFIG_DATA_SUBTYPE_STREAM_DATASET.equals(getConfigData().getSubtype()) || CONFIG_DATA_SUBTYPE_SOCIAL_DATASET.equals(getConfigData().getSubtype())))
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

		return new Field[] { idBinaryField, filenameBinaryField, aliasNameBinaryField, sizeBinaryField, insertDateBinaryField, lastUpdateDateBinaryField,
				contentTypeBinaryField, pathBinaryField, metadataBinaryField };
	}

	public byte[] readDatasetIconBytes() throws IOException{
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

}
