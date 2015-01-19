package org.csi.yucca.storage.datamanagementapi.apimanager.store;


import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.BeforeClass;
import org.junit.Test;

public class PublishApi extends TestBase {
	
	public static void beforeClass() throws IOException {
		System.out.println("beforeClass:PublishApi");
	}
	
	public void test() throws ClientProtocolException, IOException {
		loadTest("properties/base/publishApi.properties",getPropertiesVars());
		setjks();
		exec();
	}

}
