package org.csi.yucca.storage.datamanagementapi.singleton;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.ResponseParser;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.InputStreamResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteSolrException;
import org.apache.solr.common.util.Base64;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.Utils;
public class KnoxSolrSingleton {
	private SolrClient server;

	private KnoxSolrSingleton() {

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		if (Config.getInstance().getSolrUsername()!=null)
		{
			CredentialsProvider provider = new BasicCredentialsProvider();
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					Config.getInstance().getSolrUsername(), Config.getInstance().getSolrPassword());

			provider.setCredentials(AuthScope.ANY, credentials);
			clientBuilder.setDefaultCredentialsProvider(provider);
		}

		clientBuilder.setMaxConnTotal(128);


		try {
//			server = new HttpSolrClient(Config.getInstance().getSolrUrl(),
//					clientBuilder.build());
			server = new TEHttpSolrClient(Config.getInstance().getSolrUrl());
			
		} catch (Exception e) {
			//TODO log
			e.printStackTrace();
		}

	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	static class SingletonHolder { 
		static final KnoxSolrSingleton INSTANCE = new KnoxSolrSingleton();
	}

	public static SolrClient getServer() {
		return SingletonHolder.INSTANCE.server;
	}


	public class TEHttpSolrClient extends HttpSolrClient {

		
		private String defaultCollection=null;
		
		
		public void setDefaultCollection(String defaultCollection) {
			this.defaultCollection = defaultCollection;
		}

		private  final String UTF_8 = StandardCharsets.UTF_8.name();

		public TEHttpSolrClient(String baseURL) {
			super(baseURL);
		}

		@Override
		public NamedList<Object> request(final SolrRequest request, String collection) throws SolrServerException, IOException {
			ResponseParser responseParser = request.getResponseParser();
			if (responseParser == null) {
				responseParser = this.parser;
			}
			System.out.println("-------------------   " +request.getPath() + "              " +collection);
			
			if (collection==null && this.defaultCollection!=null) collection=this.defaultCollection;
			return request(request, responseParser, collection);
		}

		public NamedList<Object> request(final SolrRequest request, final ResponseParser processor, String collection)
				throws SolrServerException, IOException {
			
			
			
			HttpRequestBase method = createMethod(request, collection);
			System.out.println("**********************   " +request.getPath() + "              " +collection);
			System.out.println("**********************   " +method.getURI());
			
			String userPass = Config.getInstance().getSolrUsername()+":"+Config.getInstance().getSolrPassword();
			String encoded = Base64.byteArrayToBase64(userPass.getBytes(UTF_8));
			// below line will make sure that it sends authorization token every time in all your requests
			method.setHeader(new BasicHeader("Authorization", "Basic " + encoded));
			
			
			try {
				return executeMethod(method, processor);
			} catch (Exception e ) {
				e.printStackTrace();
				throw new SolrServerException(e.getMessage());
			} 
			
		}
	}
	
	
	



}
