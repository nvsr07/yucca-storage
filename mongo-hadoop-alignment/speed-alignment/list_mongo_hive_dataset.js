// lanciare con
// mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin  --quiet --eval "var param1='<TENANT CODE>'" elenco_dataset_json.js
//
var env = {};
env.TENANT=param1;

myArr=db.metadata.find({"configData.tenantCode":env.TENANT, "configData.current":1, "availableSpeed":true, "availableHive":true},
		{_id:0,idDataset:1,datasetCode:1,datasetVersion:1,"configData.subtype":1,"info.fields":1,dbHiveSchema:1,dbHiveTable:1}).toArray();
printjson(myArr);