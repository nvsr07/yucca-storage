tenants = db.tenant.find({}, {_id:0, tenantCode : 1, organizationCode : 1}).toArray();
printjson(tenants);