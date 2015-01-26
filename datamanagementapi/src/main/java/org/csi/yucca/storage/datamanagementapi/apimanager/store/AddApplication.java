package org.csi.yucca.storage.datamanagementapi.apimanager.store;


public class AddApplication extends TestBase {
	
	public AddApplication(){
		loadTest("conf/properties/base/addApplication.properties",getPropertiesVars());
		setjks();
	}
	
	public void run() throws Exception {
		exec();
	}
}
