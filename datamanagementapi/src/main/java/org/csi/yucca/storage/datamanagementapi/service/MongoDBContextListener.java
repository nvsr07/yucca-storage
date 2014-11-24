package org.csi.yucca.storage.datamanagementapi.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MongoDBContextListener implements ServletContextListener {
	
	@Deprecated
//	public static final String MONGO_CLIENT = "MONGO_CLIENT";
//	public static final String SUPPORT_DB = "SUPPORT_DB";
//	public static final String SUPPORT_DATASET_COLLECTION = "SUPPORT_DATASET_COLLECTION";
//	public static final String SUPPORT_API_COLLECTION = "SUPPORT_API_COLLECTION";

	public void contextDestroyed(ServletContextEvent sce) {
		//MongoClient mongo = (MongoClient) sce.getServletContext().getAttribute("MONGO_CLIENT");
		//mongo.close();
	}

	public void contextInitialized(ServletContextEvent sce) {
		try {
			//ServletContext ctx = sce.getServletContext();
			//MongoClient mongo = new MongoClient(ctx.getInitParameter("MONGODB_HOST"), Integer.parseInt(ctx.getInitParameter("MONGODB_PORT")));
			//sce.getServletContext().setAttribute(MONGO_CLIENT, mongo);
		} catch (Exception e) {
			throw new RuntimeException("MongoClient init failed");
		}
	}

}