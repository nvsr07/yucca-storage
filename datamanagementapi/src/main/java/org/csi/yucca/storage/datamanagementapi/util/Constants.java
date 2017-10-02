package org.csi.yucca.storage.datamanagementapi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Constants {
	public static final String API_NAMESPACE_BASE = "it.csi.smartdata.odata";
	public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.S";
	public static final int MAX_NUM_ROW_DATA_DOWNLOAD = 10000;
	public static final String DEFAULT_IMAGE = "smart.png";
	public static final int DEFAULT_IMAGE_WIDTH = 256;
	public static final int DEFAULT_IMAGE_HEIGHT = 256;

	// For the image: font Arial 24 Bold, fill white, Stroke no, Antialias,
	// Kerning 175, Leading 100
	public static final String DEFAULT_ODATA_IMAGE = "odataOverlay.png";
	public static final String DEFAULT_ODATA_TWITTER_IMAGE = "odataTwitterOverlay.png";
	public static final String DEFAULT_STREAM_IMAGE = "streamOverlay.png";
	public static final String DEFAULT_STREAM_TWITTER_IMAGE = "streamTwitterOverlay.png";
	public static final String DEFAULT_TWITTER_IMAGE = "twitterOverlay.png";

	public static final Integer VIRTUAL_ENTITY_TWITTER_TYPE_ID = 3;

	public static final String[] LANGUAGES_SUPPORTED = new String[] { "it", "en" };

	public static final DateFormat DEFAULT_FIELD_DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.getDefault());

	public static final String OPENDATA_EXPORT_FORMAT_CKAN = "ckan";

	public static final DateFormat ISO_DATE_FORMAT() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		isoDateFormat.setTimeZone(tz);
		return isoDateFormat;
	}

	public static final String SUBDOMAIN_VALIDATION_PATTERN = "^[\\S]*$";
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static void main(String[] args) {
		// System.out.println(DEFAULT_FIELD_DATE_FORMAT);
		// DateFormat formatter = Constants.DEFAULT_FIELD_DATE_FORMAT;
		// formatter.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
		// System.out.println(formatter.toString());

		String subDomain = "ciax@spa. com";
		if (!subDomain.matches(SUBDOMAIN_VALIDATION_PATTERN))
			// if(!Constants.SUBDOMAIN_VALIDATION_PATTERN.matcher(subDomain).matches()){
			System.out.println("non vale");

		else
			System.out.println("ok");
		
		Boolean b = null;
		if(b!=null && b)
			System.out.println("b null risulta vero!");
		else
			System.out.println("b null è falso :)");
	}
}
