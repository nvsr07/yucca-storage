// lanciare con
// mongo <DB_TENANT> --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<collection>'; var param2=<idDataset>; var param3=<datasetVersion>; var param4=<objectId.valueOf>" svecchia_dati.js
//
var env = {};
env.COLL=param1;
env.IdDataset=param2;
env.VERS=param3;
env.TS=param4;
//print("Param:"+env.COLL+" - "+env.IdDataset+" - "+env.VERS+" - "+env.TS);
myCollection=db.getCollection(env.COLL);
esito=myCollection.remove({ $and: [ {idDataset: env.IdDataset}, {datasetVersion: env.VERS}, { _id: { $lt: ObjectId(env.TS) } } ] });
if (esito.writeConcernError || esito.writeError) {
  // error during erasing
   throw "Error executing cleaning up on "+env.COLL+" collection, dataset id "+env.IdDataset+" - version "+env.VERS+", threshold "+env.TS+" - data "+ObjectId(env.TS).getTimestamp();
}
else {
  //print how many documents have been erased
  print(esito.nRemoved+" documents removed");
}

