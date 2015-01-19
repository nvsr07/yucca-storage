package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;

public class LoginOut extends TestBase {
	
	public static void beforeClass() throws IOException, KeyManagementException, NoSuchAlgorithmException {
		System.out.println("beforeClass:LoginOut");
	}
	
	public void test() throws ClientProtocolException, IOException {
		
		loadTest("conf/properties/loginOut.properties",getPropertiesVars());
		setjks();
		exec();
	}

}
