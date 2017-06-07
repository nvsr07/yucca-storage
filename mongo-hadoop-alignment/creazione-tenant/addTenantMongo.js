
// aggiunta ruoli per utenti cep e discovery

function stampaRuoli(user) {
ruoli=dbAdmin.getUser(user).roles 
for (i=0;i<ruoli.length;i++) {
     print (ruoli[i].db+"     "+ruoli[i].role)
}
}

function timestamp() {
// Create a date object with the current time
  var now = new Date();
 
// Create an array with the current month, day and time
  var date = [ now.getMonth() + 1, now.getDate(), now.getFullYear() ];
 
// Create an array with the current hour, minute and second
  var time = [ now.getHours(), now.getMinutes(), now.getSeconds() ];
 
// Determine AM or PM suffix based on the hour
  var suffix = ( time[0] < 12 ) ? "AM" : "PM";
 
// Convert hour from military time
  time[0] = ( time[0] < 12 ) ? time[0] : time[0] - 12;
 
// If hour is 0, set it to 12
  time[0] = time[0] || 12;
 
// If seconds and minutes are less than 10, add a zero
  for ( var i = 1; i < 3; i++ ) {
    if ( time[i] < 10 ) {
      time[i] = "0" + time[i];
    }
  }
 
// Return the formatted string
  return date.join("/") + " " + time.join(":") + " " + suffix;
}



print ("#######"+timestamp()+"##### Creazione strutture MongoDb per il tenant "+tenant)

var nomeDb="DB_"+tenant

//conn=new Mongo()
dbAdmin=db.getSiblingDB("admin")
//db.auth("root",rootPwd) 

// Verifica se il tenant esiste già

dbs = dbAdmin.runCommand({listDatabases: 1})
dbNames = []
for (var i in dbs.databases) { dbNames.push(dbs.databases[i].name) }

if (dbNames.indexOf(nomeDb)!=-1) {

print ("\nDDDDDD ERRORE: tenant già presente")

} else {

  print ("\n## OK Il tenant non è presente") 

  // aggiunta ruoli utente cep

  print ("\n##Utente cep, PRIMA:")
  stampaRuoli("cep");

  ruoli=dbAdmin.getUser("cep").roles 
  ruoli.push({"role" : "readWrite","db" : nomeDb}) 
  dbAdmin.grantRolesToUser("cep",ruoli) 
  print ("\n##Utente cep, DOPO:")
  stampaRuoli("cep");


  // aggiunta ruoli utente discovery

  print ("\n##Utente discovery, PRIMA:")

  stampaRuoli("discovery");

  ruoli=dbAdmin.getUser("discovery").roles 
  ruoli.push({"role" : "read","db" : nomeDb}) 
  dbAdmin.grantRolesToUser("discovery",ruoli) 
  print ("\n##Utente discovery, DOPO:")

  stampaRuoli("discovery");


  // creazione del nuovo DB (per il nuovo tenant)

  dbNew=db.getSiblingDB(nomeDb)
  dbNew.createCollection("data") 
  dbNew.data.ensureIndex({"idDataset":1,"datasetVersion":1,"time":1}) 
  dbNew.data.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true}) 
  dbNew.createCollection("measures") 
  dbNew.measures.ensureIndex({"idDataset":1,"datasetVersion":1,"time":1}) 
  dbNew.measures.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true})
  dbNew.measures.ensureIndex({"time":1},{background:true}) 
  dbNew.createCollection("social") 
  dbNew.social.ensureIndex({"idDataset":1,"datasetVersion":1,"time":1}) 
  dbNew.social.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true}) 
  dbNew.social.ensureIndex({"time":1},{background:true}) 
  dbNew.social.ensureIndex({"retweetParentId":1},{background:true}) 
  dbNew.social.ensureIndex({"userId":1},{background:true}) 
  dbNew.createCollection("media"); 
  dbNew.media.ensureIndex({"idDataset":1,"datasetVersion":1,"time":1}) 
  dbNew.media.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1},{sparse:true}) 
  dbNew.createCollection("archivemeasures") 
  dbNew.archivemeasures.ensureIndex({"idDataset":1,"datasetVersion":1,"time":1,"startValidityDate":1}) 
  dbNew.archivemeasures.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1,"startValidityDate":1},{sparse:true}) 
  dbNew.createCollection("archivedata") 
  dbNew.archivedata.ensureIndex({"idDataset":1,"datasetVersion":1,"time":1,"startValidityDate":1}) 
  dbNew.archivedata.ensureIndex({"idxLocation":"2dsphere","idDataset":1,"datasetVersion":1,"startValidityDate":1},{sparse:true})

// configurazione sharding per il nuovo DB 

sh.enableSharding(nomeDb)
sh.shardCollection(nomeDb+".measures", {"idDataset" : 1, "datasetVersion" : 1, "time" : 1 } )




  //verifica creazione strutture DB

  print("\n##Nuovo elenco DB Mongo")
  dbAdmin=db.getSiblingDB("admin")
  dbs = dbAdmin.runCommand({listDatabases: 1})

  for (var i in dbs.databases) { print(dbs.databases[i].name) }


  print ("\n##Verifica creazione strutture DB") 
  dbNew=db.getSiblingDB(nomeDb)
  dbCollectionNames=dbNew.getCollectionNames()
  for (var i  in dbCollectionNames) { print(dbCollectionNames[i]) }

}

print ("#######"+timestamp()+"##### FINE")



