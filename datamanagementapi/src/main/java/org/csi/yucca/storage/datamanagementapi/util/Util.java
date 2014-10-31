package org.csi.yucca.storage.datamanagementapi.util;

import org.csi.yucca.storage.datamanagementapi.model.Dataset;

import com.google.gson.Gson;

public class Util {
	public static String nvl(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String nvlt(Object o) {
		return nvl(o).trim();
	}
	
	public static void main(String[] args) {
		String json = "{ \"configData\":{ \"idDataset\":\"identificativo\", \"tenant\":\"smart\", \"type\":\"dataset\", \"subtype\":\"bulkDataset\", \"entityNameSpace\":\"it.csi.smartdata.odata.iotnet.iotnetapi002\", \"datasetversion\":\"ff\", \"current\":1, \"trash\":{ \"trashCollection\":\"aaaaa\", \"trashHost\":\"\", \"trashPort\":\"\", \"trashDatabase\":\"\", \"trashInfo\":[ { \"trashRevision\":1, \"trashDate\":null }, { \"trashRevision\":2, \"trashDate\":null } ] } }, \"dataset\":{ \"name\":\"primo ale\", \"licence\":\"CC BY 4.0\", \"disclaimer\":null, \"copyright\":\"Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.\", \"visibility\":\"public\", \"registrationDate\":null, \"requestorName\":\"cicciopasticcio\", \"requestorSurname\":\"pasticcio\", \"dataDomain\":\"ENVIRONMENT\", \"requestornEmail\":\"aaa@bbb.it\", \"fps\":22, \"startIngestionDate\":null, \"endIngestionDate\":null, \"importFileType\":\"\", \"datasetStatus\":\"\", \"tags\":[ { \"tagCode\":\"AIR\" }, { \"tagCode\":\"INDOOR\" }, { \"tagCode\":\"POLLUTION\" }, { \"tagCode\":\"QUALITY\" } ], \"fields\":[ { \"fieldName\":\"campo_1\", \"fieldAlias\":\"semantica campo 1\", \"dataType\":\"int\", \"sourceColumn\":\"\", \"isKey\":0, \"measureUnit\":\"ppm\" }, { \"fieldName\":\"campo_2\", \"fieldAlias\":\"semantica campo 2\", \"dataType\":\"string\", \"sourceColumn\":\"\", \"isKey\":0, \"measureUnit\":\"metri\" } ] } }";
		Gson gson = new Gson();
		Dataset d1 = gson.fromJson(json,Dataset.class);
		System.out.println("1 ok" + d1.toJson());
	}
}
