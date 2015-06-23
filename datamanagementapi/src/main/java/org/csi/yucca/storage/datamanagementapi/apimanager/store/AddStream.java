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

	public AddStream() {
	}
	
	public void setProperties(Boolean update){
		if (update)
			loadProperties("updateStream.properties");
		else
			loadProperties("addStream.properties");
	}

	public void run() throws Exception {
		//System.setProperty("javax.net.ssl.trustStore", "/usr/local/jboss-eap-6.0/standalone/configuration/ca-sdp.jks");
		//System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
		//System.setProperty("javax.net.ssl.trustStoreType", "JKS");
		//System.setProperty("javax.net.debug", "ssl");
		exec();
	}

}
