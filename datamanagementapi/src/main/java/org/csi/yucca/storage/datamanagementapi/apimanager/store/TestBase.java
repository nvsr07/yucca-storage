package org.csi.yucca.storage.datamanagementapi.apimanager.store;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.junit.Assert;

public class TestBase {
	private boolean addTimestamp = true;
	
	private VarProperties testCase;
	private DefaultHttpClient client;
	{
        try {

        	if (System.getProperty("forceSSL") != null) {
        		System.out.println("*********************************************");
        		System.out.println("***** SSL SOCKET OPENED IN FORCED MODE ******");
        		System.out.println("*********************************************");
	            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	            trustStore.load(null, null);
	
	            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
	            HttpParams params = new BasicHttpParams();
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	
	            SchemeRegistry registry = new SchemeRegistry();
	            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	            registry.register(new Scheme("https", sf, 443));
	
	            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	
	            client = new DefaultHttpClient(ccm, params);
        	} else
        		client = new DefaultHttpClient();
        } catch (Exception e) {
        	e.printStackTrace();
            client = new DefaultHttpClient();
        }       
     }
	
	public static String getPropertiesVars() {
		String vars = System.getProperty("vars");
		System.out.println("vars via System property : " + vars);
		try {
//			System.out.println(new File(".").getAbsolutePath());
			String path = "vars/vars.ini";
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println("line via vars/vars.ini : " + line);
				int p = line.indexOf("#");
				if (p == 0)
					line = "";
				else if (p > 0)
					line = line.substring(0,p-1);
				line = line.trim();
				if (line.length() > 0) {					
					vars = line;
				}
			}
			if (br != null) br.close(); 
		} catch (Exception e) {
		} finally {
			System.out.println("vars via vars/vars.ini : " + vars);		
		}
		if (vars == null) {
			vars = "/vars.properties";
			System.out.println("vars via default : " + vars);
		}
		return vars;
	}

	public void loadTest(String res,String... conf) {
//		System.out.println("loadTest " + res + " " + conf);
		try {
			testCase = new VarProperties(res,conf);
		} catch (Exception e) {
			out("Exception " + e + " in loadTest with parameters: res=" + res + ", conf=" + conf);
			e.printStackTrace();
		}
		Assert.assertNotNull(testCase);
	}
	int nop;
	String op = "<init>";
	
	public String getVar(String key) {
		return testCase.getVar(key);	
	}
/*
	public String getProperty(String key) {
		  return testCase.getProperty(key);	
	}
*/	
	public String setVar(String key,String val) {
		String prev = (String) testCase.setVar(key, val);
		return prev;
	}
/*
	public String setProperty(String key,String val) {
		String prev = (String) testCase.setProperty(key, val);
		return prev;
	}
*/
	public void setOp(String op) {
		nop++;
		this.op = op;
		System.out.println("----------------------------------------------");
	}
	
	public void setOp(String op,int step) {
		nop = step;
		this.op = op;
//		System.out.println("-------------------------- next > " + nop + " " + op);
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	long start;
	long last;
	long lasto;
	String thId = "";
	
	public void setThId(String id) {
		thId = id;
	}
	
	public void out(Object s) {
		if (op == null) op = "";
		if (addTimestamp) {
			long now = System.currentTimeMillis();
			long d = 0;
			if (start != 0) d = now - last;
			if (start == 0 || now != lasto) {
				String t = sdf.format(new Date());
				System.out.printf("%s %4d %s[%4d] - %-12s: %s%n",t,d,thId,nop,op,s);
				lasto = now;
			} else
				System.out.printf("             %4d %s[%4d] - %-12s: %s%n",d,thId,nop,op,s);
			last = now;
			if (start == 0) start = now;
		} else
			System.out.printf("[%4d] - %-12s: %s%n",nop,op,s);
	}
	
	public void outf(String f,Object...p) {
		out(String.format(f, p));
	}
		
	enum Type {Continue,Break};
	
	private Type dispatch(String key,String val) {
//		System.out.println("dispatch key " + key + " val " + val);
		if (val == null) return Type.Break;
		else if ("close".equals(val)) return Type.Break;
		else if ("chain".equals(val)) {
			execChain(key,val);
			return Type.Continue;
		} else if ("skip".equals(val)) {
			return Type.Continue;			
		} else if ("class".equals(val)) {
			execClass(key,val);
			return Type.Continue;							
		} else if ("SOAP".equals(val)) {
			execSOAP(key,val);
			return Type.Continue;							
		} else if ("SOAPSES".equals(val)) {
			execSOAPSes(key,val);
			return Type.Continue;							
		} else {
			execHttp(key,val);
			return Type.Continue;			
		}
	}

	private void execSOAP(String key,String val) {
		String values = testCase.getProperty(key + ".params.values");
        try {
			execSoap(values);
		} catch (Exception e) {
			out("unexpected exception " + e);
			Assert.fail(e.getMessage());
		}		
	}
	
	private void execSOAPSes(String key,String val) {
		String values = testCase.getProperty(key + ".params.values");
        try {
			execSoapSes(values,values);
		} catch (Exception e) {
			out("unexpected exception " + e);
			Assert.fail(e.getMessage());
		}		
	}

	private void execHttp(String key,String val) {
		String names = testCase.getProperty(key + ".params.names");
		String values = testCase.getProperty(key + ".params.values");
		String outVar = testCase.getProperty(key + ".out");
		String httpExpect = testCase.getProperty(key + ".http.status.expect");
		if (httpExpect == null)
			httpExpect = testCase.getVar("httpok");
		String respExpect = testCase.getProperty(key + ".response.expect");
		if (respExpect == null)
			respExpect = testCase.getVar("ok");
		out("exec: " + key + " -> " + val + 
			" names: "+ names + " values " + values + " httpExpect " + httpExpect + 
			" respExpect " + respExpect);
		String resp;
		try {
			resp = exec(step,names,values,httpExpect);
			out(resp);
//			out("expect " + respExpect);
            int p = resp.indexOf(respExpect);
            if (p < 0)
            	out("response don't match with expected value: " + respExpect);
			Assert.assertTrue(p >= 0);
			if (outVar != null)
				testCase.setVar(outVar, resp);
		} catch (Exception e) {
			e.printStackTrace();
			out("unexpected exception " + e);
			Assert.fail(e.getMessage());
		}		
	}

	private void execClass(String key,String val) {
		String clazz = testCase.getProperty(key + ".class");
		String action = testCase.getProperty(key + ".action");
		out("exec: class " + clazz + " action " + action);
		CallBack call = null;
		try {
			if ("this".equals(clazz)) {
				call = (CallBack) this;
			} else {
				Class<?> c = Class.forName(clazz);
				call = (CallBack) c.newInstance();
			}
		} catch (Exception e) {
			out("unexpected exception " + e);
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(call);
		call.handler(this, action);	
	}

	private void execChain(String key,String val) {
		String where = testCase.getProperty(key + ".params.start");
		String check = testCase.getProperty(key + ".params.check");
		boolean exec = true;
		if (check != null && check.length() > 0) {
			out("exec: chain check " + check);
			String v = testCase.getVar(check);
			out("exec: chain check " + check + " value " + v);
			if (v == null || v.length() == 0 || "0".equals(v))
				exec = false;
			out("exec: chain exec " + exec);
		}
		if (where != null && exec) {
			int pos = where.lastIndexOf('.');
			if (pos >= 0) {
				oper = where.substring(0, pos + 1);
				step = Integer.parseInt(where.substring(pos + 1));
				out("exec: chain oper " + oper + " idx " + step);
				step--;
			}
		}
	}
	
	int step;
	String oper = "operation.";
	
	public void exec() throws ClientProtocolException, IOException {
//		String oper = "operation.";
		for (step = 1; ; step++) {
			String key = oper + step;
			String val = testCase.getProperty(key);
			setOp(val,step);
			Type t = dispatch(key,val);
			if (t.equals(Type.Break)) break;
		}
		out("... all done");
	}

	public void execSoap(String prefix) throws ClientProtocolException, IOException {
		execSoap(prefix,prefix);
	}

	public void execSoap(String prefix,String logName) throws ClientProtocolException, IOException {
		HttpPost httppost = null;
		
		if (logName == null) logName = prefix;
		
		String add = testCase.getProperty(prefix + ".address");
		out("address " + add);
		httppost = new HttpPost(add);
        String soapAction = testCase.getProperty(prefix + ".SOAPAction");
	    httppost.setHeader("SOAPAction",soapAction);
	    out("SOAPAction " + soapAction );
	    httppost.setHeader("Content-Type","text/xml;charset=UTF-8");
	    String auth = testCase.getProperty(prefix + ".Authorization");
	    httppost.setHeader("Authorization",auth);
	    out("Authorization " + auth);
//	    Date now = new Date();
	    httppost.setHeader("X-Authorization","TestAuth");
	    out("X-Authorization " + "TestAuth");
	    
        String body = testCase.getProperty(prefix + ".body");
	    out("body " + body);
        StringEntity stringentity = new StringEntity(body,"UTF-8");
        stringentity.setChunked(true);
        httppost.setEntity(stringentity);
		String httpExpect = testCase.getProperty(prefix + ".http.status.expect");

		long start = System.currentTimeMillis();
		HttpResponse r = client.execute(httppost);
		long now = System.currentTimeMillis();
		System.out.println("=================== - Response Delay " + (now - start));
		String status = r.getStatusLine().toString();
		out("status " + status + " expect " + httpExpect);
		if (httpExpect != null)
			Assert.assertTrue(status.equals(httpExpect));
		for (Header h : r.getAllHeaders()) {
			out ("header " + h.getName() + ":" + h.getValue());
		}
			
		StringBuilder out = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			out.append(line);
		}	
		out("stream " + out);
		String respExpect = testCase.getProperty(prefix + ".response.expect");
		if (respExpect == null)
			respExpect = testCase.getVar("ok");

		if (httpExpect == null)
			httpExpect = testCase.getVar("httpok");

		out("respExpect: " + respExpect);
		if (respExpect != null && respExpect.trim().length() > 0) {
			boolean ok = out.indexOf(respExpect) >= 0;
			if (ok) out("check OK");
			else    out("check FAIL");
			Assert.assertTrue(out.indexOf(respExpect) >= 0);
		}
	}

	private String cookie;
	public void execSoapSes(String prefix,String logName) throws ClientProtocolException, IOException {
		HttpPost httppost = null;
			
		if (logName == null) logName = prefix;
		
		String add = testCase.getProperty(prefix + ".address");
		out("address " + add);
	    HttpHost proxy = new HttpHost("proxy.csi.it", 3128, "http");

		httppost = new HttpPost(add);
		
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
	            CookiePolicy.BROWSER_COMPATIBILITY);

        String soapAction = testCase.getProperty(prefix + ".SOAPAction");
	    httppost.setHeader("SOAPAction",soapAction);
	    out("SOAPAction " + soapAction );
	    httppost.setHeader("Content-Type","text/xml;");
	/*
	    String auth = testCase.getProperty(prefix + ".Authorization");
	    httppost.setHeader("Authorization",auth);
	    out("Authorization " + auth);
	    Date now = new Date();
	    if (cookie != null) {
		    httppost.setHeader("Cookie",cookie);
		    out("Cookie " + cookie);
	    }
	 */
/*
    	httppost.setHeader("MyHeader",now.toString());
	    out("MyHeader " + now.toString());
*/	    
        String body = testCase.getProperty(prefix + ".body");
	    out("body " + body);
        StringEntity stringentity = new StringEntity(body,"UTF-8");
        stringentity.setChunked(true);
        httppost.setEntity(stringentity);
		String httpExpect = testCase.getProperty(prefix + ".http.status.expect");
		if (httpExpect == null)
			httpExpect = testCase.getVar("httpok");
		System.out.println("=====================: Response");
		HttpResponse r = client.execute(httppost);
		String status = r.getStatusLine().toString();
		out("status " + status + " expected " + httpExpect);
		Header hs[] = r.getAllHeaders();
		if (hs != null && hs.length > 0)
			for (Header h : hs)
				out("Header " + h.getName() + " " + h.getValue());
		String arr[] = httpExpect.trim().split("\\|");
		boolean ok = false;
		for (String var : arr) {
			if (status.equals(var)) {
				ok = true;
				break;
			}
		}
		
		Assert.assertTrue(ok);
		for (Header h : r.getAllHeaders()) {
			out ("header " + h.getName() + " val " + h.getValue());
			if (h.getName().equals("Set-Cookie")) {
				String v[] = h.getValue().split(";");
//				cookie = v[0];
				
			}
		}
			
		StringBuilder out = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			out.append(line);
		}	
		out("stream " + out);
		String respExpect = testCase.getProperty(prefix + ".response.expect");
		if (respExpect == null)
			respExpect = testCase.getVar("ok");

		out("respExpect: " + respExpect);
		if (respExpect != null && respExpect.trim().length() > 0) {
			arr = respExpect.trim().split("\\|");
			ok = false;
			for (String var : arr) {
				ok = out.indexOf(var) >= 0;
				if (ok) {
					out("check OK");
					break;
				}
			}
			Assert.assertTrue(ok);
		}
		httppost.releaseConnection();
	}

	public void execSoapMulti(String prefix,String logName) throws ClientProtocolException, IOException {
		HttpPost httppost = null;
		
		if (logName == null) logName = prefix;
		setOp(logName);
		
		String add = testCase.getProperty(prefix + ".address");
		out("address " + add);
		httppost = new HttpPost(add);
//		httppost = new HttpPost("https://tst-sw-eng.csi.it/wso007/services/EipMockServiceAxis");
        String soapAction = testCase.getProperty(prefix + ".SOAPAction");
	    httppost.setHeader("SOAPAction",soapAction);
	    out("SOAPAction " + soapAction );
//	    httppost.setHeader("Content-Type","multipart/related; boundary=\"--pippo\"; type=\"application/xop+xml\"; start=\"<0.d15bbef6dea7ab97bc46030879cad6bb6b534fdb0a456bf0@apache.org>\"; start-info=\"text/xml\"");
//	    httppost.setHeader("Content-Type","multipart/related; boundary=\"--pippo\"; type=\"application/xop+xml\"; start-info=\"text/xml\"");

	    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.STRICT, null, Charset.forName("UTF-8"));
	    
	    String auth = testCase.getProperty(prefix + ".Authorization");
	    httppost.setHeader("Authorization",auth);
	    out("Authorization " + auth);
	    
//	    HttpParams params = client.getParams();
	    
	    String body = testCase.getProperty(prefix + ".body");
//		reqEntity.addPart("<root.message@cxf.apache.org>", new StringBody(body));
		
//		ByteArrayBody bab = new ByteArrayBody( data, "image/png", "byte_array_image" );
		FormBodyPart fbp = new FormBodyPart( "aaa", new StringBody(body,"application/xop+xml",Charset.forName("UTF-8")) );

		fbp.addField( "Content-Id", "<root.message@cxf.apache.org>" );
//		fbp.addField( "Content-Type","application/xop+xml; charset=UTF-8; type=\"text/xml\";");
	
		reqEntity.addPart( fbp );

//	    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.STRICT, "--pippo", null);
	    
//	    reqEntity.addPart("par", new StringBody(body));
	 
	    FileBody bin = new FileBody(new File("conf/icons/topopiem.PNG"));
		fbp = new FormBodyPart( "bbb", bin );
		fbp.addField( "Content-Id", "<53877a90-2519-43ee-bd7e-55f87fb1e707-1@cxf.apache.org>" );

	    reqEntity.addPart(fbp);

	    httppost.setEntity(reqEntity);

	    out("executing request " + httppost.getRequestLine());
	 
	    /*
        String body="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " + 
	                " xmlns:moc=\"http://mockservice.wso2eip.csi.it\"> <soapenv:Header/> " + 
        		    " <soapenv:Body> <moc:greatings> <moc:name>valter</moc:name> " + 
	                "</moc:greatings> </soapenv:Body> </soapenv:Envelope>";
	     */
 //       String body = testCase.getProperty(prefix + ".body");
 //       StringEntity stringentity = new StringEntity(body,"UTF-8");
 //       stringentity.setChunked(true);
 //       httppost.setEntity(stringentity);
		String httpExpect = testCase.getProperty(prefix + ".http.status.expect");
		if (httpExpect == null)
			httpExpect = testCase.getVar("httpok");

		HttpResponse r = client.execute(httppost);
		String status = r.getStatusLine().toString();
		out("status " + status);
		Assert.assertTrue(status.equals(httpExpect));

		StringBuilder out = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			out.append(line);
		}	
		out("stream " + out);
		String respExpect = testCase.getProperty(prefix + ".response.expect");
		if (respExpect == null)
			respExpect = testCase.getVar("ok");

		out("respExpect: " + respExpect);
		Assert.assertTrue(out.indexOf(respExpect) >= 0);
	}

	public String execPost(int n, String names, String values,String httpExpect)
	           throws ClientProtocolException, IOException {
		String add = testCase.getProperty(values + "address");
		out("POST " + values + "address -> " + add);
		HttpPost httppost =  new HttpPost(add);
		
		if (add != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			out("key                  param                          value");
			out("-------------------- ------------------------------ -----------");
			for (int i = 0; ;i++) {
				String k = names + i;
//				System.out.println("reading " + k);
				String key = testCase.getProperty(k);
				if (key == null)
					break;
				String val = testCase.getProperty(values + key);
				String outV = val;
				if (outV == null) outV = "";
				else if (key.toLowerCase().equals("password"))
					outV = "******";
				outf("%-20s %-30s %s",k,key,outV);
				nameValuePairs.add(new BasicNameValuePair(key, val));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			String headerKey = testCase.getProperty(values + "header.key");
			String headerVal = testCase.getProperty(values + "header.val");
			if (headerKey != null) {
				httppost.setHeader(headerKey,headerVal);
				outf("Header %-30s %s",headerKey,headerVal);
			}
			headerKey = testCase.getProperty(values + "header.key.1");
			headerVal = testCase.getProperty(values + "header.val.1");
			if (headerKey != null) {
				httppost.setHeader(headerKey,headerVal);
				outf("Header %-30s %s",headerKey,headerVal);
			}
			HttpResponse r = client.execute(httppost);
			String status = r.getStatusLine().toString();
			out("status " + status);
			Assert.assertTrue(status.equals(httpExpect));

			StringBuilder out = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				out.append(line);
			}	
			return out.toString();
		}
		return "";
	}

	public String execPostMulti(int n, String names, String values,String httpExpect)
	           throws ClientProtocolException, IOException {
		String add = testCase.getProperty(values + "address");
		out(values + "address -> " + add);
		HttpPost httppost =  new HttpPost(add);
	    MultipartEntity reqEntity = new MultipartEntity(); // HttpMultipartMode.STRICT, "--pippo", null);
		
		if (add != null) {
			out("key                  param                          value");
			out("-------------------- ------------------------------ -----------");
			for (int i = 0; ; i++) {
				String k = names + i;
//				System.out.println("reading " + k);
				String key = testCase.getProperty(k);
				if (key == null)
					break;
				if (key.startsWith("FILE.")) {
					String val = testCase.getProperty(values + key + ".name");
					outf("%-20s %-30s %s",k,key + ".name",val);
					if (val != null) {
				      FileBody f = new FileBody(new File(val));
						reqEntity.addPart(key.substring("FILE.".length()), f);
					}
				} else {
					String val = testCase.getProperty(values + key);
					String outV = val;
					if (outV == null) 
						outV = "";
					else if (key.toLowerCase().equals("password"))
						outV = "******";
					outf("%-20s %-30s %s",k,key,outV);
					reqEntity.addPart(key, new StringBody(val));
				}
			}

		    httppost.setEntity(reqEntity);
			HttpResponse r = client.execute(httppost);
			String status = r.getStatusLine().toString();
			out("status " + status);
			Assert.assertTrue(status.equals(httpExpect));

			StringBuilder out = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				out.append(line);
			}	
			return out.toString();
		}
		return "";
	}

	public String execGet(int n, String names, String values,String httpExpect)
	           throws ClientProtocolException, IOException {
		String add = testCase.getProperty(values + "address");
		out(values + "address -> " + add);
		HttpGet  httpget = new HttpGet(add);
		
		if (add != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			out("key                  param                          value");
			out("-------------------- ------------------------------ -----------");
			for (int i = 0; ;i++) {
				String k = names + i;
//				System.out.println("reading " + k);
				String key = testCase.getProperty(k);
				if (key == null)
					break;
				String val = testCase.getProperty(values + key);
				String outV = val;
				if (outV == null) outV = "";
				else if (key.toLowerCase().equals("password"))
					outV = "******";
				outf("%-20s %-30s %s",k,key,outV);
				nameValuePairs.add(new BasicNameValuePair(key, val));
			}
			HttpParams params = new BasicHttpParams();
			for (NameValuePair p : nameValuePairs) {
				params.setParameter(p.getName(), p.getValue());
			}
			httpget.setParams(params);
			HttpResponse r = client.execute(httpget);
			String status = r.getStatusLine().toString();
			out("status " + status);
			Assert.assertTrue(status.equals(httpExpect));

			StringBuilder out = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				out.append(line);
			}	
			return out.toString();
		}
		return "";
	
	}

	public String exec(int n, String names, String values,String httpExpect)
			           throws ClientProtocolException, IOException {

		String meth = testCase.getProperty(values + "method");
		if (meth == null || "POST".equals(meth.toUpperCase()))
			return execPost(n,names,values,httpExpect);
		else if ("POSTMULTI".equals(meth.toUpperCase()))
			return execPostMulti(n, names, values, httpExpect);
		else
			return execGet(n,names,values,httpExpect);

	}
	
    public static void setjks(String trustStore,boolean force) {
    	if (force) {
    		System.out.println("*********************************************");
    		System.out.println("***** SSL SOCKET OPENED IN FORCED MODE ******");
    		System.out.println("*********************************************");

    		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
    			public X509Certificate[] getAcceptedIssuers() {
    				System.out.println("in getAcceptedIssuers");
    				return null;
    			}

    			public void checkClientTrusted(X509Certificate[] certs, String authType) {
    				System.out.println("in checkClientTrusted");
    			}

    			public void checkServerTrusted(X509Certificate[] certs, String authType) {
    				System.out.println("in checkServerTrusted");
  /*
    				for (X509Certificate c : certs){
    					System.out.println("certs " + c);
    				}
    				System.out.println("authType" + authType);
    */
    			}
		    	} 
    		};
		    // Install the all-trusting trust manager
		    SSLContext sc = null;
		    try {
		    	sc = SSLContext.getInstance("SSL");
		    	sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    // Create all-trusting host name verifier
		    HostnameVerifier allHostsValid = new HostnameVerifier() {
		    	public boolean verify(String hostname, SSLSession session) {
		    		System.out.println("verify " + hostname + "  " + session);
		    		return true;
		    	}
		    };
    	} else {
    		System.out.println("set trustStore to " + trustStore);
    		System.setProperty("javax.net.ssl.trustStore", trustStore);
    		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    		System.setProperty("javax.net.ssl.trustStoreType","JKS");
    	}
//	    System.setProperty("javax.net.debug","ssl");
	}

    static boolean force = false;
    
	public void setjks() {
	
    	if (force) {
    		System.out.println("*********************************************");
    		System.out.println("***** SSL SOCKET OPENED IN FORCED MODE ******");
    		System.out.println("*********************************************");

    		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
    			public X509Certificate[] getAcceptedIssuers() {
    				System.out.println("in getAcceptedIssuers");
    				return null;
    			}

    			public void checkClientTrusted(X509Certificate[] certs, String authType) {
    				System.out.println("in checkClientTrusted");
    			}

    			public void checkServerTrusted(X509Certificate[] certs, String authType) {
    				System.out.println("in checkServerTrusted");
  /*
    				for (X509Certificate c : certs){
    					System.out.println("certs " + c);
    				}
    				System.out.println("authType" + authType);
    */
    			}
		    	} 
    		};
		    // Install the all-trusting trust manager
		    SSLContext sc = null;
		    try {
		    	sc = SSLContext.getInstance("SSL");
		    	sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    // Create all-trusting host name verifier
		    HostnameVerifier allHostsValid = new HostnameVerifier() {
		    	public boolean verify(String hostname, SSLSession session) {
		    		System.out.println("verify " + hostname + "  " + session);
		    		return true;
		    	}
		    };
    	} else {    		
    		System.out.println("trustStore " + getVar("trustStore"));
    		System.setProperty("javax.net.ssl.trustStore", getVar("trustStore"));
    		System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    		System.setProperty("javax.net.ssl.trustStoreType","JKS");
  		
    	}	    
//	    System.setProperty("javax.net.debug","ssl");
	}

}
