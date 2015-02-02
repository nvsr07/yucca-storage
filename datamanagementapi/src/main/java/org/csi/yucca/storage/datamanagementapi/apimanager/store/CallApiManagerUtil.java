package org.csi.yucca.storage.datamanagementapi.apimanager.store;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;

public class CallApiManagerUtil {
	private boolean addTimestamp = true;
	
	private VarProperties properties;
	private  HttpClient  client = HttpClientBuilder.create().build();
	

	public void loadProperties(String res) {
//		System.out.println("loadTest " + res + " " + conf);
		try {
			properties = new VarProperties(res);
		} catch (Exception e) {
			out("Exception " + e + " in loadTest with parameters: res=" + res );
			e.printStackTrace();
		}
		Assert.assertNotNull(properties);
	}
	int nop;
	String op = "<init>";
	
	public String getVar(String key) {
		return properties.getVar(key);	
	}
	public String setVar(String key,String val) {
		String prev = (String) properties.setVar(key, val);
		return prev;
	}
	public void setOp(String op) {
		nop++;
		this.op = op;
		System.out.println("----------------------------------------------");
	}
	public void setOp(String op,int step) {
		nop = step;
		this.op = op;
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
	
	private Type dispatch(String key,String val) throws Exception {
		System.out.println("dispatch key " + key + " val " + val);
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
		} else {
			execHttp(key,val);
			return Type.Continue;			
		}
	}

	private void execHttp(String key,String val) throws Exception {
		String names = properties.getProperty(key + ".params.names");
		String values = properties.getProperty(key + ".params.values");
		String outVar = properties.getProperty(key + ".out");
		String httpExpect = properties.getProperty(key + ".http.status.expect");
		if (httpExpect == null)
			httpExpect = properties.getVar("httpok");
		String respExpect = properties.getProperty(key + ".response.expect");
		if (respExpect == null)
			respExpect = properties.getVar("ok");
		out("exec: " + key + " -> " + val + 
			" names: "+ names + " values " + values + " httpExpect " + httpExpect + 
			" respExpect " + respExpect);
		String resp;
			resp = exec(step,names,values,httpExpect);
			out(resp);
//			out("expect " + respExpect);
            int p = resp.indexOf(respExpect);
            if (p < 0){
            	out("response don't match with expected value: " + respExpect);
            	throw new Exception(resp);
            }
			Assert.assertTrue(p >= 0);
			if (outVar != null)
				properties.setVar(outVar, resp);
	}

	private void execClass(String key,String val) {
		String clazz = properties.getProperty(key + ".class");
		String action = properties.getProperty(key + ".action");
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
		String where = properties.getProperty(key + ".params.start");
		String check = properties.getProperty(key + ".params.check");
		boolean exec = true;
		if (check != null && check.length() > 0) {
			out("exec: chain check " + check);
			String v = properties.getVar(check);
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
	
	public void exec() throws Exception {
//		String oper = "operation.";
		for (step = 1; ; step++) {
			String key = oper + step;
			String val = properties.getProperty(key);
			setOp(val,step);
			Type t = dispatch(key,val);
			if (t.equals(Type.Break)) break;
		}
		out("... all done");
	}

	public String execPost(int n, String names, String values,String httpExpect)
	           throws ClientProtocolException, IOException {
		String add = properties.getProperty(values + "address");
		out("POST " + values + "address -> " + add);
		HttpPost httppost =  new HttpPost(add);
		
		if (add != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			out("key                  param                          value");
			out("-------------------- ------------------------------ -----------");
			for (int i = 0; ;i++) {
				String k = names + i;
//				System.out.println("reading " + k);
				String key = properties.getProperty(k);
				if (key == null)
					break;
				String val = properties.getProperty(values + key);
				String outV = val;
				if (outV == null) outV = "";
				else if (key.toLowerCase().equals("password"))
					outV = "******";
				outf("%-20s %-30s %s",k,key,outV);
				nameValuePairs.add(new BasicNameValuePair(key, val));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			String headerKey = properties.getProperty(values + "header.key");
			String headerVal = properties.getProperty(values + "header.val");
			if (headerKey != null) {
				httppost.setHeader(headerKey,headerVal);
				outf("Header %-30s %s",headerKey,headerVal);
			}
			headerKey = properties.getProperty(values + "header.key.1");
			headerVal = properties.getProperty(values + "header.val.1");
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
		String add = properties.getProperty(values + "address");
		out(values + "address -> " + add);
		HttpPost httppost =  new HttpPost(add);
		
	    MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	    reqEntity.setMode(HttpMultipartMode.STRICT);
		
		if (add != null) {
			out("key                  param                          value");
			out("-------------------- ------------------------------ -----------");
			for (int i = 0; ; i++) {
				String k = names + i;
//				System.out.println("reading " + k);
				String key = properties.getProperty(k);
				if (key == null)
					break;
				if (key.startsWith("FILE.")) {
					String val = properties.getProperty(values + key + ".name");
					outf("%-20s %-30s %s",k,key + ".name",val);
					if (val != null) {
				      FileBody f = new FileBody(new File(val));
						reqEntity.addPart(key.substring("FILE.".length()), f);
					}
				} else {
					String val = properties.getProperty(values + key);
					String outV = val;
					if (outV == null) 
						outV = "";
					else if (key.toLowerCase().equals("password"))
						outV = "******";
					outf("%-20s %-30s %s",k,key,outV);
					reqEntity.addPart(key, new StringBody(val,ContentType.TEXT_PLAIN));
				}
			}
			HttpEntity entity = reqEntity.build();
		    httppost.setEntity(entity);
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
		String add = properties.getProperty(values + "address");
		out(values + "address -> " + add);
		
		if (add != null) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			out("key                  param                          value");
			out("-------------------- ------------------------------ -----------");
			for (int i = 0; ;i++) {
				String k = names + i;
//				System.out.println("reading " + k);
				String key = properties.getProperty(k);
				if (key == null)
					break;
				String val = properties.getProperty(values + key);
				String outV = val;
				if (outV == null) outV = "";
				else if (key.toLowerCase().equals("password"))
					outV = "******";
				outf("%-20s %-30s %s",k,key,outV);
				nameValuePairs.add(new BasicNameValuePair(key, val));
			}
			String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
			
			if(!add.endsWith("?"))
				add += "?";
			
			add+=paramString;
			HttpGet  httpget = new HttpGet(add);
			
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

		String meth = properties.getProperty(values + "method");
		if (meth == null || "POST".equals(meth.toUpperCase()))
			return execPost(n,names,values,httpExpect);
		else if ("POSTMULTI".equals(meth.toUpperCase()))
			return execPostMulti(n, names, values, httpExpect);
		else
			return execGet(n,names,values,httpExpect);

	}
}
