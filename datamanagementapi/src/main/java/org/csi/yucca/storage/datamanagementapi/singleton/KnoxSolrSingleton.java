package org.csi.yucca.storage.datamanagementapi.singleton;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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
		server = new HttpSolrClient(Config.getInstance().getSolrUrl(),
				clientBuilder.build());
		
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

}
