// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet elenco_coll_solr_osl.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, 
		{dataSolrCollectionName:1, measuresSolrCollectionName:1, mediaSolrCollectionName:1, socialSolrCollectionName:1});
while (elencoTenant.hasNext()) {
  t = elencoTenant.next();
  if(t.dataSolrCollectionName != undefined)
	  print(t.dataSolrCollectionName.toLowerCase() + ";" + t.measuresSolrCollectionName.toLowerCase() + ";" + 
			  t.mediaSolrCollectionName.toLowerCase() + ";" + t.socialSolrCollectionName.toLowerCase());
}
