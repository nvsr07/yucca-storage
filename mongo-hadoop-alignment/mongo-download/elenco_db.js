elenco = db.adminCommand('listDatabases');
elencoDb = elenco.databases
myReg = new RegExp('^DB_');
Object.keys(elencoDb).forEach(function (key) { 
  var myDB = elencoDb[key];  
  if (myReg.test(myDB.name) && myDB.name != "DB_SUPPORT") {
    printjson(myDB.name);
    db = db.getSiblingDB(myDB.name);
    printjson(db.getCollectionNames());
    elencoColl = db.getCollectionNames();
  } 
})

