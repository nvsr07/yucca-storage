package org.csi.yucca.storage.datamanagementapi.apimanager.store;


import java.io.IOException;

public class PublishApi extends CallApiManagerUtil {
	
	public static void beforeClass() throws IOException {
		System.out.println("beforeClass:PublishApi");
	}
	
	public PublishApi(){
		loadProperties("publishApi.properties");
	}
	
	public void run() throws Exception {
		exec();
	}
}

