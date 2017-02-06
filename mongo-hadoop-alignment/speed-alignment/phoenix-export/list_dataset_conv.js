// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet --eval "var param1='<TENANT>'" elenco_dataset.js
// 29-04-2016: aggiunto campi dataDomain,streamCode,virtualEntitySlug nello scarico in posizione 10,11,12
//             esclude i dataset con configData.subtype = "binaryDataset"
//
var env = {};
env.TENANT=param1;
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoDataset = db.metadata.find({"configData.tenantCode":env.TENANT, "configData.subtype":{ $ne : "binaryDataset"}});
infoDatasets = [];
while (elencoDataset.hasNext()) {
    myDataset = elencoDataset.next();
    infoStream = db.stream.findOne({"configData.tenantCode":env.TENANT,"configData.idDataset":myDataset.idDataset,"configData.datasetVersion":myDataset.datasetVersion});
    if (infoStream == null) {
    	myvirtualEntitySlug="null";
    	mystreamCode="null";
    } else {
    	myvirtualEntitySlug=infoStream.streams.stream.virtualEntitySlug;
  //	se lo slug e vuoto, salto il dataset
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
  
    info = {
    	"datasetCode" : myDataset.datasetCode,
    	"idDataset" : myDataset.idDataset,
    	"datasetVersion" : myDataset.datasetVersion,
    	"configData" : {
    		"database" : myDataset.configData.database,
    		"collection" : myDataset.configData.collection,
    		"subtype" : myDataset.configData.subtype,
    		"tenantCode" : myDataset.configData.tenantCode
    	},
    	"info" : {
    		"visibility" : myDataset.info.visibility,
    		"fields" : myDataset.info.fields,
    		"dataDomain" : myDataset.info.dataDomain
    	},
    	"streamCode" : mystreamCode,
    	"virtualEntitySlug" : myvirtualEntitySlug, 
    	"codSubDomain" : mycodSubDomain
    };
    
    if(info.configData.database == null || info.configData.database == undefined)
    	info.configData.database = "DB_" + env.TENANT;
    
    if(info.configData.collection == null || info.configData.collection == undefined)
    	 if (info.configData.subtype == "bulkDataset") 
    		 info.configData.collection = "data";
    	 else if (info.configData.subtype == "socialDataset") 
    		 info.configData.collection = "social";
    	 else 
    		 info.configData.collection = "measures";
    
    infoDatasets.push(info);
        
}
printjson(infoDatasets);
