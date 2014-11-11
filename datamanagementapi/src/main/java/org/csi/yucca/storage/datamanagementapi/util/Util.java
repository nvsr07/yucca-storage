package org.csi.yucca.storage.datamanagementapi.util;

import java.util.LinkedList;
import java.util.List;

import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.util.json.GSONExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Util {
	public static String nvl(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String nvlt(Object o) {
		return nvl(o).trim();
	}
	
	public static void main(String[] args) {
		String json = "{ configData : { idDataset : \"primoID\", \"tenant\" : \"iotnet\", \"collection\" : \"provaAle\", \"database\": \"db\", \"type\" : \"dataset\", \"subtype\" : \"bulkDataset\", \"entityNameSpace\" : \"it.csi.smartdata.odata.iotnet.iotnetapi002\", \"datasetversion\" : \"1.0.0\", \"current\" : 1, \"archive\" : { \"archiveCollection\" : \"altraCollection\", \"archiveDatabase\": \"altroDB\",  \"archiveInfo\": [ { \"archiveRevision\": 1, \"archiveDate\" : null }, { \"archiveRevision\": 2, \"archiveDate\" : null } ]	 }			 }, \"metadata\" : { \"name\" : \"primodato\", \"description\" : \"Descrizione\", \"license\" : \"CC BY 4.0\", \"disclaimer\" : null, \"copyright\" : \"Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.\", \"visibility\" : \"public\", \"registrationDate\" : null, \"requestorName\" : \"nomeRichiedente\", \"requestorSurname\" : \"cognomeRichiedente\", \"dataDomain\" : \"ENVIRONMENT\", \"requestornEmail\" : \"ciao@email.it\", \"fps\" : 22,  \"startIngestionDate\" : null, \"endIngestionDate\" : null, \"importFileType\" : \"CSV\", \"datasetStatus\": \"Ok\",  \"tags\" : [{ \"tagCode\" : \"AIR\" }, { \"tagCode\" : \"INDOOR\" }, { \"tagCode\" : \"POLLUTION\" }, { \"tagCode\" : \"QUALITY\" } ], \"fields\" : [{ \"fieldName\" : \"campo_1\", \"fieldAlias\" : \"semantica campo 1\", \"dataType\" : \"int\", \"sourceColumn\" : \"1\", \"isKey\" : 0, \"measureUnit\" : \"ppm\" }, { \"fieldName\" : \"campo_2\", \"fieldAlias\" : \"semantica campo 2\", \"dataType\" : \"string\", \"sourceColumn\" : \"2\", \"isKey\" : 0, \"measureUnit\" : \"metri\" } ] } }";
		String json2 = "{ configData : { idDataset : \"secondoID\", \"tenant\" : \"iotnet\", \"collection\" : \"provaAle\", \"database\": \"db\", \"type\" : \"dataset\", \"subtype\" : \"bulkDataset\", \"entityNameSpace\" : \"it.csi.smartdata.odata.iotnet.iotnetapi002\", \"datasetversion\" : \"1.0.0\", \"current\" : 1, \"archive\" : { \"archiveCollection\" : \"altraCollection\", \"archiveDatabase\": \"altroDB\",  \"archiveInfo\": [ { \"archiveRevision\": 1, \"archiveDate\" : null }, { \"archiveRevision\": 2, \"archiveDate\" : null } ]	 }			 }, \"metadata\" : { \"name\" : \"primodato\", \"description\" : \"Descrizione\", \"license\" : \"CC BY 4.0\", \"disclaimer\" : null, \"copyright\" : \"Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.\", \"visibility\" : \"public\", \"registrationDate\" : null, \"requestorName\" : \"nomeRichiedente\", \"requestorSurname\" : \"cognomeRichiedente\", \"dataDomain\" : \"ENVIRONMENT\", \"requestornEmail\" : \"ciao@email.it\", \"fps\" : 22,  \"startIngestionDate\" : null, \"endIngestionDate\" : null, \"importFileType\" : \"CSV\", \"datasetStatus\": \"Ok\",  \"tags\" : [{ \"tagCode\" : \"AIR\" }, { \"tagCode\" : \"INDOOR\" }, { \"tagCode\" : \"POLLUTION\" }, { \"tagCode\" : \"QUALITY\" } ], \"fields\" : [{ \"fieldName\" : \"campo_1\", \"fieldAlias\" : \"semantica campo 1\", \"dataType\" : \"int\", \"sourceColumn\" : \"1\", \"isKey\" : 0, \"measureUnit\" : \"ppm\" }, { \"fieldName\" : \"campo_2\", \"fieldAlias\" : \"semantica campo 2\", \"dataType\" : \"string\", \"sourceColumn\" : \"2\", \"isKey\" : 0, \"measureUnit\" : \"metri\" } ] } }";
		Gson gson = new GsonBuilder().setExclusionStrategies(new GSONExclusionStrategy()).create();
		Metadata d1 = gson.fromJson(json,Metadata.class);
		Metadata d2 = gson.fromJson(json2,Metadata.class);
		d1.setId("nuovoID");
		System.out.println("1 ok" + d1.toJson());
		
		List<Metadata> result = new LinkedList<Metadata>();
		result.add(d1);
		result.add(d2);
		System.out.println("2 ok" + gson.toJson(result));
		
		String json3 = "{\"dataset\":{\"id\":\"5459050d73456b346624b067\",\"configData\":{\"idDataset\":\"quarto\",\"tenant\":\"sandbox\",\"type\":\"dataset\",\"subtype\":\"bulkDataset\",\"datasetversion\":\"1\",\"current\":\"1\"},\"metadata\":{\"name\":\"aaa\",\"registrationDate\":\"Nov 4, 2014 5:55:41 PM\",\"requestorName\":\"aa\",\"requestorSurname\":\"aa\",\"dataDomain\":\"AGRICULTURE\",\"requestornEmail\":\"aa\",\"tags\":[{}],\"visibility\":\"public\",\"license\":\"lll\"}}}";
		Metadata d3 = Metadata.fromJson(json3);
		System.out.println("3ok" + gson.toJson(d3));


	}



}



