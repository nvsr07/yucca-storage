package org.csi.yucca.storage.datamanagementapi.delegate;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

public class HttpDelegate {

	static Logger log = Logger.getLogger(HttpDelegate.class);
	
	public static String executeGet(String targetUrl, String contentType, String characterEncoding, Map<String, String> parameters) throws IOException {
		log.debug("[HttpDelegate::executeGet] START");
		String result = "";
		int resultCode = -1;
		try {

			if (contentType == null)
				contentType = "application/json";
			if (characterEncoding == null)
				characterEncoding = "UTF-8";

			log.debug("[HttpDelegate::executeGet] - targetUrl: " + targetUrl);

			if (parameters != null) {
				for (String key : parameters.keySet()) {
					targetUrl += key + "=" + parameters.get(key).replaceAll("  ", " ").replaceAll(" ", "%20").
							replaceAll("\\[", "%5B").replaceAll("\\]", "%5D").replaceAll(">", "%3E").replaceAll("<", "%3C") + "&";
				}

			}

			log.debug("[HttpDelegate::executeGet] - targetUrl: " + targetUrl);
			GetMethod get = new GetMethod(targetUrl);
			

			contentType = "application/x-www-form-urlencoded";
			get.setRequestHeader("Content-Type", contentType);

			HttpClient httpclient = new HttpClient();
			try {
				resultCode = httpclient.executeMethod(get);
				log.debug("[HttpDelegate::executeGet] - post result: " + resultCode);
				result = get.getResponseBodyAsString();
			} finally {
				get.releaseConnection();
			}

		} finally {
			log.debug("[HttpDelegate::executeGet] END");
		}
		return result;
	}
	
	
	public static String executePost(String targetUrl, String basicUser, String basicPassword, String contentType, String characterEncoding, Map<String, String> parameters, String data) throws IOException {
		log.debug("[HttpDelegate::executePost] START");
		String result = "";
		int resultCode = -1;
		try {

			if (contentType == null)
				contentType = "application/json";
			if (characterEncoding == null)
				characterEncoding = "UTF-8";

			log.debug("[HttpDelegate::executePost] - targetUrl: " + targetUrl);

			if (parameters != null) {
				for (String key : parameters.keySet()) {
					targetUrl += key + "=" + parameters.get(key).replaceAll("  ", " ").replaceAll(" ", "%20").
							replaceAll("\\[", "%5B").replaceAll("\\]", "%5D").replaceAll(">", "%3E").replaceAll("<", "%3C") + "&";
				}

			}

			
			log.debug("[HttpDelegate::executePost] - targetUrl: " + targetUrl);
			PostMethod post = new PostMethod(targetUrl);

			RequestEntity requestEntity = new StringRequestEntity(data, contentType, characterEncoding);
			post.setRequestEntity(requestEntity);
			

			post.setRequestHeader("Content-Type", contentType);
			HttpClient httpclient = new HttpClient();

			if(basicUser!=null && basicPassword!=null){
				post.setDoAuthentication( true );
				String userPassowrd  = basicUser + ":" + basicPassword;
				byte[] encoding = Base64.encodeBase64(userPassowrd.getBytes());
				post.setRequestHeader("Authorization", "Basic " + new String(encoding));
				
			}
			
			try {
				resultCode = httpclient.executeMethod(post);
				log.debug("[HttpDelegate::executePost] - post result: " + resultCode);
				result = post.getResponseBodyAsString();
			} finally {
				post.releaseConnection();
			}

		} finally {
			log.debug("[HttpDelegate::executePost] END");
		}
		return result;
	}
	
}
