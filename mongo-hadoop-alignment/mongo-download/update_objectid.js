// lanciare con
// mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<TENANT>';var param2=<objectId.valueOf>" update_objectid.js
//
var env = {};
env.TENANT=param1;
env.TS=param2;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = dbSupporto.allineamento.find({"tenantCode":env.TENANT});
if (myAllin.count() == 1) {
  esito = dbSupporto.allineamento.update({"tenantCode":env.TENANT},{"$set":{"last_objectid": ObjectId(env.TS) }},{"upsert":true});
  if (esito.nModified != 1) {
    //printjson(esito);
    throw "Error executing updating on last_objectid "+env.TS+" for tenant "+env.TENANT;
  }
}
else {
  // tenant does not exists: throw an error
  throw "Error: tenant "+env.TENANT+" not found executing updating on last_objectid "+env.TS;
}

