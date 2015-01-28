package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class QSPStore extends TestBase implements CallBack {

	static String confVars;
	static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd/M/yyyy");
	
//	public static void main(String ...args) throws Exception{
//		QSPStore subscription = new QSPStore();
//		subscription.setVar("apiVersion","9");
//		subscription.setVar("appName","userportal_csp");
//		subscription.setVar("apiName","ds_StoryPoints_17_odata");
//		subscription.setVar("P","");
//		
//		subscription.run();
//	}
	public QSPStore(){
		confVars = getPropertiesVars();
		loadTest("conf/properties/apiman-1.6/QSPstore.properties",confVars);
//		setjks();
	}
		
	@Test
	public void run() throws Exception {
		exec();
	}

	@Override
	public void handler(TestBase test, String action) {
		JsonMap jm = new JsonMap();
	    String res =  test.getVar("result");
	    Map<String,Object> m = jm.decode(res);
	    test.out("jsonMap " + m);
	    if ("getId".equals(action)) {
	    	String appName = test.getVar("appName");
		    List<?> app = (List<?>) m.get("applications");
		    for (Object o : app) {
		    	@SuppressWarnings("unchecked")
				Map<String,Object> mm = (Map<String,Object>) o;
		    	String name = (String) mm.get("name");
		    	Integer id = (Integer) mm.get("id");
		    	if (name.equals(appName))
		    	  test.setVar("id",id.toString());		
		    }
		    String id = test.getVar("id");
		    System.out.println("ID : "+id);
	
	    } 
//		    else if ("getToken".equals(action)) {
//	    	Map<?,?> md = (Map<?,?>) m.get("data");
//	    	Map<?,?> mk = (Map<?,?>) md.get("key");
//	    	String token = (String) mk.get("accessToken");
//	    	String consumerKey = (String) mk.get("consumerKey");
//	    	String consumerSecret = (String) mk.get("consumerSecret");
//	    	test.out("result: " + res);
////	    	List<List<String>> fields = getVals(res,"accessToken");
//	    	test.setVar("token", token);
//	    	test.setVar("consumerKey",consumerKey);
//	    	test.setVar("consumerSecret",consumerSecret);
//	    	test.out("getToken.token...........: " + token);
//	    	test.out("getToken.consumerKey.....: " + consumerKey);
//	    	test.out("getToken.consumerSecret..: " + consumerSecret);
//	    	
//			try {
//				File theDir = new File("vars");
//
//				  // if the directory does not exist, create it
//				if (!theDir.exists()) {
//				    out("creating directory: vars");
//				    theDir.mkdir();
//				}
//				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("vars/token.properties", true)));
//				
//				writer.println("#\n# written by apimanager.test\n# confVar " + 
//                               confVars + "\n# on " + sdf.format(new Date()) + "\n#");
//		    	writer.println("consumerKey=" + consumerKey);
//		    	writer.println("consumerSecret="+ consumerSecret);
//		    	writer.println("token="+ token);
//		    	writer.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	    }
	}

}
