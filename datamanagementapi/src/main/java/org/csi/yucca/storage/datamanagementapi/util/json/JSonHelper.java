package org.csi.yucca.storage.datamanagementapi.util.json;

import org.csi.yucca.storage.datamanagementapi.util.Constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSonHelper {
	private static Gson gson;

	public static Gson getInstance() {
		if (gson == null)
			gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).setDateFormat(Constants.DATE_FORMAT).disableHtmlEscaping()
					.setPrettyPrinting().serializeNulls().create();
		return gson;
	}

}
