.. _init_replica_set:

==================================
Inizializzazione di un Replica Set
==================================

Questa procedura servirà ogniqualvolta si vorrà aggiungere una nuova :ref:`shard` al 
:ref:`cluster` in quanto una :ref:`shard` deve essere necessariamente un :ref:`replica_set`.

Per comodità il nuovo :ref:`replica_set` verrà chiamato ``speed3``, i relativo host che ne faranno
parte saranno ``sdnet-speed31.sdp.csi.it``, ``sdnet-speed32.sdp.csi.it``, 
``sdnet-speed33.sdp.csi.it``.
Nel caso in cui si voglia aggiungere una shard con un nome diverso, sarà necessario utilizzare i 
nomi corretti.

Downlaod dei file necessari
===========================

Collegarsi tramite ssh al nodo 1 del nuovo :ref:`replica_set`::
    
    $ ssh sdnet-speed31.sdp.csi.it

Creare la cartella necessaria ad ospitare i file::
    
    $ mkdir -p /data/mongodb/conf/
    $ mkdir -p /data/mongodb/data/mongod-wiredTiger
    ...

Scaricare da un nodo già configurato del :ref:`replica_set` i seguenti file: ``mongod`` (script 
di avvio), ``mongod.conf``, ``keyfile``::
    
    $ sudo scp sdnet-speed1.sdp.csi.it:/etc/init.d/mongod /etc/init.d/mongod
    $ sudo scp sdnet-speed1.sdp.csi.it:/data/mongodb/conf/mongod.conf \ 
      /data/mongodb/conf/mongod.conf
    ...
    
Cambio dei parametri di configurazione
======================================

Nel file di configurazione ``mongod.conf`` appena scaricato andare a modificare il valore della
chiave ``replSet`` mettendo il nome del nuovo :ref:`replica_set`::

    replSet=speed0
    
Inizializzazione del Replica Set
================================
    
Lanciare l'istanza ``mongod``::

    $ sudo /etc/init.d/mongod start

Collegarsi al ``mongod`` appena lanciato utilizzando la shell ``mongo``::

    $ mongo --port 27017
    
Inizializzare il :ref:`replica_set`::

    > rs.initiate()

Aggiungeta degli altri nodi
===========================

Per aggiungere gli altri nodi al :ref:`replica_set` seguire la procedura illustrata nel capitolo
:doc:`add_replica`