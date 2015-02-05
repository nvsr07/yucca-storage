package org.csi.yucca.storage.datamanagementapi.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.csi.yucca.storage.datamanagementapi.model.metadata.Metadata;
import org.csi.yucca.storage.datamanagementapi.service.StoreService;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDBMetadataDAO {
	private DBCollection collection;
	static Logger log = Logger.getLogger(MongoDBMetadataDAO.class);

	public MongoDBMetadataDAO(MongoClient mongo, String db, String collection) {
		this.collection = mongo.getDB(db).getCollection(collection);
	}

	public Metadata createMetadata(Metadata metadata,Long idDataset) {


		for (int i = 0; i < 5; i++) {
			try {
				if(idDataset==null){
					metadata.setIdDataset(MongoDBUtils.getIdForInsert(this.collection, "idDataset"));
				}else{
					metadata.setIdDataset(idDataset);
				}
				metadata.generateCode();
				metadata.generateNameSpace();

				String json = metadata.toJson();
				DBObject dbObject = (DBObject) JSON.parse(json);

				DBObject uniqueMetadata = new BasicDBObject("idDataset",metadata.getIdDataset());
				uniqueMetadata.put("datasetVersion",metadata.getDatasetVersion());

				// if the metadata with that id and version exists .. update it, otherwise insert the new one.
				//upsert:true  multi:false
				this.collection.update(uniqueMetadata,dbObject,true,false);
				//				ObjectId id = (ObjectId) dbObject.get("_id");
				//				metadata.setId(id.toString());
				break;
			} catch (Exception e) {
				log.error("[] - ERROR in insert. Attempt " + i + " - message: " + e.getMessage());
			}
		}

		/*
		 * Create api in the store
		 */
		String apiName = "";
		try{
			apiName = StoreService.createApiforBulk(metadata,false);
		}catch(Exception duplicate){
			if(duplicate.getMessage().toLowerCase().contains("duplicate")){
				try {
					apiName = StoreService.createApiforBulk(metadata,true);
				} catch (Exception e) {
					log.error("[] - ERROR to update API in Store for Bulk. Message: " + duplicate.getMessage());
				}
			}else{
				log.error("[] -  ERROR in create or update API in Store for Bulk. Message: " + duplicate.getMessage());
			} 
		}
		try {
			StoreService.publishStore("1.0", apiName, "admin");

			String appName = "userportal_"+metadata.getConfigData().getTenantCode();
			StoreService.addSubscriptionForTenant(apiName,appName);


		} catch (Exception e) {
			log.error("[] - ERROR in publish Api in store - message: " + e.getMessage());
		}


		return metadata;
	}

	public Metadata createNewVersion(Metadata metadata){

		metadata.setDatasetVersion(metadata.getDatasetVersion() + 1);
		metadata.getConfigData().setCurrent(1);
		metadata.getInfo().setRegistrationDate(new Date());

		String json = metadata.toJson();
		DBObject dbObject = (DBObject) JSON.parse(json);

		this.collection.insert(dbObject);
		ObjectId id = (ObjectId) dbObject.get("_id");
		metadata.setId(id.toString());
		return metadata;
	}

	public void updateMetadata(Metadata metadata) {
		DBObject query = BasicDBObjectBuilder.start().append("_id", new ObjectId(metadata.getId())).get();
		DBObject dbObject = (DBObject) JSON.parse(metadata.toJson());
		dbObject.removeField("id");
		this.collection.update(query, dbObject);
	}


	public List<Metadata> readAllMetadata(String tenant, boolean onlyCurrent) {
		List<Metadata> data = new ArrayList<Metadata>();
		BasicDBObject searchQuery = new BasicDBObject();
		if (tenant != null)
			searchQuery.put("configData.tenantCode", tenant);
		if(onlyCurrent)
			searchQuery.put("configData.current", 1);

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

	public Metadata readCurrentMetadataByCode(String metadataCode) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("datasetCode", metadataCode);
		searchQuery.put("configData.current", 1);

		DBObject data = collection.find(searchQuery).one();
		ObjectId id = (ObjectId) data.get("_id");
		Metadata metadataLoaded = Metadata.fromJson(JSON.serialize(data));
		metadataLoaded.setId(id.toString());
		return metadataLoaded;
	}

}
