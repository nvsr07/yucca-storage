package org.csi.yucca.storage.datamanagementapi.apimanager.store;


public class AddSubscription extends TestBase {
	
	public AddSubscription(){
		loadTest("conf/properties/base/addSubscription.properties",getPropertiesVars());
		setjks();
	}
	public void run() throws Exception {
		exec();
	}
}
