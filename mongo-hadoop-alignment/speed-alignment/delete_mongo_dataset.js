// run with
// mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin  --quiet --eval "var param1=<IDDATASET>; var param2=<datasetVersion>; var param3='<ORIGIN>'" delete_dataset.js
//
var env = {};
env.DATASET=param1;
env.VERSION=param2;
env.ORIGIN=param3;

dbSupport = db.getSiblingDB("DB_SUPPORT");
//print("Count:"+dbSupport.getCollection('metadata').find({idDataset:env.DATASET,datasetVersion:env.VERSION}).count());
//print(dbSupport.getCollectionNames());
listColl=dbSupport.getCollection('metadata').find({idDataset:env.DATASET,datasetVersion:env.VERSION});
while (listColl.hasNext()) {
  myColl = listColl.next();
  print("datasetCode:"+myColl.datasetCode);
  myTenant = myColl.configData.tenantCode;
  if (myColl.configData.collection == null || myColl.configData.collection == undefined) {
    if (myColl.configData.subtype == "bulkDataset") {
      nomeColl = "data";
    } else if (myColl.configData.subtype  == "socialDataset") {
      nomeColl = "social";
    } else if (myColl.configData.subtype  == "binaryDataset") {
      nomeColl = "media";
    } else {
      nomeColl = "measures";
    }
  } else {
    print("Passa da else");
    nomeColl = myColl.configData.collection;
  }
}
//print("Nome coll:"+nomeColl);
print("Tenant:"+myTenant);

dbTenant = db.getSiblingDB("DB_"+myTenant);
dbTenant.getCollection(nomeColl).remove({idDataset:env.DATASET,datasetVersion:env.VERSION,origin:env.ORIGIN});
print('dbTenant.getCollection("'+nomeColl+'").remove({idDataset:'+env.DATASET+',datasetVersion:'+env.VERSION+',origin:"'+env.ORIGIN+'"});');
