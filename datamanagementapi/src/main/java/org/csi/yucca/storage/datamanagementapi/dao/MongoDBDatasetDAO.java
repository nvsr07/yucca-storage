package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.DatasetCollectionItem;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDBDatasetDAO {
	private DBCollection collection;

	public MongoDBDatasetDAO(MongoClient mongo, String db, String collection) {
		this.collection = mongo.getDB(db).getCollection(collection);
	}

	public DatasetCollectionItem createDatasetCollectionItem(DatasetCollectionItem datasetCollectionItem) {
		DBObject dbObject = (DBObject) JSON.parse(datasetCollectionItem.toJson());
		this.collection.insert(dbObject);
		ObjectId id = (ObjectId) dbObject.get("_id");
		datasetCollectionItem.setId(id.toString());
		return datasetCollectionItem;
	}

	public void updateDatasetCollectionItem(DatasetCollectionItem datasetCollectionItem) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(datasetCollectionItem.getId())).get();
		DBObject dbObject = (DBObject) JSON.parse(datasetCollectionItem.toJson());
		this.collection.update(query, dbObject);
	}

	public List<DatasetCollectionItem> readAllDatasetCollectionItem() {
		List<DatasetCollectionItem> data = new ArrayList<DatasetCollectionItem>();
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			DatasetCollectionItem datasetCollectionItem = new DatasetCollectionItem(JSON.serialize(doc));
			data.add(datasetCollectionItem);
		}
		return data;
	}

	public void deleteDatasetCollectionItem(DatasetCollectionItem datasetCollectionItem) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(datasetCollectionItem.getId())).get();
		this.collection.remove(query);
	}

	public DatasetCollectionItem readDatasetCollectionItem(DatasetCollectionItem datasetCollectionItem) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(datasetCollectionItem.getId())).get();
		DBObject data = this.collection.findOne(query);
		return new DatasetCollectionItem(JSON.serialize(data));
	}
}
