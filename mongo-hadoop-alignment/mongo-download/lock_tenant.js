// lanciare con
// mongo DB_SUPPORT -u cep -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" lock_tenant.js
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = db.allineamento.find({"tenantCode":env.TENANT});
if (myAllin.count() == 1) {
  while (myAllin.hasNext()) {
    islocked = myAllin.next().locked;
    //print("islocked:"+islocked);
    if (islocked == 0) {
      print("lock "+env.TENANT);
      esito = db.allineamento.update({"tenantCode":env.TENANT},{"$set":{"locked":1}},{"upsert":true});
      if (esito.nModified != 1) {
        throw "Error executing locking update on allineamento collection";
      }
    }
    else {
      throw "tenant already locked";
    }
  }
} 
else {
  // tenant does not exists: let create it - already locked
  // ISODate("2014-01-01T00:00:00.000Z").valueOf() == 1388534400000
  esito = db.allineamento.insert({"tenantCode":env.TENANT, "last_timestamp": 1388534400000, "locked": 1 });
  if (esito.nInserted != 1) {
        throw "Error executing insert for new tenant "+env.TENANT;
      }
}

