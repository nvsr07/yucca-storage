package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.model.streamOutput.StreamOut;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDBStreamDAO {
	private DBCollection collection;
	static Logger log = Logger.getLogger(MongoDBStreamDAO.class);

	public MongoDBStreamDAO(MongoClient mongo, String db, String collection) {
		this.collection = mongo.getDB(db).getCollection(collection);
	}

	public StreamOut createStream(StreamOut stream) {
		throw new UnsupportedOperationException("create Stream is not implemented");
	}

	public StreamOut createNewVersion(StreamOut stream) {
		throw new UnsupportedOperationException("createNewVersion Stream is not implemented");
	}

	public void updateStream(StreamOut stream) {
		throw new UnsupportedOperationException("createNewVersion Stream is not implemented");
	}

	public List<StreamOut> readAllStream(String tenant, boolean onlyCurrent) {
		List<StreamOut> data = new ArrayList<StreamOut>();
		BasicDBObject searchQuery = new BasicDBObject();
		if (tenant != null)
			searchQuery.put("configData.tenantCode", tenant);
		if (onlyCurrent)
			searchQuery.put("configData.current", 1);

		DBCursor cursor = collection.find(searchQuery);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			ObjectId id = (ObjectId) doc.get("_id");
			StreamOut stream = StreamOut.fromJson(JSON.serialize(doc));
			stream.setId(id.toString());
			data.add(stream);
		}
		return data;
	}

	public void deleteStream(StreamOut stream) {
		throw new UnsupportedOperationException("delete Stream is not implemented");
	}

	public StreamOut readStream(StreamOut stream) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(stream.getId())).get();
		DBObject data = this.collection.findOne(query);
		ObjectId id = (ObjectId) data.get("_id");
		StreamOut streamLoaded = StreamOut.fromJson(JSON.serialize(data));
		streamLoaded.setId(id.toString());
		return streamLoaded;
	}

	public StreamOut readStreamByMetadata(Metadata metadata) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("configData.idDataset", metadata.getIdDataset());
		searchQuery.put("configData.datasetVersion", metadata.getDatasetVersion());

		DBObject data = collection.find(searchQuery).one();
		StreamOut streamLoaded = null;
		if (data != null) {
			ObjectId id = (ObjectId) data.get("_id");
			streamLoaded = StreamOut.fromJson(JSON.serialize(data));
			streamLoaded.setId(id.toString());
		}
		return streamLoaded;
	}
}
