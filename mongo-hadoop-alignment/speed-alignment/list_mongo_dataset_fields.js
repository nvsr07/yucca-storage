// lanciare con
// mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin  --quiet --eval "var param1='<TENANT CODE>'" elenco_dataset_json.js
//
var env = {};
env.TENANT=param1;

myArr=db.getCollection('metadata').aggregate([{ $match : {"configData.tenantCode":env.TENANT}}, { $project : { "idDataset" :1, "datasetVersion":1, "info.fields.fieldName" : 1, "info.fields.dataType" : 1, "subtype" : "$configData.subtype" }}, { $project : { "idDataset" :1, "datasetVersion":1, "fields" : "$info.fields", "subtype" : "$subtype"}}, { $group: { _id : {subtype :"$subtype" , fields : "$fields"}, datasets : { $push: {idDataset: "$idDataset" , datasetVersion : "$datasetVersion"}}}}]).toArray();
printjson(myArr);
