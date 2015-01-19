package org.csi.yucca.storage.datamanagementapi.apimanager.store;
/**
 * Questo test case è per l'api manager 1.7 modificato
 * per la gestione stream smartdata
 */

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class AddStream extends TestBase {
	
	public static void beforeClass() throws IOException {
		System.out.println("beforeClass:AddStream");
	}
	
	public void test() throws ClientProtocolException, IOException {
		loadTest("conf/properties/addStream.properties",getPropertiesVars());
		setjks();
/*
		setVar("icon","${iconspath}/speed.jpg");
		setVar("apiVersion","1.0");
		setVar("apiName","TTTTTT");
		setVar("P","Prefix-");
*/
		setVar("icon","${iconspath}/smart.png");
		setVar("apiVersion","1.0");
		setVar("apiName","ds_voc_28");
		setVar("P","");
		setVar("endpoint","http://api.smartdatanet.it/odata/SmartDataOdataService.svc/ds_Voc_28");
		setVar("desc","HALADINs at Ist. Fauser");
		setVar("copiright","Copyright (C) 2014, CSP Innovazione nelle ICT");
		
		exec();
	}

}
