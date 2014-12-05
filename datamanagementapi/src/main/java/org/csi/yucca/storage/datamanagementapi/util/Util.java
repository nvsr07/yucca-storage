package org.csi.yucca.storage.datamanagementapi.util;

import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Field;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Info;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;

public class Util {
	public static String nvl(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String nvlt(Object o) {
		return nvl(o).trim();
	}

	public static String cleanString(String in) {
		if (in != null)
			return in.replaceAll(" ", "").replaceAll("[^\\w\\s]", "");

		return "";
	}

	static String toProperCase(String in) {
		if (in != null && in.length() > 1)
			return in.substring(0, 1).toUpperCase() + in.substring(1).toLowerCase();
		else if (in != null)
			return in.toUpperCase();
		return "";

	}

	public static String safeSubstring(String in, int length) {
		String out = in;
		if (in != null && in.length() > length)
			out = in.substring(0, length);

		return out == null ? "" : out;
	}

	public static String cleanStringCamelCase(String in) {
		String out = "";
		if (in != null) {
			String[] words = in.split(" ");
			for (String word : words) {
				out += toProperCase(cleanString(word));
			}
		}

		return out;
	}

	public static String cleanStringCamelCase(String in, int length) {
		return safeSubstring(cleanStringCamelCase(in), length);
	}

	public static void main(String[] args) {
		// String json =
		// "{ configData : { idDataset : \"primoID\", \"tenant\" : \"iotnet\", \"collection\" : \"provaAle\", \"database\": \"db\", \"type\" : \"dataset\", \"subtype\" : \"bulkDataset\", \"entityNameSpace\" : \"it.csi.smartdata.odata.iotnet.iotnetapi002\", \"datasetversion\" : \"1.0.0\", \"current\" : 1, \"archive\" : { \"archiveCollection\" : \"altraCollection\", \"archiveDatabase\": \"altroDB\",  \"archiveInfo\": [ { \"archiveRevision\": 1, \"archiveDate\" : null }, { \"archiveRevision\": 2, \"archiveDate\" : null } ]	 }			 }, \"metadata\" : { \"name\" : \"primodato\", \"description\" : \"Descrizione\", \"license\" : \"CC BY 4.0\", \"disclaimer\" : null, \"copyright\" : \"Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.\", \"visibility\" : \"public\", \"registrationDate\" : null, \"requestorName\" : \"nomeRichiedente\", \"requestorSurname\" : \"cognomeRichiedente\", \"dataDomain\" : \"ENVIRONMENT\", \"requestornEmail\" : \"ciao@email.it\", \"fps\" : 22,  \"startIngestionDate\" : null, \"endIngestionDate\" : null, \"importFileType\" : \"CSV\", \"datasetStatus\": \"Ok\",  \"tags\" : [{ \"tagCode\" : \"AIR\" }, { \"tagCode\" : \"INDOOR\" }, { \"tagCode\" : \"POLLUTION\" }, { \"tagCode\" : \"QUALITY\" } ], \"fields\" : [{ \"fieldName\" : \"campo_1\", \"fieldAlias\" : \"semantica campo 1\", \"dataType\" : \"int\", \"sourceColumn\" : \"1\", \"isKey\" : 0, \"measureUnit\" : \"ppm\" }, { \"fieldName\" : \"campo_2\", \"fieldAlias\" : \"semantica campo 2\", \"dataType\" : \"string\", \"sourceColumn\" : \"2\", \"isKey\" : 0, \"measureUnit\" : \"metri\" } ] } }";
		// String json2 =
		// "{ configData : { idDataset : \"secondoID\", \"tenant\" : \"iotnet\", \"collection\" : \"provaAle\", \"database\": \"db\", \"type\" : \"dataset\", \"subtype\" : \"bulkDataset\", \"entityNameSpace\" : \"it.csi.smartdata.odata.iotnet.iotnetapi002\", \"datasetversion\" : \"1.0.0\", \"current\" : 1, \"archive\" : { \"archiveCollection\" : \"altraCollection\", \"archiveDatabase\": \"altroDB\",  \"archiveInfo\": [ { \"archiveRevision\": 1, \"archiveDate\" : null }, { \"archiveRevision\": 2, \"archiveDate\" : null } ]	 }			 }, \"metadata\" : { \"name\" : \"primodato\", \"description\" : \"Descrizione\", \"license\" : \"CC BY 4.0\", \"disclaimer\" : null, \"copyright\" : \"Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.\", \"visibility\" : \"public\", \"registrationDate\" : null, \"requestorName\" : \"nomeRichiedente\", \"requestorSurname\" : \"cognomeRichiedente\", \"dataDomain\" : \"ENVIRONMENT\", \"requestornEmail\" : \"ciao@email.it\", \"fps\" : 22,  \"startIngestionDate\" : null, \"endIngestionDate\" : null, \"importFileType\" : \"CSV\", \"datasetStatus\": \"Ok\",  \"tags\" : [{ \"tagCode\" : \"AIR\" }, { \"tagCode\" : \"INDOOR\" }, { \"tagCode\" : \"POLLUTION\" }, { \"tagCode\" : \"QUALITY\" } ], \"fields\" : [{ \"fieldName\" : \"campo_1\", \"fieldAlias\" : \"semantica campo 1\", \"dataType\" : \"int\", \"sourceColumn\" : \"1\", \"isKey\" : 0, \"measureUnit\" : \"ppm\" }, { \"fieldName\" : \"campo_2\", \"fieldAlias\" : \"semantica campo 2\", \"dataType\" : \"string\", \"sourceColumn\" : \"2\", \"isKey\" : 0, \"measureUnit\" : \"metri\" } ] } }";
		// Gson gson = new GsonBuilder().setExclusionStrategies(new
		// GSONExclusionStrategy()).create();
		// Metadata d1 = gson.fromJson(json,Metadata.class);
		// Metadata d2 = gson.fromJson(json2,Metadata.class);
		// d1.setId("nuovoID");
		// System.out.println("1 ok" + d1.toJson());
		//
		// List<Metadata> result = new LinkedList<Metadata>();
		// result.add(d1);
		// result.add(d2);
		// System.out.println("2 ok" + gson.toJson(result));
		//
		// String json3 =
		// "{\"dataset\":{\"id\":\"5459050d73456b346624b067\",\"configData\":{\"idDataset\":\"quarto\",\"tenant\":\"sandbox\",\"type\":\"dataset\",\"subtype\":\"bulkDataset\",\"datasetversion\":\"1\",\"current\":\"1\"},\"metadata\":{\"name\":\"aaa\",\"registrationDate\":\"Nov 4, 2014 5:55:41 PM\",\"requestorName\":\"aa\",\"requestorSurname\":\"aa\",\"dataDomain\":\"AGRICULTURE\",\"requestornEmail\":\"aa\",\"tags\":[{}],\"visibility\":\"public\",\"license\":\"lll\"}}}";
		// Metadata d3 = Metadata.fromJson(json3);
		// System.out.println("3ok" + gson.toJson(d3));
		Metadata m = new Metadata();
		
		Info info = new Info();
		Field[] fields = new Field[1];
		info.setFields(fields);
		m.setInfo(info);;
		
	}

	public static String join(String[] strings, String glue) {

		String result = "";
		if (glue == null)
			glue = ",";

		if (strings != null) {
			int counter = 0;
			for (String s : strings) {
				result += s;
				counter++;
				if (counter < strings.length)
					result += glue;
			}
		}
		return result;
	}

	public static String join(List<Object> objectList, String glue) {

		String out = "";
		int counter = 0;
		if (objectList != null) {
			for (Object o : objectList) {
				if (o instanceof String)
					out += o;
				else
					out += o.toString();
				if (counter < objectList.size() - 1)
					out += glue;
				counter++;
			}
		}
		return out;
	}

}
