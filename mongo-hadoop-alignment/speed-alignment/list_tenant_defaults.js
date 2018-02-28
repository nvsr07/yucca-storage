// lanciare con
// mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin  --quiet --eval "var param1='<TENANT CODE>'" elenco_dataset_json.js
//
var env = {};
env.TENANT=param1;

myArr=db.getCollection('tenant').find({"tenantCode":env.TENANT},{ _id:0, 
	dataCollectionName:1, dataCollectionDb:1, measuresCollectionName:1, measuresCollectionDb:1,
	socialCollectionName:1, socialCollectionDb:1, mediaCollectionName:1, mediaCollectionDb:1, 
	dataPhoenixTableName:1, dataPhoenixSchemaName:1, measuresPhoenixTableName:1, measuresPhoenixSchemaName:1, 
	mediaPhoenixTableName:1, mediaPhoenixSchemaName:1, socialPhoenixTableName:1, socialPhoenixSchemaName:1, 
	dataSolrCollectionName:1, measuresSolrCollectionName:1, mediaSolrCollectionName:1, socialSolrCollectionName:1,
	organizationCode:1
}).toArray();
printjson(myArr[0]);
