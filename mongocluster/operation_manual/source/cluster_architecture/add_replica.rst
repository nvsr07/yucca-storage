.. _add_replica:

=====================================
Aggiunta di un nodo ad un replica set
=====================================

Questo capitolo parla delle operazioni da effettuare per aggiungere un nodo ad un 
:ref:`replica_set` già esistente, è un'operazione che può essere eseguita "a caldo" senza
necessità di dare disservizio.

Per comodità il nuovo nodo verrà chiamato ``sdnet-speed4.sdp.csi.it`` e sarà aggiunto al 
:ref:`replica_set` ``speed0``.
Nel caso in cui si voglia aggiungere un nodo con un host diverso, o nel caso in cui si voglia 
aggiungere il nodo ad un differente :ref:`replica_set` sarà necessario utilizzare i nomi corretti.

Downlaod dei file necessari
===========================

Collegarsi tramite ssh al nuovo nodo::
    
    $ ssh sdnet-speed4.sdp.csi.it

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

Restore dei dati
================

*Nel caso in cui si sta inizilizzando un nuovo :ref:`cluster` da 0 questo paragrafo si può 
saltare.*

Per popolare il nuovo nodo con i dati esistono 2 possibilità:

- aggiungerlo al :ref:`replica_set` ed aspettare che venga eseguita la sincronizzazione da 0
- ripristinare i dati presi da un altro nodo

Vista la velocità e visto l'utilizzo di un nodo del :ref:`replica_set` utilizzato esclusivamente 
per il backup si è deciso di utilizzare la seconda possibilità.

Scaricare la cartella dei dati prendendola dal nodo di backup (``sdnet-speed-restore``)::

    $ sudo scp -r sdnet-speed-restore.sdp.csi.it:/backup_folder \
      /data/mongodb/data/mongod-wiredTiger
    
Lanciare l'istanza mongod::
    
    $ sudo /etc/init.d/mongod start

Aggiunta al Replica Set
=======================

Una volta lanciato il sever è necessario aggiungere al :ref:`replica_set` il nuovo nodo appena
configurato, queste operazioni vanno eseguite sul :ref:`primary` del :ref:`replica_set`.

Collegarsi al nodo ``sdnet-speed1.sdp.csi.it`` e verificare che sia :ref:`primary`::
    
    $ ssh sdnet-speed1.sdp.csi.it
    $ mongo --port 27018
    > use admin
    > db.auth( |db_user| , |db_password| )
    > db.isMaster()


Nel caso in cui sia :ref:`secondary` provare con gli host ``sdnet-speed2.sdp.csi.it`` e 
``sdnet-speed3.sdp.csi.it``

Una volta collegati al corretto :ref:`primary` aggiungere il nuovo nodo al :ref:`replica_set`::

    > rs.add("sdnet-speed4.sdp.csi.it:27018")
    
Verificare che il nuovo nodo sia stato correttamente aggiunto::

    > rs.conf()

Il ``JSON`` di output generato ha una chiave ``members`` in cui è presente la lista di tutti i 
nodi del :ref:`replica_set`, verificare che sia presente l'host del nuovo nodo appena aggiunto 
(``sdnet-speed4.sdp.csi.it``).




