// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" elenco_dataset.js
// 29-04-2016: aggiunto campi dataDomain,streamCode,virtualEntitySlug nello scarico in posizione 10,11,12
//             esclude i dataset con configData.subtype = "binaryDataset"
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoDataset = db.metadata.find({"configData.tenantCode":env.TENANT, "configData.subtype":{ $ne : "binaryDataset"}});
infoDataset = "";
while (elencoDataset.hasNext()) {
  myDataset = elencoDataset.next();
  //printjson(myDataset.datasetCode);
  //printjson(myDataset.datasetVersion);
  arrFields = myDataset.info.fields;
  elencoFields=""
//print(myDataset.datasetCode+";"+myDataset.idDataset+";"+myDataset.datasetVersion);
  for (i=0;i<arrFields.length;i++) {
    elencoFields=elencoFields+arrFields[i].fieldName+",";
  }
  // elimina l'ultima virgola da elenco campi
  elencoFields=elencoFields.replace(new RegExp("[,]+$"), "");
  //print("campi:"+elencoFields);
  // legge campo virtualEntitySlug su Stream
  infoStream = db.stream.findOne({"configData.tenantCode":env.TENANT,"configData.idDataset":myDataset.idDataset,"configData.datasetVersion":myDataset.datasetVersion});
  if (infoStream == null) {
      myvirtualEntitySlug="null";
      mystreamCode="null";
  } else {
      myvirtualEntitySlug=infoStream.streams.stream.virtualEntitySlug;
      // se lo slug e vuoto, salto il dataset
      if (myvirtualEntitySlug == null) { continue; }
      // sostituisce eventuali caratteri non ammessi con '-'
      myvirtualEntitySlug=myvirtualEntitySlug.replace(/([^a-zA-Z0-9-_]+)/gi, '-');
      mystreamCode=infoStream.streamCode;
  }
  if (myDataset.info.codSubDomain == null) {
      mycodSubDomain=myDataset.info.dataDomain;
  } else {
      mycodSubDomain=myDataset.info.codSubDomain;
  }
  //
  infoDataset = myDataset.datasetCode+";"+myDataset.idDataset+";"+myDataset.datasetVersion+";"+myDataset.configData.database+";"
      +myDataset.configData.collection+";"+myDataset.configData.subtype+";"+myDataset.configData.tenantCode+";"+myDataset.info.visibility+";"
      +elencoFields+";"+myDataset.info.dataDomain+";"+mystreamCode+";"+myvirtualEntitySlug+";"+mycodSubDomain;
  print(infoDataset);
}
