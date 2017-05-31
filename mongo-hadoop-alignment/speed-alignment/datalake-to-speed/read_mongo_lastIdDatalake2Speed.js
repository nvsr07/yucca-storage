//lanciare con
//mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" read_mongo_lastIdDatalake2Speed.js
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
myAllin = dbSupporto.allineamento.find({"tenantCode":env.TENANT}, {"last_datalake2speed_objectid":1});
lastId = "000000000000000000000000";
if (myAllin.count() == 1 && myAllin.last_datalake2speed_objectid != undefined)
    	lastId = myAllin.last_datalake2speed_objectid;
print(lastId);