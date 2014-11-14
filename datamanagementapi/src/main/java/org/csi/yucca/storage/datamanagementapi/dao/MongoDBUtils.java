package org.csi.yucca.storage.datamanagementapi.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoDBUtils {
	public static Long insertDocumentWithKey(DBCollection col, DBObject obj, String key, Integer maxRetry) throws Exception {
		Long id = 0L;
		try {
			BasicDBObject sortobj = new BasicDBObject();
			sortobj.append(key, -1);
			DBObject doc = col.find().sort(sortobj).limit(1).one();
			System.out.println(doc);
			id = ((Number) doc.get(key)).longValue() + 1;
			obj.put(key, id);
			col.insert(obj);
		} catch (Exception e) {
			if (maxRetry > 0) {
				return insertDocumentWithKey(col, obj, key, --maxRetry);
			} else {
				throw e;
			}
		}
		return id;
	}

	public static Long getIdForInsert(DBCollection col, String key) {
		Long id = 1L;
		BasicDBObject sortobj = new BasicDBObject();
		sortobj.append(key, -1);
		DBObject doc = col.find().sort(sortobj).limit(1).one();
		if(doc != null && doc.get(key)!=null)
			id = ((Number) doc.get(key)).longValue() + 1;
		return id;

	}
}
