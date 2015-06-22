=============================================
Procedura di Upgrade per MongoDB versione 3.0
=============================================
La documentazione ufficiale inerente all'upgrade di versione si trova qui:

http://docs.mongodb.org/manual/release-notes/3.0-upgrade/

Prerequisiti
============

I prerequisiti per poter procedere all'upgrade sono i seguenti:
    * mongodb almeno versione 2.6
    * authschema almeno versione 3
    
http://docs.mongodb.org/manual/release-notes/3.0-compatibility/

Introduzione
============

La procedura si divide in 4 macro attività da eseguire in ordine::
    * Upgrade dei binari dei mongoconf e dei mongos: 
        * int-sdnet-config1.sdp.csi.it 
        * int-sdnet-config2.sdp.csi.it 
        * int-sdnet-config3.sdp.csi.it
    * Upgrade dei binari per quanto riguarda i Replica Set:
        * int-sdnet-speed1.sdp.csi.it
        * int-sdnet-speed2.sdp.csi.it
        * int-sdnet-speed3.sdp.csi.it
        * int-sdnet-speed11.sdp.csi.it
        * int-sdnet-speed12.sdp.csi.it
        * int-sdnet-speed13.sdp.csi.it
        * int-sdnet-speed21.sdp.csi.it
        * int-sdnet-speed22.sdp.csi.it
        * int-sdnet-speed23.sdp.csi.it
    * Upgrade dello Storage System Engine (wiredTiger) dei mongoconf:
        * int-sdnet-config1.sdp.csi.it 
        * int-sdnet-config2.sdp.csi.it 
        * int-sdnet-config3.sdp.csi.it
    * Upgrade dello Storage System Engine (wiredTiger) dei Replica Set:
        * int-sdnet-speed1.sdp.csi.it
        * int-sdnet-speed2.sdp.csi.it
        * int-sdnet-speed3.sdp.csi.it
        * int-sdnet-speed11.sdp.csi.it
        * int-sdnet-speed12.sdp.csi.it
        * int-sdnet-speed13.sdp.csi.it
        * int-sdnet-speed21.sdp.csi.it
        * int-sdnet-speed22.sdp.csi.it
        * int-sdnet-speed23.sdp.csi.it
        
Upgrade binari config e mongos
==============================

* spegnere il bilanciatore, lanciando dalla macchina sdnet-speed-restore.sdp.csi.it lo script 
  presente nella cartella ``/root/backup``::
    
    ./stop_balancer.sh
        
* verificare che sia spento lanciando lo script presente nella medesima cartella::

    ./status_balancer.sh
    
    
* spegnere i mongos su tutti i nodi del cluster e sugli application server 
  (int-sdnet-mb1.sdp.csi.it, int-sdnet-mb2.sdp.csi.it, int-sdnet-up1.sdp.csi.it, 
  int-sdnet-up2.sdp.csi.it)::
  
    /etc/init.d/mongos stop
    
* Scricare il file di configurazione di un mongos ed editarlo rimuovendo le seguenti 
  configurazioni::
    
    fork=true
    pidfilepath=/var/run/mongodb/mongos.pid
    logpath=/var/log/mongodb/mongos.log
    logappend=true
    
* Scaricare il keyFile e mettere il relativo path all'interno del file di configurazione appena 
  modificato::
  
    keyfile=/tmp/keyfile
  
* Lanciare tramite un mongos 3.0 la procedura di upgrade dei metadati del cluster (sarà presente 
  un mongos 3.0 di integrazione)::

    mongos –f (copia_del_file_in_locale_della_configurazione) --upgrade

    
* l'output di avvenuto upgrade sarà il seguente::

    <timestamp> I SHARDING [mongosMain] MongoS version 3.0.0 starting: ...
    ...
    <timestamp> I SHARDING [mongosMain] starting upgrade of config server from v5 to v6
    <timestamp> I SHARDING [mongosMain] starting next upgrade step from v5 to v6
    <timestamp> I SHARDING [mongosMain] about to log new metadata event: ...
    <timestamp> I SHARDING [mongosMain] checking that version of host ... is compatible with 2.6
    ...
    <timestamp> I SHARDING [mongosMain] upgrade of config server to v6 successful
    ...
    <timestamp> I SHARDING [mongosMain] distributed lock 'configUpgrade/...' unlocked.
    <timestamp> I -        [mongosMain] Config database is at version v6


* upgradare SOLO i mongos su tutti i nodi del cluster e sui nodi applicativi, i nodi dei mongoconf 
  non hanno mongos (copiarlo su tutti i nodi e su repository in rete)::
    
    rpm -Uvh mongodb-enterprise-mongos-3.0.4-1.el6.x86_64.rpm –nodeps

* riavviare i mongos sui nodi del cluster, sui nodi applicativi verranno rilanciati solo a 
  procedura ultimata::
    
    /etc/init.d/mongos start

* Verificare che il cluster sia correttamente funzionante collegandosi ad un mongos::
    
    sh.status()

* upgradare gli ultimi 2 config server (nell'ordine in cui sono presenti alla voce configdb nel file
  di configurazione dei mongos) int-sdnet-config2.sdp.csi.it e int-sdnet-config3.sdp.csi.it::
    
    /etc/init.d/mongoconf stop
    rpm -Uvh mongodb-enterprise-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-mongos-3.0.4-1.el6.x86_64.rpm 
    mongodb-enterprise-server-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-shell-3.0.4-1.el6.x86_64.rpm 
    mongodb-enterprise-tools-3.0.4-1.el6.x86_64.rpm cyrus-sasl-2.1.23-15.el6_6.2.x86_64.rpm 
    cyrus-sasl-gssapi-2.1.23-15.el6_6.2.x86_64.rpm cyrus-sasl-lib-2.1.23-15.el6_6.2.x86_64.rpm 
    cyrus-sasl-plain-2.1.23-15.el6_6.2.x86_64.rpm
    
    /etc/init.d/mongoconf start

* upgradare il config server rimanente int-sdnet-config2.sdp.csi.it::
    
    /etc/init.d/mongoconf stop
    rpm -Uvh mongodb-enterprise-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-mongos-3.0.4-1.el6.x86_64.rpm 
    mongodb-enterprise-server-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-shell-3.0.4-1.el6.x86_64.rpm 
    mongodb-enterprise-tools-3.0.4-1.el6.x86_64.rpm cyrus-sasl-2.1.23-15.el6_6.2.x86_64.rpm 
    cyrus-sasl-gssapi-2.1.23-15.el6_6.2.x86_64.rpm cyrus-sasl-lib-2.1.23-15.el6_6.2.x86_64.rpm 
    cyrus-sasl-plain-2.1.23-15.el6_6.2.x86_64.rpm
    
    /etc/init.d/mongoconf start
    
NON RIACCENDERE IL BILANCIATORE FINO A UPGRADE AVVENUTO ANCHE SUI BINARI DEI REPLICA SET

Upgrade binari Replica Set
==========================

* collegarsi attraverso la shell a ciascuno dei nodi e autenticarsi::
    
    mongo --port 27017
    use admin
    db.auth('user', 'pass')

* rendere primary i nodi "master", per farlo è necessario forzare tutti gli altri nodi a diventare 
  secondary (int-sdnet-speed2.sdp.csi.it int-sdnet-speed3.sdp.csi.it int-sdnet-speed12.sdp.csi.it
  int-sdnet-speed13.sdp.csi.it int-sdnet-speed22.sdp.csi.it int-sdnet-speed23.sdp.csi.it::

    rs.freeze(120)
    rs.stepDown(120)

http://docs.mongodb.org/manual/tutorial/force-member-to-be-primary/

* verificare che siano effettivamente diventati secondary e spegnerli::
    
    rs.status()
    db.shutdownServer()

* come operazione preliminare all'upgrade è necessario effettuare un backup dei seguenti file in 
  quanto l' upgrade li sovrascriverà::

    /etc/init.d/mongod
    /etc/sysconfig/mongod
    
* upgradare i binari::
    
    rpm -Uvh mongodb-enterprise-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-mongos-3.0.4-1.el6.x86_64.rpm 
    mongodb-enterprise-server-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-shell-3.0.4-1.el6.x86_64.rpm
    mongodb-enterprise-tools-3.0.4-1.el6.x86_64.rpm cyrus-sasl-2.1.23-15.el6_6.2.x86_64.rpm
    cyrus-sasl-gssapi-2.1.23-15.el6_6.2.x86_64.rpm cyrus-sasl-lib-2.1.23-15.el6_6.2.x86_64.rpm
    cyrus-sasl-plain-2.1.23-15.el6_6.2.x86_64.rpm

* nel file ``/etc/init.d/mongod`` modificare i valori di ulimit prendendoli dalla nuova copia 
  installata 
  
* ripristinare i 2 file copiati in precedenza (``/etc/init.d/mongod`` ``/etc/sysconfig/mongod``)
    
* avviare uno alla volta i nodi e attendere che riprendano lo status di SECONDARY::

    /etc/init.d/mongod start
    rs.status()
    
* ripetere la procedura con i 3 nodi primary rimasti alla vecchia versione dei binari forzandoli a 
  diventare secondary::

    rs.freeze(120)
    rs.stepDown(120)

* verificare che siano diventati secondary, che ci sia almeno 1 primary nel replica set e spegnerli::
    
    rs.status()
    db.shutdownServer()

    
* upgradare i binari::

    rpm -Uvh mongodb-enterprise-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-mongos-3.0.4-1.el6.x86_64.rpm 
    mongodb-enterprise-server-3.0.4-1.el6.x86_64.rpm mongodb-enterprise-shell-3.0.4-1.el6.x86_64.rpm
    mongodb-enterprise-tools-3.0.4-1.el6.x86_64.rpm cyrus-sasl-2.1.23-15.el6_6.2.x86_64.rpm
    cyrus-sasl-gssapi-2.1.23-15.el6_6.2.x86_64.rpm cyrus-sasl-lib-2.1.23-15.el6_6.2.x86_64.rpm
    cyrus-sasl-plain-2.1.23-15.el6_6.2.x86_64.rpm

* nel file ``/etc/init.d/mongod`` modificare i valori di ulimit prendendoli dalla nuova copia 
  installata 
  
* ripristinare i 2 file copiati in precedenza (``/etc/init.d/mongod`` ``/etc/sysconfig/mongod``)
    
* avviare uno alla volta i nodi e attendere che riprendano lo status di SECONDARY::

    /etc/init.d/mongod start
    rs.status()

* collegarsi a un mongos e riattivare il bilanciatore, verificare che sia acceso e verificare lo
  stato del cluster::
    
    sh.startBalancer()
    sh.getBalancerState()
    sh.status()

* procedere con l'upgrade sulle 4 macchine applicative (int-sdnet-mb1.sdp.csi.it, 
  int-sdnet-mb2.sdp.csi.it, int-sdnet-up1.sdp.csi.it, int-sdnet-up2.sdp.csi.it)
    
    
Upgrade Storage System replica set
==================================

* creare il nuovo repository su tutti i nodi del replicaset, cofigurare i permessi per l'utente 
  mongod::
  
    /data/mongodb/data/wiredTiger-mongod
    chown mongod:mongod /data/mongodb/data/wiredTiger-mongod
    
* spegnere il bilanciatore e verificare che sia spento::
    
    sh.stopBalancer()
    sh.getBalancerState()
    
* rendere primary i nodi "master", per farlo è necessario forzare tutti gli altri nodi a diventare 
  secondary (int-sdnet-speed2.sdp.csi.it int-sdnet-speed3.sdp.csi.it int-sdnet-speed12.sdp.csi.it
  int-sdnet-speed13.sdp.csi.it int-sdnet-speed22.sdp.csi.it int-sdnet-speed23.sdp.csi.it::

    rs.freeze(120)
    rs.stepDown(120)

http://docs.mongodb.org/manual/tutorial/force-member-to-be-primary/

* verificare che siano diventati secondary, che ci sia almeno 1 primary nel replica set e 
  spegnerli::
  
    rs.status()
    db.shutdownServer()
    

* modificare il file di configurazione ``/etc/mongod.conf``, cambiando il dbpath con il path della
  cartella creata in precedenza, inoltre abilitare il nuovo storage system::

    storageEngine=wiredTiger
    dbpath=/data/mongodb/data/wiredTiger-mongod
    
* riavviare i secondary::

    /etc/init.d/mongod start
    
* attendere che il sync iniziale sia terminato, per verificarlo i nodi dovranno essere tornati in 
  status SECONDARY e i tempi dell'oplog devono essere uguali tra i vari nodi di ciascun Replica Set::

    rs.status()
    
* una volta terminato il sync iniziale ripetere la procedura con i 3 primary alla vecchia versione 
  facendo attenzione che siano diventati secondary::

    rs.freeze(120)
    rs.stepDown(120)
    rs.status()
    db.shutdownServer()
    
* modificare il file di configurazione ``/etc/mongod.conf``, cambiando il dbpath con il path della
  cartella creata in precedenza, inoltre abilitare il nuovo storage system::

    storageEngine=wiredTiger
    dbpath=/data/mongodb/data/wiredTiger-mongod

* riavviare i primary::

    /etc/init.d/mongod start
    
* collegarsi a un mongos e riattivare il bilanciatore, verificare che sia acceso e verificare lo
  stato del cluster::
    
    sh.startBalancer()
    sh.getBalancerState()
    sh.status()    

Upgrade Storage System Config server
====================================
    
* creare il nuovo repository su tutti i nodi del replicaset, cofigurare i permessi per l'utente 
  mongod::
  
    /data/mongodb/data/wiredTiger-mongoconf
    chown mongod:mongod /data/mongodb/data/wiredTiger-mongoconf
    
* spegnere il bilanciatore e verificare che sia spento::
    
    sh.stopBalancer()
    sh.getBalancerState()
    
* spegnere l'ultimo config server (int-sdnet-config3.sdp.csi.it)::
    
    /etc/init.d/mongoconf stop

azioni da effettuare sul secondo config server (int-sdnet-config2.sdp.csi.it)
-----------------------------------------------------------------------------

* effettuare il backup del db utilizzando mongodump::
        
    mkdir dump
    mongodump --port 27017 --out dump/
    
* spegnere il servizio mongoconf::
    
    /etc/init.d/mongoconf stop

* modificare il file di configurazione ``/etc/mongoconf.conf``, cambiando il dbpath con il path 
  della cartella creata in precedenza, inoltre abilitare il nuovo storage system::
        
    storageEngine=wiredTiger
    dbpath=/data/mongodb/data/wiredTiger-mongod

* avviare il servizio mongoconf::

    /etc/init.d/mongoconf start

* ripristinare il backup::
    
    mongorestore --port 27017 dump/

* spegnere il servizio mongoconf::
    
    /etc/init.d/mongoconf stop
    
    
azioni da effettuare solo sul terzo config server (int-sdnet-config3.sdp.csi.it)
--------------------------------------------------------------------------------

* avviare il servizio mongoconf fermato in precedenza::

    /etc/init.d/mongoconf start   

* effettuare il backup del db utilizzando mongodump::
        
    mkdir dump
    mongodump --port 27017 --out dump/
    
* spegnere il servizio mongoconf::
    
    /etc/init.d/mongoconf stop

* modificare il file di configurazione ``/etc/mongoconf.conf``, cambiando il dbpath con il path 
  della cartella creata in precedenza, inoltre abilitare il nuovo storage system::
        
    storageEngine=wiredTiger
    dbpath=/data/mongodb/data/wiredTiger-mongod

* avviare il servizio mongoconf::

    /etc/init.d/mongoconf start

* ripristinare il backup::
  
    mongorestore --port 27017 dump/
    
azioni da effettuare sul primo config server (int-sdnet-config1.sdp.csi.it)
---------------------------------------------------------------------------

* effettuare il backup del db utilizzando mongodump::
        
    mkdir dump
    mongodump --port 27017 --out dump/
    
* spegnere il servizio mongoconf::
    
    /etc/init.d/mongoconf stop

* modificare il file di configurazione ``/etc/mongoconf.conf``, cambiando il dbpath con il path 
  della cartella creata in precedenza, inoltre abilitare il nuovo storage system::
        
    storageEngine=wiredTiger
    dbpath=/data/mongodb/data/wiredTiger-mongod

* avviare il servizio mongoconf::

    /etc/init.d/mongoconf start

* ripristinare il backup::
  
    mongorestore --port 27017 dump/
        
* rilanciare il secondo config server fermato in precedenza (int-sdnet-config2.sdp.csi.it)::
    
    sudo service mongoconf start
    
* riattivare il bilanciatore, verificare che sia acceso e verificare lo stato del cluster::

    sh.startBalancer()
    sh.getBalancerState()
    sh.status()
    
    