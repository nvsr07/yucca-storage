// lanciare con
// mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin  --quiet --eval "var param1='<TENANT CODE>'" elenco_dataset_json.js
//
var env = {};
env.TENANT=param1;

elencoDataset=db.metadata.find({"configData.tenantCode":env.TENANT, "configData.current":1, "availableSpeed":true, "availableHive":true, $or: [{"configData.deleted":0}, {"configData.deleted":{$exists:false}}]},
		{_id:0,idDataset:1,datasetCode:1,datasetVersion:1,"configData.subtype":1,"info.fields":1,"info.dataDomain":1,"info.codSubDomain":1,dbHiveSchema:1,dbHiveTable:1});

infoDatasets = [];
while (elencoDataset.hasNext()) {
    myDataset = elencoDataset.next();
    infoStream = db.stream.findOne({"configData.tenantCode":env.TENANT,"configData.idDataset":myDataset.idDataset,"configData.datasetVersion":myDataset.datasetVersion});
    if (infoStream == null) {
    	myvirtualEntitySlug="null";
    } else {
    	myvirtualEntitySlug=infoStream.streams.stream.virtualEntitySlug;
  //	se lo slug e vuoto, salto il dataset
    	if (myvirtualEntitySlug == null) { continue; }
    	// sostituisce eventuali caratteri non ammessi con '-'
    	myvirtualEntitySlug=myvirtualEntitySlug.replace(/([^a-zA-Z0-9-_]+)/gi, '-');
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
       		"subtype" : myDataset.configData.subtype
    	},
    	"info" : {
    		"fields" : myDataset.info.fields,
    		"dataDomain" : myDataset.info.dataDomain
    	},
    	"virtualEntitySlug" : myvirtualEntitySlug, 
    	"codSubDomain" : mycodSubDomain,
    	"dbHiveSchema" : myDataset.dbHiveSchema,
    	"dbHiveTable" : myDataset.dbHiveTable
    };
    infoDatasets.push(info);
    }  
         
printjson(infoDatasets);