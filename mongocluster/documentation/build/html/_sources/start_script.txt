===============
Script di avvio
===============

Gli script di avvio sono disponibili nella directory ``start_script/scripts``.

Servizi
=======

* ``mongoconf`` - Config server
* ``mongod`` - Nodo del replica set
* ``mongos`` - router mongos

Vagrant
=======

L'ambiente è stato testato con un cluster virtuale di 3 server (centos 6.5), per
eseguire la prima installazione utilizzare il comando ``vagrant up``.
In seguito è possibile utilizzare ``vagrant restart`` per riavviare il cluster
``vagrant halt`` per spegnerlo e ``vagrant up`` per riaccenderlo

alla prima installazione vengono:

* copiati i file di utilità (supporting_files) all'interno della cartella
  ``/home/vagrant/``
* installati e registrati in chkconfig i servizi ``mongos``, ``mongod`` e
  ``mongoconf``
* lanciati ``mongod`` e ``mongoconf``
* inizializzato il replicaset tramite l'utility *init_replica.js*

Il servizio ``mongos`` dovrà essere lanciato manualmente in ogni nodo in quanto
al primo lancio è necessario che i config server siano disponibili, così
come l'aggiunta del replica set alla shard utilizzando l' utility
**add_shards.js** ::

    $ sudo service mongos start
    $ mongo 10.102.67.53:20000/admin /home/vagrant/add_shards.js

configuration_files
===================

* ``mongoconf.conf``
* ``mongod.conf``
* ``mongos.conf``

File di configurazione per i 3 servizi, tutte le configurazionu relative ai nodi
del cluster sono qui presenti

supporting_files
================

* **init_replica.js** script di inizializzazione della replica
* **add_shards.js** script di aggiunta del replica set al cluster
* **mongodb.repo** file contenente la configurazione per la repository
    (utilizzato dall'ambiente vagrant per configurarsi).

Script di utilità utilizzati per la prima installazione del cluster

scripts
=======

* ``mongoconf`` - Config server
* ``mongod`` - Nodo del replica set
* ``mongos`` - router mongos

Script di lancio da inserire in init.d e da registrare in chkconfig.
Gli script utilizzati sono quelli forniti da mongodb adattati alle esigenze.

Interdipendenza tra i nodi
--------------------------

In particolare lo script di lancio del ``mongos``, per garantire che il servzio
venga startato con successo (almeno un config deve essere raggiungibile), farà
5 tentativi intervallati da 30 secondi di sleep (il massimo tempo di lancio di
un'istanza mongod è di 60 secondi) in modo che sia disponibile almeno 1 config
server.

Per quanto riguarda ``mongod`` invece, è possibile startare il servizio senza
preoccuparsi di avere gli altri nodi del replica set startati.

start()
-------

* UNIX ulimit Settings::

    ulimit -f unlimited
    ulimit -t unlimited
    ulimit -v unlimited
    ulimit -n 64000
    ulimit -m unlimited
    ulimit -u 32000

parametri settati prima di avviare il servizio, servono a limitare e
controllare l' utilizzo delle risorse del sistema, verificare sulla
documentazione di mongodb i parametri consigliati
(http://docs.mongodb.org/manual/reference/ulimit/#recommended-settings)

NUMA
----

* NUMA Settings::

    NUMACTL_ARGS="--interleave=all"
    if which numactl >/dev/null 2>/dev/null && numactl $NUMACTL_ARGS ls / >/dev/null 2>/dev/null
    then
        NUMACTL="numactl $NUMACTL_ARGS"
    else
        NUMACTL=""
    fi

All'interno degli script di avvio è opportuno disabilita l'allocazione
della memoria usando NUMA in quanto MongoDB non beneficia della prossimità
della memoria alla CPU che ne fa uso, anzi ne è danneggiato in quanto non
è predicibile la CPU che utilizzerà una certa zona di memoria
( http://docs.mongodb.org/manual/administration/production-notes/#mongodb-and-numa-hardware )
