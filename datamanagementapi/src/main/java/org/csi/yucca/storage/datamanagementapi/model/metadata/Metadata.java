package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.util.Constants;
import org.csi.yucca.storage.datamanagementapi.util.Util;
import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Metadata extends AbstractEntity {

	public static final String CONFIG_DATA_TYPE_DATASET = "dataset";
	public static final String CONFIG_DATA_SUBTYPE_BULK_DATASET = "bulkDataset";
	public static final String CONFIG_DATA_SUBTYPE_STREAM_DATASET = "streamDataset";

	private String id;
	private Long idDataset; // max dei presenti (maggiore di un milione)

	private String datasetCode; // trim del nome senza caratteri speciali, max
								// 12 _ idDataset
	private Integer datasetVersion;

	private ConfigData configData;
	private Info info;

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

	public void generateCode() {
		String code = null;

		// "ds_debsStream_123", // per Stream "ds"_<streamCode>_<idDataset>, per
		// Bulk NoSpec(Max12(Trim(<info.datasetName>)))_<idDataset>
		if (idDataset != null) { // trim del nome senza caratteri speciali, max
									// 12 _ idDataset

			String prefix = "";
			if (getConfigData() != null && CONFIG_DATA_SUBTYPE_STREAM_DATASET.equals(getConfigData().getSubtype()))
				prefix = "ds_";

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

}
