package org.csi.yucca.storage.datamanagementapi.importdatabase;

import java.util.LinkedList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;

public class DatabaseTableDataset {

	public static final String DATABASE_TABLE_DATASET_STATUS_NEW = "new";
	public static final String DATABASE_TABLE_DATASET_STATUS_EXISTING = "existing";

	private String tableName;
	private String tableType;
	private String status;
	private Metadata dataset;
	private List<String> warnings;
	private List<Field> newFields;

	public DatabaseTableDataset() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Metadata getDataset() {
		return dataset;
	}

	public void setDataset(Metadata dataset) {
		this.dataset = dataset;
	}

	public List<Field> getNewFields() {
		return newFields;
	}

	public void setNewFields(List<Field> newFields) {
		this.newFields = newFields;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	
	public void addWarning(String warning){
		if(warnings==null)
			warnings = new LinkedList<String>();
		warnings.add(warning);
	}
}
