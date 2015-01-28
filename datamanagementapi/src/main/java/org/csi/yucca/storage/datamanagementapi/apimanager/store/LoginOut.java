package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class LoginOut extends TestBase {
	
	public static void beforeClass() throws IOException, KeyManagementException, NoSuchAlgorithmException {
		System.out.println("beforeClass:LoginOut");
	}
	
	public void test() throws Exception {
		
		loadTest("conf/properties/loginOut.properties",getPropertiesVars());
//		setjks();
		exec();
	}

}
