======================
Java driver assessment
======================

Java Driver compatibility with MongoDB
======================================
La versione 2.13 del driver JAVA è dichiarato essere fully compatible con MongoDB 3.0. http://docs.mongodb.org/ecosystem/drivers/driver-compatibility-reference/#reference-compatibility-mongodb-java

Progetti in assesment
=====================

I progetti presi in considerazione sono:

* yucca-dataservice
* yucca-fabriccontroller
* yucca-realtime
* yucca-storage

Classi verificate
=================

Nei progetti yucca le classi usate del driver JAVA mongodb sono le seguenti:

* AggregationOptions;
* BasicDBList;
* BasicDBObject;
* BasicDBObjectBuilder;
* BulkWriteOperation;
* BulkWriteResult;
* Cursor;
* DB;
* DBCollection;
* DBCursor;
* DBObject;
* DBRef;
* MongoClient;
* MongoCredential;
* ServerAddress;
* WriteConcern;
* WriteResult;

Deprecation list
================
Nessuna delle deprecation riportate nella versione 2.11.4 e 2.12 affligge un cambiamento nella 2.13.
Il metodo per il controllo delle deprecation è stato fatto, cercando l'utilizzo delle classi con deprecation warning nei progetti yucca, dopodiché è stato valutato l'utilizzo di queste classi se fossero stati usati methods o field che hanno il deprecation warning.
Nella versione 2.12 del driver java le seguenti componenti hanno dei deprecation warning

* DB, nessuno dei methodi e dei field in deprecation è stato utilizzato nei progetti yucca
* DBCollection, nessuno dei methodi e dei field in deprecation è stato utilizzato nei progetti yucca
* DBCursor, nessuno dei methodi e dei field in deprecation è stato utilizzato nei progetti yucca
* WriteConcern, nessuno dei methodi e dei field in deprecation è stato utilizzato nei progetti yucca
* WriteResult, nessuno dei methodi e dei field in deprecation è stato utilizzato nei progetti yucca

La lista dei deprecation valutati sono presi dalla documentazione ufficiale presente su: http://api.mongodb.org/java
presente per tutte le versioni del driver java

es::
    grep 'getSizes\|numGetMores\|slaveOk' $(grep -ro "import com.mongodb.DBCursor" yucca-*|sort|uniq|cut -f 1 -d :)

Autenticazione con mongodb 3.0
==============================

Per l'utilizzo dell'authenticazione con MongoDB 3.0 è possibile continuare ad utilizzare MONGODB-CR senza cambiare il codice di authenticazione con il driver 2.13.
Nel caso si volesse invece passare al nuovo meccanismo SCRAM-SHA-1 è necessario rimpiazzare gli utilizzi di `MongoCredential#createMongoCRCredential <http://api.mongodb.org/java/2.13/com/mongodb/MongoCredential.html#createMongoCRCredential(java.lang.String,%20java.lang.String,%20char%5B%5D)>`_ con `MongoCredential#createCredential <http://api.mongodb.org/java/2.13/com/mongodb/MongoCredential.html#createCredential(java.lang.String,%20java.lang.String,%20char%5B%5D)>`_

Benefici
========

Il driver 2.13 ha il grande pregio di essere compatibile con la versione 3.0 di mongodb ed è l'ultima release in rilascio per quanto riguarda la versione 2.x dei driver java. In più una grande feature nuova è il supporto per il nuovo meccanismo di autenticazione che viene usato in mongodb 3.0

Ci sono bugfix e improvement più o meno importanti che affliggono le versioni precedenti ma che sono state corrette nella 2.13.
La lista aggiornata delle migliorie e dei bug è visionabile su:

 * `Improvements <https://jira.mongodb.org/issues/?jql=project%20%3D%20JAVA%20AND%20issuetype%20%3D%20Improvement%20AND%20fixVersion%20%3D%20%222.13.0%22>`_
 * `Bugs <https://jira.mongodb.org/issues/?jql=project%20%3D%20JAVA%20AND%20issuetype%20%3D%20Bug%20AND%20fixVersion%20%3D%20%222.13.0%22>`_


Conclusioni
===========
È possibile e consigliato rimpiazzare il driver nei progetti sopracitati con la versione 2.13 per renderli compatibili con la versione 3.0 di mongodb senza particolari modifiche al codice e senza speciali accorgimenti.


