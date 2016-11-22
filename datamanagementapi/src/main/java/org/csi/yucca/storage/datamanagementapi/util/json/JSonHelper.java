package org.csi.yucca.storage.datamanagementapi.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSonHelper {
	private static Gson gson;

	public static Gson getInstance() {
		if (gson == null)
			gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).disableHtmlEscaping().setPrettyPrinting().create();
		return gson;
	}
	
	public static String escapeJS(String string) {
        String escapes[][] = new String[][]{
                {"\\", "\\\\"},
                {"\"", "\\\""},
                {"\n", "\\n"},
                {"\r", "\\r"},
                {"\b", "\\b"},
                {"\f", "\\f"},
                {"\t", "\\t"}
        };
        for (String[] esc : escapes) {
            string = string.replace(esc[0], esc[1]);
        }
        return string;
    }
}
