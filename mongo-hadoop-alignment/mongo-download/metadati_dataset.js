// lanciare con
// mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'; var param2='<idDATASET>'; var param3='<DATASETVERSION>'" metadati_dataset.js
//
var env = {};
env.TenantCode  = param1;
env.idDataset   = param2;
env.datasetVers = param3;
rowTitle="";
rowData="";

dbSupporto = db.getSiblingDB("DB_SUPPORT");

myTenant = dbSupporto.tenant.findOne({tenantCode : env.TenantCode});
if (!myTenant) {
  // tenant does not exists: something went wrong
  throw "Error while reading tenant "+env.TenantCode;
}

myStream = dbSupporto.stream.findOne({"configData.idDataset" : env.idDataset , "configData.datasetVersion" : env.datasetVers});
if (!myStream) {
  // stream does not exists: append an empty string
  rowData=rowData+",";
} else {
  rowData=rowData+myStream.streams.stream.virtualEntityName+",";
}
rowTitle=rowTitle+"Sensor.Name,";

myDataset = dbSupporto.metadata.findOne({idDataset : env.idDataset , datasetVersion : env.datasetVers});
if (myDataset) {
  // fields list
  arrFields = myDataset.info.fields;
  for (i=0;i<arrFields.length;i++) {
    // fieldAlias
    rowTitle=rowTitle+arrFields[i].fieldName+".fieldAlias"+",";
    rowData=rowData+arrFields[i].fieldAlias+",";
    // measureUnit
    rowTitle=rowTitle+arrFields[i].fieldName+".measureUnit"+",";
    rowData=rowData+arrFields[i].measureUnit+",";
    // dataType
    rowTitle=rowTitle+arrFields[i].fieldName+".dataType"+",";
    rowData=rowData+arrFields[i].dataType+",";
  }

  // frequency - fps
  rowTitle=rowTitle+"Dataset.frequency,";
  rowData=rowData+myDataset.info.fps+",";

  // tags list
  myArrTags = myDataset.info.tags;
  myTags = "";
  while (myArrTags && myArrTags.length>0) {myElem=myArrTags.pop();myTags=myElem.tagCode+" "+myTags;}
  //myTags=myTags.replace(new RegExp("[-]+$"), "");  // delete the last dash
  rowTitle=rowTitle+"Dataset.Tags,";
  rowData=rowData+myTags+",";

  rowTitle = rowTitle.replace(new RegExp("[,]+$"), "");  // delete the last comma
  rowData = rowData.replace(new RegExp("[,]+$"), "");  // delete the last comma
  print(rowTitle+";"+rowData);
} else {
  // dataset does not exists: something went wrong
  throw "Error while reading dataset id "+env.idDataset+ " - version "+env.datasetVers;
}

