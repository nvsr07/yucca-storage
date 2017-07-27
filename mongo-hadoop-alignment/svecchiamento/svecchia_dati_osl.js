 // lanciare con
// mongo <DB_TENANT> --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<collection>'; var param2=<idDataset>; var param3='<datasetVersion>';" svecchia_dati.js
//
var env = {};
env.COLL=param1;
env.IdDataset=param2;
env.minId=param3;
//print("Param:"+env.COLL+" - "+env.IdDataset+" - "+env.minId);
myCollection=db.getCollection(env.COLL);
esito=myCollection.remove({ $and: [ {idDataset: env.IdDataset}, { _id: { $lt: ObjectId(env.minId) } } ] });
if (esito.writeConcernError || esito.writeError) {
  // error during erasing
   throw "Error executing cleaning up on "+env.COLL+" collection, dataset id "+env.IdDataset+", threshold "+env.minId+" - data "+ObjectId(env.minId).getTimestamp();
}
else {
  //print how many documents have been erased
  print(esito.nRemoved+" documents removed");
}

