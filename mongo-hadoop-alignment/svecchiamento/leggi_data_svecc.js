// lanciare con
// mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<TENANT>';" leggi_data_svecc.js
//
var env = {};
env.TenantCode=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = dbSupporto.tenant.findOne({"tenantCode":env.TenantCode});
if (!myAllin) {
  // tenant does not exists: something went wrong
  throw "Error while reading tenant "+env.TenantCode;
}
else {
  gg = myAllin.giorniOnLine;
  ddToday = new Date();
  ddThreshold = Math.floor((ddToday - (gg * 24 * 3600 * 1000))/1000);
  //print("ddThreshold:"+ddThreshold+" - stringato:"+ddThreshold.toString(16).pad(24,true,"0"));
  last_objectid = ObjectId(ddThreshold.toString(16).pad(24,true,"0"));
  print(last_objectid.str);
  //print("gg:"+gg+" - ddThreshold:"+ddThreshold+" / "+ddThreshold.toString(16)+" - "+last_objectid.getTimestamp());
} 

