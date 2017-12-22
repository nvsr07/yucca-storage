// lanciare con
// mongo DB_SUPPORT -u discovery -p XXX --authenticationDatabase admin --quiet elenco_tenant.js
//
dbSupporto = db.getSiblingDB("DB_SUPPORT");
elencoTenant = db.tenant.find({}, {tenantCode : 1, organizationCode : 1, dataPhoenixTableName : 1, dataPhoenixSchemaName : 1,
  measuresPhoenixTableName : 1, measuresPhoenixSchemaName : 1, socialPhoenixTableName : 1, socialPhoenixSchemaName : 1,
  mediaPhoenixTableName : 1, mediaPhoenixSchemaName : 1});
while (elencoTenant.hasNext()) {
  myTenant = elencoTenant.next();
  codiceTenant = myTenant.tenantCode;
  codiceOrg    = myTenant.organizationCode;
  
  phoenixTableData = myTenant.dataPhoenixTableName + "." + myTenant.dataPhoenixSchemaName;
  phoenixTableMeasures = myTenant.measuresPhoenixTableName + "." + myTenant.measuresPhoenixSchemaName;
  phoenixTableSocial = myTenant.socialPhoenixTableName + "." + myTenant.socialPhoenixSchemaName;
  phoenixTableMedia = myTenant.mediaPhoenixTableName + "." + myTenant.mediaPhoenixSchemaName;
  
  if (myTenant.dataPhoenixTableName != undefined)
    print(codiceTenant + ";" + codiceOrg + ";" + phoenixTableData + ";" + phoenixTableMeasures + ";" + phoenixTableSocial + ";" + phoenixTableMedia);
}
