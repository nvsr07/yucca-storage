package org.csi.yucca.storage.datamanagementapi.apimanager.store;


public class AddApplication extends TestBase {
	
	
	public static void main(String args[]) throws Exception{
		AddApplication app = new AddApplication();
		app.setVar("appName", "Pippo");
		
		app.run();
	}
	
	public AddApplication(){
		loadTest("conf/properties/base/addApplication.properties",getPropertiesVars());
//		setjks();
	}
	
	public void run() throws Exception {
		exec();
	}
}
