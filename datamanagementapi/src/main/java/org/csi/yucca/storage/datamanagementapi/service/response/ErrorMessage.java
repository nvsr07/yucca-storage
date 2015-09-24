package org.csi.yucca.storage.datamanagementapi.service.response;

public class ErrorMessage {

	public static final String GENERIC = "GENERIC_ERROR";
	public static final String UNSUPPORTED_FORMAT = "UNSUPPORTED_FORMAT";
	private String code;
	private String message;
	private String detail;

	public ErrorMessage() {
		super();
	}

	public ErrorMessage(String code, String message, String detail) {
		super();
		this.code = code;
		this.message = message;
		this.detail = detail;
	}

	public ErrorMessage(Exception ex) {
		this.code = ErrorMessage.GENERIC;
		this.message = ex.getMessage();

		java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
		ex.printStackTrace(new java.io.PrintStream(os));
		this.detail = os.toString();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
