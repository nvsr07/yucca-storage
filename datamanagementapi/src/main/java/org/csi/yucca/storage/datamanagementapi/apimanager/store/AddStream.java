package org.csi.yucca.storage.datamanagementapi.apimanager.store;
/**
 * Questo test case è per l'api manager 1.7 modificato
 * per la gestione stream smartdata
 */

import java.io.IOException;

public class AddStream extends CallApiManagerUtil {
	
	public static void beforeClass() throws IOException {
		System.out.println("beforeClass:AddStream");
	}
	
	public AddStream(){
		loadProperties("addStream.properties");
	}
	
	public void run() throws Exception {
		exec();
	}

}
