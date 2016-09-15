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

public class ListTenantSubscription {

	static Logger log = Logger.getLogger(MetadataService.class);
	private static DBCollection collection;
	private static MongoClient mongo;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			mongo = MongoSingleton.getMongoClient();
			String supportDb = Config.getInstance().getDbSupport();
			String supportDatasetCollection = Config.getInstance().getCollectionSupportStream();
			collection = mongo.getDB(supportDb).getCollection(supportDatasetCollection);

			BasicDBList and = new BasicDBList();
			and.add(new BasicDBObject("configData.deleted", new BasicDBObject("$not", new BasicDBObject("$eq", 1))));
			and.add(new BasicDBObject("configData.current", 1));
			and.add(new BasicDBObject("info.visibility", "public"));
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("$and", and);

			log.info(" ======== > SearchQuery " + searchQuery.toString());
			eraseDatasetSubscription(searchQuery);
			eraseStreamSubscription(searchQuery);
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
	}

	private static void eraseDatasetSubscription(BasicDBObject searchQuery) throws Exception {
		log.debug("[ErasePublicSubscription::eraseDatasetSubscription] - START");

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
		CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(Config.getInstance().getStoreUsername(),
				Config.getInstance().getStorePassword());
		SubscriptionResponse listSubscriptions = ApiManagerFacade.listSubscription(httpClient);
		
		while (allDatasetIterator.hasNext()) {
			Metadata metaData = allDatasetIterator.next();
			String appName = "userportal_" + metaData.getConfigData().getTenantCode();

			for (Subscriptions tenantSubscription : listSubscriptions.getSubscriptions()) {
				tenantSubscription.toString();
			}
		}
	}

	private static void eraseStreamSubscription(BasicDBObject searchQuery) throws Exception {
		log.debug("[ErasePublicSubscription::eraseStreamSubscription] - START");

		DBCursor cursor = collection.find(searchQuery);
		List<StreamOut> allStream = new ArrayList<StreamOut>();
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			StreamOut stream = StreamOut.fromJson(JSON.serialize(doc));
			allStream.add(stream);
		}

		Iterator<StreamOut> allStreamIterator = allStream.iterator();
		CloseableHttpClient httpClient = ApiManagerFacade.registerToStoreInit(Config.getInstance().getStoreUsername(),
				Config.getInstance().getStorePassword());
		SubscriptionResponse listSubscriptions = ApiManagerFacade.listSubscription(httpClient);
		
		while (allStreamIterator.hasNext()) {
			StreamOut stream = allStreamIterator.next();
			String appName = "userportal_" + stream.getConfigData().getTenantCode();

			for (Subscriptions tenantSubscription : listSubscriptions.getSubscriptions()) {
				tenantSubscription.toString();
			}
		}
	}
}
