// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet elenco_tenant.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, {dataCollectionDb:1, dataCollectionName:1, measuresCollectionDb:1, measuresCollectionName:1, 
	mediaCollectionDb:1, mediaCollectionName:1, socialCollectionDb:1, socialCollectionName:1, 
	dataSolrCollectionName:1, measuresSolrCollectionName:1, mediaSolrCollectionName:1, socialSolrCollectionName:1});
while (elencoTenant.hasNext()) {
  t = elencoTenant.next();
  print(t.dataCollectionDb + ";" + t.dataCollectionName + ";" + t.measuresCollectionDb + ";" + t.measuresCollectionName + ";" + 
		t.mediaCollectionDb + ";" + t.mediaCollectionName + ";" + t.socialCollectionDb + ";" + t.socialCollectionName + ";" + 
		t.dataSolrCollectionName + ";" + t.measuresSolrCollectionName + ";" + t.mediaSolrCollectionName + ";" + t.socialSolrCollectionName);
}
