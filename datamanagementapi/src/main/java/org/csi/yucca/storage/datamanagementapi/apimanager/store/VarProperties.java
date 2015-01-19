package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class VarProperties extends Properties {

	private Properties vars;
	private static final long serialVersionUID = 1L;

	public VarProperties(String res,String... v) throws IOException {
		InputStream in;
		String path = "";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (v != null) {
			vars = new Properties();
			for (String name : v) {
				System.out.println("Properties: try to load " + name);
//				in = VarProperties.class.getResourceAsStream(name);
				in = classLoader.getResourceAsStream(name);
//				BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				if (in != null) {
					vars.load(in);
					in.close();
					System.out.println("Properties: loaded " + name + " count " + vars.size());
				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
//					BufferedReader br = new BufferedReader(new FileReader(name));
					vars.load(br);
					System.out.println("Properties: loaded " + name + " count " + vars.size());
					br.close();
				}
			}
			path = (String) vars.get("confPath");
			if (path == null) path = "";
		}
		System.out.println("try to load " + path + res);
		in =  classLoader.getResourceAsStream(path + res);
		if (in == null && path.length() > 0) {
			System.out.println(path + res + " not found or empty");
			System.out.println("try to load " + res);
			in = classLoader.getResourceAsStream(res);
			if (in == null)
				System.out.println(res + " not found or empty");
		}
		if (in != null)
			load(in);
	}
	
	public String getVar(String key) {
		String v = vars.getProperty(key);
		return expand(v);
	}
	
	public String setVar(String key,String val) {
		String v = (String) vars.setProperty(key,val);
		return v;
	}
	
	@Override
	public String getProperty(String key) {
		String v =  super.getProperty(key);
		return expand(v);
	}
	
	@Override
	public synchronized Object get(Object key) {
		Object ov = super.get(key);
		return ov;
	}
	
	private String expand(String in) {
		if (vars == null || in == null)
		    return in;
		if (in.indexOf('$') == -1)
			return in;
		StringBuilder nv = new StringBuilder(in.length());
		int l = in.length();
		for (int i = 0; i < l; i++)
			if (in.charAt(i) == '$' && i < l - 1 && in.charAt(i + 1) == '{') {
				int j = in.indexOf('}', i + 1);
				if (j == -1)
					nv.append('$');
				else {
					String key = in.substring(i + 2, j);
					if (key != null) {
						String val = vars.getProperty(key);
						if (val != null) {
							nv.append(expand(val));
						} // else
				         //		nv.append("${" + key + "}");
					}
					i = j;
				}
			} else
				nv.append(in.charAt(i));
		return nv.toString();
	}
}
