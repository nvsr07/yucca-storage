// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" elenco_dataset.js
// 29-04-2016: aggiunto campi dataDomain,streamCode,virtualEntitySlug nello scarico in posizione 10,11,12
//             esclude i dataset con configData.subtype = "binaryDataset"
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
env.DATATYPESUFFIXES = {
    'boolean' : '_b',
    'string' : '_s',
    'int' : '_i',
    'long' : '_l',
    'double' : '_d',
    'data' : '_dt',
    'date' : '_dt',
    'datetimeOffset' : '_dt',
    'datetime' : '_dt',
    'dateTime' : '_dt',
    'time' : '_dt',
    'float' : '_f',
    'longitude' : '_d',
    'latitude' : '_d',
    'binary' : '_s',
    'bigdecimal' : '_d'
};
env.DATATYPE2PHOENIX = {
    'boolean' : 'boolean',
    'string' : 'varchar',
    'int' : 'integer',
    'long' : 'bigint',
    'double' : 'double',
    'data' : 'timestamp',
    'date' : 'timestamp',
    'datetimeOffset' : 'timestamp',
    'datetime' : 'timestamp',
    'dateTime' : 'timestamp',
    'time' : 'timestamp',
    'float' : 'float',
    'longitude' : 'double',
    'latitude' : 'double',
    'binary' : 'varchar',
    'bigdecimal' : 'double'
}
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
    elencoFields=elencoFields+arrFields[i].fieldName+env.DATATYPESUFFIXES[arrFields[i].dataType]+",";
    elencoFieldsPhoenix=elencoFieldsPhoenix+arrFields[i].fieldName+env.DATATYPESUFFIXES[arrFields[i].dataType]+" "+env.DATATYPE2PHOENIX[arrFields[i].dataType]+",";
  }
  // elimina l'ultima virgola da elenco campi
  elencoFields=elencoFields.replace(new RegExp("[,]+$"), "");
  elencoFieldsPhoenix=elencoFieldsPhoenix.replace(new RegExp("[,]+$"), "");
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
      +elencoFields+";"+myDataset.info.dataDomain+";"+mystreamCode+";"+myvirtualEntitySlug+";"+mycodSubDomain+";"+elencoFieldsPhoenix;
  print(infoDataset);
}
