// lanciare con
// mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'; var param2='<idDATASET>'; var param3='<DATASETVERSION>'" metadati_empty.js
//
var env = {};
env.TenantCode  = param1;
env.idDataset   = param2;
env.datasetVers = param3;
rowTitle="";
rowData="";

print(rowTitle+";"+rowData);
