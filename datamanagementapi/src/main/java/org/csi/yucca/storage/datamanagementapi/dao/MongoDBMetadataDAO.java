package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDBMetadataDAO {
	private DBCollection collection;

	public MongoDBMetadataDAO(MongoClient mongo, String db, String collection) {
		this.collection = mongo.getDB(db).getCollection(collection);
	}

	public Metadata createMetadata(Metadata metadata) {
		metadata.setDatasetVersion(1);
		metadata.getConfigData().setCurrent("1");
		metadata.getInfo().setRegistrationDate(new Date());
		String json = metadata.toJson();
		DBObject dbObject = (DBObject) JSON.parse(json);
		this.collection.insert(dbObject);
		ObjectId id = (ObjectId) dbObject.get("_id");
		metadata.setId(id.toString());
		return metadata;
	}

	public void updateDataset(Metadata metadata) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(metadata.getId())).get();
		DBObject dbObject = (DBObject) JSON.parse(metadata.toJson());
		dbObject.removeField("id");
		this.collection.update(query, dbObject);
	}

	public List<Metadata> readAllMetadata(String tenant) {
		List<Metadata> data = new ArrayList<Metadata>();
		BasicDBObject searchQuery = new BasicDBObject();
		if (tenant != null)
			searchQuery.put("configData.tenant", tenant);

		DBCursor cursor = collection.find(searchQuery);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			Metadata metadata = Metadata.fromJson(JSON.serialize(doc));
			metadata.setId(id.toString());
			data.add(metadata);
		}
		return data;
	}

	public void deleteMetadata(Metadata metadata) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(metadata.getId())).get();
		this.collection.remove(query);
	}

	public Metadata readMetadata(Metadata metadata) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(metadata.getId())).get();
		DBObject data = this.collection.findOne(query);
		ObjectId id = (ObjectId) data.get("_id");
		Metadata metadataLoaded = Metadata.fromJson(JSON.serialize(data));
		metadataLoaded.setId(id.toString());
		return metadataLoaded;
	}
}
