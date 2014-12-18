// lanciare con
// mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin creaTenant.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, {tenantName : 1});
while (elencoTenant.hasNext()) {
  myTenant = elencoTenant.next();
  nomeTenant = myTenant.tenantName;
  dbSupporto = db.getSiblingDB("DB_"+nomeTenant);
  myCollections = dbSupporto.getCollectionNames();
  if (myCollections.indexOf("data") == -1) {
    print("devo creare data nel db DB_"+nomeTenant);
    dbSupporto.createCollection("data");
    dbSupporto.data.ensureIndex({"idDataset":1,"datasetVersion":1});
    dbSupporto.data.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true});
  }
  if (myCollections.indexOf("measures") == -1) {
    print("devo creare measures nel db DB_"+nomeTenant);
    dbSupporto.createCollection("measures");
    dbSupporto.measures.ensureIndex({"idDataset":1,"datasetVersion":1});
    dbSupporto.measures.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true});
  }
  if (myCollections.indexOf("social") == -1) {
    print("devo creare social nel db DB_"+nomeTenant);
    dbSupporto.createCollection("social");
    dbSupporto.social.ensureIndex({"idDataset":1,"datasetVersion":1});
    dbSupporto.social.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true});
  }
  if (myCollections.indexOf("media") == -1) {
    print("devo creare media nel db DB_"+nomeTenant);
    dbSupporto.createCollection("media");
    dbSupporto.media.ensureIndex({"idDataset":1,"datasetVersion":1});
    dbSupporto.media.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true});
  }
  if (myCollections.indexOf("archivedata") == -1) {
    print("devo creare archivedata nel db DB_"+nomeTenant);
    dbSupporto.createCollection("archivedata");
    dbSupporto.archivedata.ensureIndex({"idDataset":1,"datasetVersion":1});
    dbSupporto.archivedata.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true});
  }
  if (myCollections.indexOf("archivemeasures") == -1) {
    print("devo creare archivemeasures nel db DB_"+nomeTenant);
    dbSupporto.createCollection("archivemeasures");
    dbSupporto.archivemeasures.ensureIndex({"idDataset":1,"datasetVersion":1});
    dbSupporto.archivemeasures.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true});
  }
}

