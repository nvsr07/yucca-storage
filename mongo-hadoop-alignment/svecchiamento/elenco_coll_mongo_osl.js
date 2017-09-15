// lanciare con
// mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1=<idDataset>;" elenco_coll_mongo.js
//
var env = {};
env.idDataset=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
tenantCode = dbSupporto.metadata.find({"idDataset":env.idDataset}, {"configData.tenantCode":1}).toArray()[0].configData.tenantCode;
elencoTenant = dbSupporto.tenant.find({"tenantCode":tenantCode}, {dataCollectionDb:1, dataCollectionName:1, measuresCollectionDb:1, 
	measuresCollectionName:1, mediaCollectionDb:1, mediaCollectionName:1, socialCollectionDb:1,	socialCollectionName:1});
while (elencoTenant.hasNext()) {
  t = elencoTenant.next(); 
  print(t.dataCollectionName + ";" + t.dataCollectionDb + ";" + t.measuresCollectionName + ";" + t.measuresCollectionDb + ";" + 
		  t.mediaCollectionName + ";" + t.mediaCollectionDb + ";" + t.socialCollectionName + ";" + t.socialCollectionDb);
}
