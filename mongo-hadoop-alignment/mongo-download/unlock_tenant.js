// lanciare con
// mongo DB_SUPPORT -u cep -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" unlock_tenant.js
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = db.allineamento.find({"tenantCode":env.TENANT});
if (myAllin.count() == 1) {
  while (myAllin.hasNext()) {
    islocked = myAllin.next().locked;
    esito = db.allineamento.update({"tenantCode":env.TENANT},{"$set":{"locked":0}},{"upsert":true});
    if (esito.nModified != 1) {
      //printjson(esito);
      throw "Error executing unlocking update on tenant "+env.TENANT;
    }
  }
} 
else {
  // tenant does not exists: nothing to do
}

