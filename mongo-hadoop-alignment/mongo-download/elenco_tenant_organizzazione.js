// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet elenco_tenant.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, {tenantCode : 1, organizationCode : 1});
while (elencoTenant.hasNext()) {
  myTenant = elencoTenant.next();
  codiceTenant = myTenant.tenantCode;
  codiceOrg    = myTenant.organizationCode;
  print(codiceTenant + ";" + codiceOrg);
}
