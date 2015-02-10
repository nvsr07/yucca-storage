package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.csi.yucca.storage.datamanagementapi.util.Constants;

public class VarProperties extends Properties {

	private Properties vars;
	private static final long serialVersionUID = 1L;

	public VarProperties(String res) throws IOException {
		InputStream in;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		vars = new Properties();

		System.out.println("try to load " + res);
		in = classLoader.getResourceAsStream(res);
		if (in == null)
			System.out.println(res + " not found or empty");

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
						} 
					}
					i = j;
				}
			} else
				nv.append(in.charAt(i));
		return nv.toString();
	}
}
