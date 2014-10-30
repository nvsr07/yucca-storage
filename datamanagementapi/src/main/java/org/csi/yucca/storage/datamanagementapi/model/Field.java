package org.csi.yucca.storage.datamanagementapi.model;

public class Field extends AbstractEntity{
	private String fieldName;
	private String fieldAlias;
	private String dataType;
	private String sourceColumn;
	private boolean isKey;
	private String measureUnit;

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

	public String getSourceColumn() {
		return sourceColumn;
	}

	public void setSourceColumn(String sourceColumn) {
		this.sourceColumn = sourceColumn;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

}
