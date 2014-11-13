package org.csi.yucca.storage.datamanagementapi.service;

import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mongodb.MongoClient;

@WebListener
public class MongoDBContextListener implements ServletContextListener {
	
	public static final String MONGO_CLIENT = "MONGO_CLIENT";
	public static final String SUPPORT_DB = "SUPPORT_DB";
	public static final String SUPPORT_DATASET_COLLECTION = "SUPPORT_DATASET_COLLECTION";
	public static final String SUPPORT_API_COLLECTION = "SUPPORT_API_COLLECTION";

	public void contextDestroyed(ServletContextEvent sce) {
		MongoClient mongo = (MongoClient) sce.getServletContext().getAttribute("MONGO_CLIENT");
		mongo.close();
	}

	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext ctx = sce.getServletContext();
			MongoClient mongo = new MongoClient(ctx.getInitParameter("MONGODB_HOST"), Integer.parseInt(ctx.getInitParameter("MONGODB_PORT")));
			sce.getServletContext().setAttribute(MONGO_CLIENT, mongo);
			
			sce.getServletContext().setAttribute(SUPPORT_DB, ctx.getInitParameter(SUPPORT_DB));
			sce.getServletContext().setAttribute(SUPPORT_DATASET_COLLECTION, ctx.getInitParameter(SUPPORT_DATASET_COLLECTION));
			sce.getServletContext().setAttribute(SUPPORT_API_COLLECTION, ctx.getInitParameter(SUPPORT_API_COLLECTION));

			
		} catch (UnknownHostException e) {
			throw new RuntimeException("MongoClient init failed");
		}
	}

}