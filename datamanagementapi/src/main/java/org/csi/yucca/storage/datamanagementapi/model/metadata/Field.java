package org.csi.yucca.storage.datamanagementapi.model.metadata;

import org.csi.yucca.storage.datamanagementapi.util.json.JSonHelper;

import com.google.gson.Gson;

public class Field extends AbstractEntity {
	
	private String fieldName;
	private String fieldAlias;
	private String dataType;
	private Integer sourceColumn;
	private Integer isKey;
	private String measureUnit;

	public Field() {
	}

	public String toJson() {
		Gson gson = JSonHelper.getInstance();
		return gson.toJson(this);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		this.fieldAlias = fieldAlias;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getSourceColumn() {
		return sourceColumn;
	}

	public void setSourceColumn(Integer sourceColumn) {
		this.sourceColumn = sourceColumn;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public Integer getIsKey() {
		return isKey;
	}

	public void setIsKey(Integer isKey) {
		this.isKey = isKey;
	}

}
