package org.csi.yucca.storage.datamanagementapi.upload;

public class SDPBulkInsertException {

	public static final String[] ERROR_TYPE_UNDEFINED = new String[] { "ERROR_UNDEFINED", "Undefined error" };
	public static final String[] ERROR_TYPE_TOTALFIELDINROW = new String[] { "ERROR_FIELD_COUNT", "Row containing an invalid number of column" };
	public static final String[] ERROR_TYPE_INVALIDTYPE = new String[] { "ERROR_INVALIDTYPE", "Invalid data type in colum" };
	public static final String[] ERROR_TYPE_COLUMNNOTFOUND = new String[] { "ERROR_COLUMN_NOT_FOUND", "Undefined error" };

	private String rowAffected = null;
	private Integer rowAffectedNumber = -1;
	private Integer columnNumber = -1;

	private String errorCode;
	private String errorMessage;
	private String errorDetail;

	public SDPBulkInsertException(String[] errorType, String rowAffected, Integer rowAffectedNumber, Integer columnNumber, String messageDetail) {
		this.setErrorDetail(messageDetail);
		this.setErrorCode(errorType[0]);
		this.setErrorMessage(errorType[1]);
		this.rowAffected = rowAffected;
		this.rowAffectedNumber = rowAffectedNumber;
		this.columnNumber = columnNumber;

		String detail = "";
		if (this.rowAffectedNumber != -1)
			detail += "ROW " + this.rowAffectedNumber + " - ";
		if (this.columnNumber != -1)
			detail += " COLUMN " + this.columnNumber + " - ";
		if (null != this.errorDetail && this.errorDetail.trim().length() > 0)
			detail += " DETAIL " + messageDetail;
		setErrorDetail(detail);

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

	public String getErrorDetail() {
		return errorDetail;
	}

	public String toString() {
		return getErrorDetail();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

}
