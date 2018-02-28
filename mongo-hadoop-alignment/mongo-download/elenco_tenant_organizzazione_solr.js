// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet elenco_tenant.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, {tenantCode : 1, organizationCode : 1, dataSolrCollectionName : 1, measuresSolrCollectionName : 1, socialSolrCollectionName : 1, mediaSolrCollectionName : 1});
while (elencoTenant.hasNext()) {
  myTenant = elencoTenant.next();
  codiceTenant = myTenant.tenantCode;
  codiceOrg    = myTenant.organizationCode;
  
  dataSolrCollectionName = myTenant.dataSolrCollectionName;
  measuresSolrCollectionName = myTenant.measuresSolrCollectionName;
  socialSolrCollectionName = myTenant.socialSolrCollectionName;
  mediaSolrCollectionName = myTenant.mediaSolrCollectionName;
  
  if (dataSolrCollectionName != undefined)  
    print(codiceTenant + ";" + codiceOrg + ";" + dataSolrCollectionName + ";" + measuresSolrCollectionName + ";" + socialSolrCollectionName 
  + ";" + mediaSolrCollectionName);
}
