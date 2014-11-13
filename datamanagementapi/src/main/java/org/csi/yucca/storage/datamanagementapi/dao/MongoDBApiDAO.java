package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.api.MyApi;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDBApiDAO {
	private DBCollection collection;

	public MongoDBApiDAO(MongoClient mongo, String db, String collection) {
		this.collection = mongo.getDB(db).getCollection(collection);
	}

	public MyApi createApi(MyApi api) {
		String json = api.toJson();
		DBObject dbObject = (DBObject) JSON.parse(json);
		this.collection.insert(dbObject);
		ObjectId id = (ObjectId) dbObject.get("_id");
		api.setId(id.toString());
		return api;
	}

	public void updateDataset(MyApi api) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(api.getId())).get();
		DBObject dbObject = (DBObject) JSON.parse(api.toJson());
		dbObject.removeField("id");
		this.collection.update(query, dbObject);
	}

	public List<MyApi> readAllApi(String tenant) {
		List<MyApi> data = new ArrayList<MyApi>();
		BasicDBObject searchQuery = new BasicDBObject();
		if (tenant != null)
			searchQuery.put("configData.tenant", tenant);

		DBCursor cursor = collection.find(searchQuery);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			MyApi api = MyApi.fromJson(JSON.serialize(doc));
			api.setId(id.toString());
			data.add(api);
		}
		return data;
	}

	public void deleteApi(MyApi api) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(api.getId())).get();
		this.collection.remove(query);
	}

	public MyApi readApi(MyApi api) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(api.getId())).get();
		DBObject data = this.collection.findOne(query);
		ObjectId id = (ObjectId) data.get("_id");
		MyApi apiLoaded = MyApi.fromJson(JSON.serialize(data));
		apiLoaded.setId(id.toString());
		return apiLoaded;
	}
}
