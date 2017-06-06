// lanciare con
// mongo DB_SUPPORT -u cep -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" leggi_objectid.js
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = dbSupporto.allineamento.find({"tenantCode":env.TENANT},{"last_objectid" : 1})
if (myAllin.count() == 1) {
  if (myAllin.hasNext()) {
    lastTS = myAllin.next().last_objectid.valueOf();
    newTS = ObjectId().valueOf();
    print(lastTS+";"+newTS);
  }
  else {
    // error reading tenant data
    throw "Error while reading tenant data "+env.TENANT;
  }
}
else {
  // tenant does not exists: lets create it
  dbSupporto.allineamento.insert({
    "tenantCode": env.TENANT,
    "last_objectid": ObjectId("000000000000000000000000"),
    "locked": 0
    });
    lastTS = ObjectId("000000000000000000000000").valueOf();
    newTS = ObjectId().valueOf();
    print(lastTS+";"+newTS);
}

