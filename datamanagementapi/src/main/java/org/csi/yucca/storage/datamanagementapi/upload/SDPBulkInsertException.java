package org.csi.yucca.storage.datamanagementapi.upload;

import org.csi.yucca.storage.datamanagementapi.util.json.IgnoredJSON;

public class SDPBulkInsertException {

	public static final int ERROR_TYPE_TOTALFIELDINROW = 1;
	public static final int ERROR_TYPE_INVALIDTYPE = 2;
	public static final int ERROR_TYPE_COLUMNNOTFOUND = 3;

	private String rowAffected = null;
	private Integer rowAffectedNumber = -1;
	private Integer columnNumber = -1;

	@IgnoredJSON
	private String[] errorMessages = new String[] { "undefined error", "row containing an invalid number of column", "invalid data type in colum",
			"column not found" };
	private String errorDetail = null;
	@IgnoredJSON
	private int errorType = -1;

	public SDPBulkInsertException(int errorType, String rowAffected, Integer rowAffectedNumber, Integer columnNumber, String messageDetail) {
		this.errorDetail = messageDetail;
		this.errorType = errorType;
		this.rowAffected = rowAffected;
		this.rowAffectedNumber = rowAffectedNumber;
		this.columnNumber = columnNumber;
		if (this.errorType < 1 || this.errorType > 3)
			this.errorType = 0;
	}

	public String getRowAffected() {
		return rowAffected == null ? "-" : rowAffected;
	}

	public String getRowAffectedNumber() {
		return rowAffectedNumber == null ? "-" : rowAffectedNumber.toString();
	}

	public String getColumnNumber() {
		return columnNumber == null ? "-" : columnNumber.toString();
	}

	public String[] getErrorMessages() {
		return errorMessages;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public int getErrorType() {
		return errorType;
	}

	public String toString() {
		String ret = "";
		ret += errorMessages[this.errorType];
		if (this.rowAffectedNumber != -1)
			ret += " (rowNumber:" + this.rowAffectedNumber + ") ";
		if (this.columnNumber != -1)
			ret += " (columnNumber:" + this.columnNumber + ") ";
		if (null != this.errorDetail && this.errorDetail.trim().length() > 0)
			ret += " (detail: " + this.errorDetail + ")";

		return ret;
	}

}
