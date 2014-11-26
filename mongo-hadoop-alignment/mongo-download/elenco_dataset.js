// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" elenco_dataset.js
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoDataset = db.metadata.find({"configData.tenantCode":env.TENANT});
infoDataset = "";
while (elencoDataset.hasNext()) {
  myDataset = elencoDataset.next();
  //printjson(myDataset.datasetCode);
  //printjson(myDataset.dataSetVersion);
  arrFields = myDataset.info.fields;
  elencoFields=""
  for (i=0;i<arrFields.length;i++) {
    elencoFields=elencoFields+arrFields[i].fieldName+",";
  }
  // elimina l'ultima virgola
  elencoFields=elencoFields.replace(new RegExp("[,]+$"), "");
  //print("campi:"+elencoFields);
  infoDataset = myDataset.datasetCode+";"+myDataset.idDataset+";"+myDataset.datasetVersion+";"+myDataset.configData.database+";"
      +myDataset.configData.collection+";"+myDataset.configData.tenantCode+";"+myDataset.info.visibility+";"+elencoFields;
  print(infoDataset);
}
