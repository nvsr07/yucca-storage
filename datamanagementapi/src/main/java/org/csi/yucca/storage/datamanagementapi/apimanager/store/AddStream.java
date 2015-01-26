package org.csi.yucca.storage.datamanagementapi.apimanager.store;
/**
 * Questo test case è per l'api manager 1.7 modificato
 * per la gestione stream smartdata
 */

import java.io.IOException;

public class AddStream extends TestBase {
	
	public static void beforeClass() throws IOException {
		System.out.println("beforeClass:AddStream");
	}
	
	public AddStream(){
		loadTest("conf/properties/apiman-1.6/addStream.properties",getPropertiesVars());
		setjks();
	}
	
	public void run() throws Exception {
		
/*
		setVar("icon","${iconspath}/speed.jpg");
		setVar("apiVersion","1.0");
		setVar("apiName","TTTTTT");
		setVar("P","Prefix-");
*/
		exec();
	}

}
