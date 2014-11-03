package org.csi.yucca.storage.datamanagementapi.util;

import org.csi.yucca.storage.datamanagementapi.model.dataset.Dataset;

import com.google.gson.Gson;


public class Util {
	public static String nvl(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String nvlt(Object o) {
		return nvl(o).trim();
	}
	
	public static void main(String[] args) {
		String json = "{ \"configData\" : { \"idDataset\" : \"primoID\", \"tenant\" : \"iotnet\", \"collection\" : \"provaAle\", \"database\": \"db\", \"type\" : \"dataset\", \"subtype\" : \"bulkDataset\", \"entityNameSpace\" : \"it.csi.smartdata.odata.iotnet.iotnetapi002\", \"datasetversion\" : \"1.0.0\", \"current\" : 1, \"archive\" : { \"archiveCollection\" : \"altraCollection\", \"archiveDatabase\": \"altroDB\",  \"archiveInfo\": [ { \"archiveRevision\": 1, \"archiveDate\" : null }, { \"archiveRevision\": 2, \"archiveDate\" : null } ]	 }			 }, \"metadata\" : { \"name\" : \"primodato\", \"description\" : \"Descrizione\", \"license\" : \"CC BY 4.0\", \"disclaimer\" : null, \"copyright\" : \"Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.\", \"visibility\" : \"public\", \"registrationDate\" : null, \"requestorName\" : \"nomeRichiedente\", \"requestorSurname\" : \"cognomeRichiedente\", \"dataDomain\" : \"ENVIRONMENT\", \"requestornEmail\" : \"ciao@email.it\", \"fps\" : 22,  \"startIngestionDate\" : null, \"endIngestionDate\" : null, \"importFileType\" : \"CSV\", \"datasetStatus\": \"Ok\",  \"tags\" : [{ \"tagCode\" : \"AIR\" }, { \"tagCode\" : \"INDOOR\" }, { \"tagCode\" : \"POLLUTION\" }, { \"tagCode\" : \"QUALITY\" } ], \"fields\" : [{ \"fieldName\" : \"campo_1\", \"fieldAlias\" : \"semantica campo 1\", \"dataType\" : \"int\", \"sourceColumn\" : \"1\", \"isKey\" : 0, \"measureUnit\" : \"ppm\" }, { \"fieldName\" : \"campo_2\", \"fieldAlias\" : \"semantica campo 2\", \"dataType\" : \"string\", \"sourceColumn\" : \"2\", \"isKey\" : 0, \"measureUnit\" : \"metri\" } ] } }";
		Gson gson = new Gson();
		Dataset d1 = gson.fromJson(json,Dataset.class);
		d1.setId("nuovoID");
		System.out.println("1 ok" + d1.toJson());
		
	}



}



