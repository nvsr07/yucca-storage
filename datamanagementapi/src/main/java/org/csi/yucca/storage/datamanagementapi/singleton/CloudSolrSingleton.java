package org.csi.yucca.storage.datamanagementapi.singleton;


import org.apache.solr.client.solrj.impl.CloudSolrClient;

public class CloudSolrSingleton {
	private CloudSolrClient server;
	
	private CloudSolrSingleton() {
		try {
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
