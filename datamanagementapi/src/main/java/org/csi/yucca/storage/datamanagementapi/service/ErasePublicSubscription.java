package org.csi.yucca.storage.datamanagementapi.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.store.response.SubscriptionResponse;
import org.csi.yucca.storage.datamanagementapi.model.store.response.Subscriptions;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;
import org.csi.yucca.storage.datamanagementapi.singleton.Config;
import org.csi.yucca.storage.datamanagementapi.singleton.MongoSingleton;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class ErasePublicSubscription {

	static Logger log = Logger.getLogger(ErasePublicSubscription.class);
	private static DBCollection collection;
	private static MongoClient mongo;

	public static void main(String[] args) {
		log.info("[ErasePublicSubscription::main] - START");
		// TODO Auto-generated method stub

		try {
			mongo = MongoSingleton.getMongoClient();
			String supportDb = Config.getInstance().getDbSupport();
			String supportDatasetCollection = Config.getInstance().getCollectionSupportDataset();
			collection = mongo.getDB(supportDb).getCollection(supportDatasetCollection);

			BasicDBList and = new BasicDBList();
			and.add(new BasicDBObject("configData.deleted", new BasicDBObject("$not", new BasicDBObject("$eq", 1))));
			and.add(new BasicDBObject("configData.current", 1));
			and.add(new BasicDBObject("info.visibility", "public"));
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("$and", and);

			log.info(" ======== > SearchQuery " + searchQuery.toString());
			CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(
					Config.getInstance().getStoreUsername(), Config.getInstance().getStorePassword());
			SubscriptionResponse listSubscriptions = ApiManagerFacade.listSubscription(httpClient);

			log.info(" ======== > call eraseDatasetSubscription");
			// eraseDatasetSubscription(searchQuery, listSubscriptions,
			// httpClient);

			supportDb = Config.getInstance().getDbSupport();
			supportDatasetCollection = Config.getInstance().getCollectionSupportStream();
			collection = mongo.getDB(supportDb).getCollection(supportDatasetCollection);

			and = new BasicDBList();
			and.add(new BasicDBObject("configData.deleted", new BasicDBObject("$not", new BasicDBObject("$eq", 1))));
			and.add(new BasicDBObject("streams.stream.visibility", "public"));
			searchQuery = new BasicDBObject();
			searchQuery.put("$and", and);

			log.info(" ======== > SearchQuery " + searchQuery.toString());
			log.info(" ======== > call eraseStreamSubscription");
			eraseStreamSubscription(searchQuery, listSubscriptions, httpClient);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("[ErasePublicSubscription::main] - END");
	}

	private static void eraseDatasetSubscription(BasicDBObject searchQuery, SubscriptionResponse listSubscriptions,
			CloseableHttpClient httpClient) throws Exception {
		log.info("[ErasePublicSubscription::eraseDatasetSubscription] - START");

		DBCursor cursor = collection.find(searchQuery);
		List<Metadata> allDataset = new ArrayList<Metadata>();
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			Metadata metadata = Metadata.fromJson(JSON.serialize(doc));
			metadata.setId(id.toString());
			allDataset.add(metadata);
		}

		Iterator<Metadata> allDatasetIterator = allDataset.iterator();

		while (allDatasetIterator.hasNext()) {
			Metadata metaData = allDatasetIterator.next();
			log.info("Dataset => " + metaData.getDatasetCode());
			String appName = "userportal_" + metaData.getConfigData().getTenantCode();
			log.info("appName => " + appName);

			for (Subscriptions tenantSubscription : listSubscriptions.getSubscriptions()) {
				if (tenantSubscription.getName().equals(appName)) {
					log.info("Remove " + tenantSubscription.toString() + ", tenantSubscription="
							+ tenantSubscription.getName());
					ApiManagerFacade.unSubscribeApi(httpClient, metaData.getDatasetCode() + "_odata", appName,
							tenantSubscription.getId(), "admin");
				}
			}
		}
		log.info("[ErasePublicSubscription::eraseDatasetSubscription] - END");
	}

	private static void eraseStreamSubscription(BasicDBObject searchQuery, SubscriptionResponse listSubscriptions,
			CloseableHttpClient httpClient) throws Exception {
		log.info("[ErasePublicSubscription::eraseStreamSubscription] - START");

		DBCursor cursor = collection.find(searchQuery);
		List<StreamOut> allStream = new ArrayList<StreamOut>();
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			StreamOut stream = StreamOut.fromJson(JSON.serialize(doc));
			allStream.add(stream);
		}

		Iterator<StreamOut> allStreamIterator = allStream.iterator();

		while (allStreamIterator.hasNext()) {
			StreamOut stream = allStreamIterator.next();
			log.info("StreamOut => " + stream.getStreamName());
			String appName = "userportal_" + stream.getConfigData().getTenantCode();
			log.info("appName => " + appName);

			for (Subscriptions tenantSubscription : listSubscriptions.getSubscriptions()) {
				if (tenantSubscription.getName().equals(appName)) {
					log.info("Remove " + tenantSubscription.toString() + ", tenantSubscription="
							+ tenantSubscription.getName());
					ApiManagerFacade.unSubscribeApi(httpClient,
							stream.getConfigData().getTenantCode() + "."
									+ stream.getStreams().getStream().getVirtualEntityCode() + "_"
									+ stream.getStreamCode() + "_stream",
							appName, tenantSubscription.getId(), "admin");
				}
			}
		}
		log.info("[ErasePublicSubscription::eraseStreamSubscription] - END");
	}
}
