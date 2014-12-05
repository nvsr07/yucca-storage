package org.csi.yucca.storage.datamanagementapi.util;

import java.text.DateFormat;
import java.util.Locale;

public class Constants {
	public static final String API_NAMESPACE_BASE = "it.csi.smartdata.odata";
	public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.S";
	public static final int MAX_NUM_ROW_DATA_DOWNLOAD = 10000;

	public static final DateFormat DEFAULT_FIELD_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault());

}
