// lanciare con
// mongo DB_SUPPORT -u cep -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" leggi_timestamp.js
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = db.allineamento.find({"tenantCode":env.TENANT},{"last_timestamp" : 1})
if (myAllin.count() == 1) {
  if (myAllin.hasNext()) {
    lastTS = myAllin.next().last_timestamp;
    newTS = new ISODate().valueOf();
    print(lastTS+";"+newTS);
  }
} 
else {
  // tenant does not exists: something went wrong
  throw "Error while reading tenant "+env.TENANT;
}

