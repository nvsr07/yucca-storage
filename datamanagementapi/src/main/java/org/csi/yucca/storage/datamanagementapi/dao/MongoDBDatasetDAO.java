package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.Dataset;

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

	public Dataset createDataset(Dataset dataset) {
		String json = dataset.toJson();
		DBObject dbObject = (DBObject) JSON.parse(json);
		this.collection.insert(dbObject);
		ObjectId id = (ObjectId) dbObject.get("_id");
		dataset.setId(id.toString());
		return dataset;
	}

	public void updateDataset(Dataset dataset) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(dataset.getId())).get();
		DBObject dbObject = (DBObject) JSON.parse(dataset.toJson());
		this.collection.update(query, dbObject);
	}

	public List<Dataset> readAllDataset() {
		List<Dataset> data = new ArrayList<Dataset>();
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			Dataset dataset = Dataset.fromJson(JSON.serialize(doc));
			data.add(dataset);
		}
		return data;
	}

	public void deleteDataset(Dataset dataset) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(dataset.getId())).get();
		this.collection.remove(query);
	}

	public Dataset readDataset(Dataset dataset) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(dataset.getId())).get();
		DBObject data = this.collection.findOne(query);
		return Dataset.fromJson(JSON.serialize(data));
	}
}
