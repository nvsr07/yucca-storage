// lanciare con
// mongo DB_SUPPORT -u cep -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>';var param2=<timestamp>" update_timestamp.js
//
var env = {};
env.TENANT=param1;
env.TS=param2;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = db.allineamento.find({"tenantCode":env.TENANT});
if (myAllin.count() == 1) {
// while (myAllin.hasNext()) {
//   islocked = myAllin.next().locked;
    esito = db.allineamento.update({"tenantCode":env.TENANT},{"$set":{"last_timestamp":env.TS}},{"upsert":true});
    if (esito.nModified != 1) {
      //printjson(esito);
      throw "Error executing updating on last_timestamp "+env.TS+" for tenant "+env.TENANT;
    }
// }
} 
else {
  // tenant does not exists: throw an error
  throw "Error: tenant "+env.TENANT+" not found executing updating on last_timestamp "+env.TS;
}

