// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet elenco_tenant.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, {dataPhoenixTableName:1, dataPhoenixSchemaName:1, measuresPhoenixTableName:1, measuresPhoenixSchemaName:1, 
	mediaPhoenixTableName:1, mediaPhoenixSchemaName:1, socialPhoenixTableName:1, socialPhoenixSchemaName:1, 
	dataSolrCollectionName:1, measuresSolrCollectionName:1, mediaSolrCollectionName:1, socialSolrCollectionName:1});
while (elencoTenant.hasNext()) {
  t = elencoTenant.next();
  print(t.dataPhoenixTableName + ";" + t.dataPhoenixSchemaName + ";" + t.measuresPhoenixTableName + ";" + t.measuresPhoenixSchemaName + ";" + 
		t.mediaPhoenixTableName + ";" + t.mediaPhoenixSchemaName + ";" + t.socialPhoenixTableName + ";" + t.socialPhoenixSchemaName + ";" + 
		t.dataSolrCollectionName + ";" + t.measuresSolrCollectionName + ";" + t.mediaSolrCollectionName + ";" + t.socialSolrCollectionName);
}