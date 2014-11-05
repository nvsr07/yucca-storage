package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.dataset.Dataset;

import com.mongodb.BasicDBObject;
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
		dataset.getConfigData().setDatasetversion("1");
		dataset.getConfigData().setCurrent("1");
		dataset.getMetadata().setRegistrationDate(new Date());
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
		dbObject.removeField("id");
		this.collection.update(query, dbObject);
	}

	public List<Dataset> readAllDataset(String tenant) {
		List<Dataset> data = new ArrayList<Dataset>();
		BasicDBObject searchQuery = new BasicDBObject();
		if (tenant != null)
			searchQuery.put("configData.tenant", tenant);

		DBCursor cursor = collection.find(searchQuery);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			Dataset dataset = Dataset.fromJson(JSON.serialize(doc));
			dataset.setId(id.toString());
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
		ObjectId id = (ObjectId) data.get("_id");
		Dataset datasetLoaded = Dataset.fromJson(JSON.serialize(data));
		datasetLoaded.setId(id.toString());
		return datasetLoaded;
	}
}
