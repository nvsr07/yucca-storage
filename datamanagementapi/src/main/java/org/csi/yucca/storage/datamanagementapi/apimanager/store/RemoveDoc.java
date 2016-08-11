package org.csi.yucca.storage.datamanagementapi.apimanager.store;


import java.io.IOException;

public class RemoveDoc extends CallApiManagerUtil {
	
	public static void beforeClass() throws IOException {
		System.out.println("beforeClass:RemoveDoc");
	}
	
	public RemoveDoc(){
		loadProperties("removeDoc.properties");
	}
	
	public void run() throws Exception {
		exec();
	}
}

