package org.csi.yucca.storage.datamanagementapi.apimanager.store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMap {
/** BEGINTEST
{"error" : false}
{"pippo" : 12}
{"data" : {"key" : 12}}
{"data" : [{"key" : 12}]}
{"data" : {"key" : {"consumerKey" : "cka9CyJR9aByeDqQAR7wS7s9KTsa", "accessToken" : "PZF8xS9UPWXEpbAx_utQr3ihcHwa", "consumerSecret" : "9IeOlqBguflo0rbbnbJkPsIYto0a", "enableRegenarate" : true, "accessallowdomains" : ""}}, "error" : false}
{"applications" : [{"id" : 46, "callbackUrl" : "", "name" : "A-test", "tier" : "Unlimited"}, {"id" : 33, "callbackUrl" : null, "name" : "DefaultApplication", "tier" : "Unlimited"}], "error" : false}
{"error" : false, "api" : { "inSequence" : "", "outSequence" : "",  "endpointConfig" : "{\"production_endpoints\":{\"url\":\"http:\\/\\/tst-www.geoportale.piemonte.it\\/geocatalogorp\\/geonetworkrp\\/srv\\/it\"},\"endpoint_type\":\"http\"}"}}
ENDTEST  **/ 

	String sym;
	Object val;

	private int getNum(String in,int i) {
		StringBuilder out = new StringBuilder();
		if (Character.isDigit(in.charAt(i))) {
			while (Character.isDigit(in.charAt(i))) {
				out.append(in.charAt(i));
				i++;
			}
			sym = out.toString();
			return i;
		}		
		throw new RuntimeException("syntax error: expected digit in " + in.substring(i));
	}

	private int getId(String in,int i) {
	 	if (in.charAt(i) == '"') {
	 		int j;
	 		for (j = i + 1; j < in.length(); j++)
	 			if (in.charAt(j) == '\\' && in.charAt(j + 1) == '"') {
	 				j++;
	 				continue;
	 			} else if (in.charAt(j) == '"')
	 				break;
	 		if (j >= in.length())
				throw new RuntimeException("syntax error: expected closing '\"' in " + in.substring(i));
	 		sym = in.substring(i + 1,j);
	 		return j + 1;
	 	}
		throw new RuntimeException("syntax error: expected '\"' in " + in.substring(i));
	}
	
	private int list(String in,int i,List<Object> out) {
		if (in.charAt(i) == '[') {
			i++;
			while(i < in.length() && in.charAt(i) != ']') {
				i = getVal(in,i);
				out.add(val);
				while (i < in.length() && Character.isWhitespace(in.charAt(i))) i++;
				if (in.charAt(i) == ',') {
					i++;
					while (i < in.length() && Character.isWhitespace(in.charAt(i))) i++;
				}
			}
			if (in.charAt(i) == ']') 
				i++;
			else
				throw new RuntimeException("syntax error: expected ']' in" + in.substring(i));					
			return i;
		}
		throw new RuntimeException("syntax error: expected '[' in" + in.substring(i));					
	}
	
	private int getVal(String in,int i) {
		while (i < in.length() && Character.isWhitespace(in.charAt(i))) i++;

		if (in.length() - 5 >= i &&
			in.charAt(i) == 'f' && in.charAt(i + 1) == 'a' &&
			in.charAt(i + 2) == 'l' && in.charAt(i + 3) == 's' &&
			in.charAt(i + 4) == 'e') {
			val = Boolean.FALSE;
			return i + 5;
		}
		if (in.length() - 4 >= i &&
			in.charAt(i) == 't' && in.charAt(i + 1) == 'r' &&
			in.charAt(i + 2) == 'u' && in.charAt(i + 3) == 'e') {
			val = Boolean.TRUE;
			return i + 4;
		}
		if (in.length() - 4 >= i &&
			in.charAt(i) == 'n' && in.charAt(i + 1) == 'u' &&
			in.charAt(i + 2) == 'l' && in.charAt(i + 3) == 'l') {
			val = null;
			return i + 4;
		}
		switch(in.charAt(i)) {
		case '{' :
			HashMap<String,Object> block = new HashMap<String,Object>();
			i = block(in,i,block);
			val = block;
			return i;
		case '[' :
			List<Object> ll = new ArrayList<Object>();
			i = list(in,i,ll);
			val = ll;
			return i;
			
		case '"' :
			i = getId(in,i);
			val = sym;
			return i;		
		
		case '0' : case '1' : case '2' : case '3' : case '4' :
		case '5' : case '6' : case '7' : case '8' : case '9' :
			
			i = getNum(in,i);
			val = new Integer(sym);
			return i;
		}	
		throw new RuntimeException("syntax error: in " + in.substring(i));
	}
	
	private int elem(String in,int i,Map<String,Object> out) {
		while (i < in.length() && Character.isWhitespace(in.charAt(i))) i++;
		switch(in.charAt(i)) {
		case '"' :
			i = getId(in,i);
			String id = sym;
			while (i < in.length() && Character.isWhitespace(in.charAt(i))) i++;
			if (in.charAt(i) == ':') {
				i = getVal(in,i + 1);
				out.put(id, val);
			} else
				throw new RuntimeException("syntax error: expected ':' in " + in.substring(i));
		}
		return i;	
	}
	
	private int block(String in,int i,Map<String,Object> out) {
		if (in.charAt(i) == '{') {
			i = elem(in,i + 1,out);
			while (in.charAt(i)== ',') {
				i = elem(in,i + 1,out);
			}
			if (i >= in.length() || in.charAt(i) != '}')
				throw new RuntimeException("syntax error: expected '}' in " + in);
			i++;
		} else
			throw new RuntimeException("syntax error: expected '{' in " + in);
		return i;
	}
	
	private void parse(String in,Map<String,Object> out) {
		int i = 0;
		int len = in.length();
		while (i < len && Character.isWhitespace(in.charAt(i))) i++;
		if (i < len)
			i = block(in,i,out);
	}
	
	public Map<String,Object> decode(String in) {
		HashMap<String,Object> out = new HashMap<String,Object>();
//		System.out.println("in decode in " + in);
		if (in != null) 
			parse(in,out);
		return out;
	}
	
	public static void main(String[] args) throws IOException {
		
		boolean inTest = false;
		JsonMap me = new JsonMap();
		BufferedReader reader = new BufferedReader(new FileReader("src/java/it/csi/wso2/apiman/load/JsonMap.java"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (!inTest && line.contains("BEGINTEST") && !line.contains("contains")) {
				inTest = true;
			} else if (inTest && line.contains("ENDTEST")) {
				inTest = false;
			} else if (inTest) {
				System.out.println("Test on: " + line);
				Map<String,Object> out = me.decode(line);
				System.out.println("out " + out);
			}
		}
	}

}
