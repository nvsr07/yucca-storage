package org.csi.yucca.storage.datamanagementapi.singleton;


import org.csi.yucca.storage.datamanagementapi.singleton.Krb5HttpClientConfigurer;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;

public class CloudSolrSingleton {
	private CloudSolrClient server;
	
	private CloudSolrSingleton() {
		try {
			//HttpClientUtil.setConfigurer( new  Krb5HttpClientConfigurer("KERBEROS-POCHDP"));
//			HttpClientUtil.setConfigurer( new  Krb5HttpClientConfigurer(Config.getInstance().getSolrSecurityDomainName()));
			
		server = new CloudSolrClient(Config.getInstance().getSolrUrl());
		} catch (Exception e) {
			
		}
    }
	
		  /**
		   * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
		   * or the first access to SingletonHolder.INSTANCE, not before.
		   */
	  private static class SingletonHolder { 
	    private static final CloudSolrSingleton INSTANCE = new CloudSolrSingleton();
	  }

	  public static CloudSolrClient getServer() {
	    return SingletonHolder.INSTANCE.server;
	  }

}
