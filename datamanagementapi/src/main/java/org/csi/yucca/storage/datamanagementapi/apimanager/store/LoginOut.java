package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class LoginOut extends CallApiManagerUtil {
	
	public static void beforeClass() throws IOException, KeyManagementException, NoSuchAlgorithmException {
		System.out.println("beforeClass:LoginOut");
	}
	
	public static void main(String ...args) throws Exception{
		new LoginOut().test();
	}
	
	public void test() throws Exception {
		
		loadProperties("loginOut.properties");
//		setjks();
		exec();
	}

}
