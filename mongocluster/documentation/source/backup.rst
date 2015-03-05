=======================
Cluster Backup Strategy
=======================

Il seguente documento riporta alcuni possibili strategie per la gestione dei backup
in un ambiente MongoDB con Sharding e Repliche attivate.

Lo scopo è definire la migliore politica di backup a caldo per il sistema che sostiene
lo Speed Layer anche sul lungo termine.

.. _oplog-size:

OpLog Size
==========

Una componente fondamentale nella politica di Backup è la definizione della dimensione
dell'oplog. Una volta creato il cluster MongoDB la dimensione dell'oplog non può più
essere cambiata senza sganciare i nodi del replica set.

La stima dell'oplog influisce sul tempo massimo che può essere speso per effettuare il backup,
superato quel tempo i nodi che sono backuppati non potranno più riagganciarsi al replica set
ed andranno riallineati.

In base alla mail del *2014-12-22* in cui si stimavano 500 FPS per 30 progetti si può dedurre
un throughput di dati pari a 15.000 documenti al secondo. Stimando ogni documento di dimensione
~256 bytes (La stima è fatta sulla base del dump fornito che ha ``"avgObjSize" : 240``) si
può evincere che in questo scenario l'oplog deve avere dimensione pari a **225MB al minuto**.

.. _backup-replica:

Replica per Backup
==================

Al fine di poter effettuare interventi di backup con il minimo disservizio è opportuno
predisporre all'interno di tutti i **replica set che fanno parte del cluster**, un nodo
atto solo allo scopo di effettuare i backup.

Se ad esempio il nostro cluster ha due shard, e quindi due replica set ci saranno due nodi
di backup. Uno per ogni replica set.

Questo nodo va impostato a ``priority: 0`` in modo che non sia eletto come Primary Node
all'interno del replicat set e ``hidden: true`` in modo che i client non lo usino per
effettuare queries.

Il fatto che non sia mai toccato dai client né usato dal replica set come primario permette
di spegnere il nodo in qualsiasi momento per poterne effettuare il backup senza alcun
impatto sul cluster che continuerà a funzionare come prima.

Al fine di impostare un nodo come replica per il backup bisogna *connettersi al primary*
del replica set (in caso ci sia più replica set va fatto per ognuno) e lanciato il comando::

    rs.conf()

Il comando stamperà l'array con l'elenco dei nodi membri del replica set. Una volta identificato
l'indice all'interno dell'array del nodo che si vuole rendere di backup lo si può impostare come
modo di backup tramite::

    cfg = rs.conf()
    cfg.members[2].priority = 0
    cfg.members[2].hidden = true
    rs.reconfig(cfg)

.. note::
    Al posto di **2** è necessario usare l'indice effettivo del nodo identificato precedentemente.

Processo di Backup
==================

.. _stop-balancer:

Stop Balancer
-------------

Indipendentemente dalla tecnica di backup che si decide di usare (snapshot LVM, mongodump,
copia delle macchine Virtuali, etc...) è sempre necessario procedere allo stop del balancer
prima di effettuare il backup.

Il balancer può essere fermato connettendosi al cluster tramite mongos::

    use config
    sh.setBalancerState(false)
    sh.stopBalancer()

Prima di procedere al backup è necessario verificare che il balancer sia effettivamente
fermo con::

    !sh.getBalancerState() && !sh.isBalancerRunning()

**Fermare il balancer è obbligatorio** per poter ottenere un backup corretto, se il balancer
non è stato fermato c'è la possibilità di salvare dei dati duplicati e quindi non riuscire
poi a ripristinare il backup.

Backup Config Server
--------------------

Prima di procedere all'effetivo backup dei nodi è necessario effettuare il backup di un config
server. Dato che i config server replicano tutti gli stessi dati è sufficiente backupparne
uno qualsiasi di quelli disponibili.

Per procedere al backup del config server il metodo più sicuro è lanciare ``mongodump``
direttamente sul config server con l'opzione ``--oplog``::

    $ mongodump --oplog

.. note::

    E` importante specificare l'opzione ``--oplog`` al fine di essere certi che anche eventuali
    modifiche che avvengono durante il backup stesso non possano portare ad un backup in stato
    corrotto.

.. note::

    Durante l'esecuzione di ``mongodump`` non deve essere stata lanciato il comando
    ``db.fsyncLock()`` che viene invece usato per il backup delle repliche di backup.
    Altrimenti impedirà a mongodump di leggere il database e procedere con il dump.

Backup Shard Members
--------------------

Questa procedura va effetuata *per ognuno dei nodi di* :ref:`backup-replica`.

Collegandosi ad ogni nodo di :ref:`backup-replica` è necessario procedere
allo stop dei nodi con::
    
    use admin
    db.shutdownServer({timeoutSecs: 60})

A questo punto una volta terminato lo shutdown si può procedere al backup dei dati stessi
lanciando sul server il comando::

    $ mongodump --journal --dbpath /data/db/ --out /data/backup/

Dove ``/data/db`` è la directory ove il nodo salva i dati effettivi e ``/data/backup`` è
la directory ove si vuole venga generato il backup.

Una volta terminato il backup si può procedere semplicemente al riavvio del processo
mongodb sul nodo.

.. note::

    E` importante che il tempo speso per il backup sia inferiore del tempo massimo reso
    disponibile dalla :ref:`oplog-size` altrimenti il nodo non sarà più in grado
    di riagganciarsi al replica set una volta terminato il backup.

.. _start-balancer:

Start Balancer
--------------

Al termine del backup è necessario ricordarsi di riavviare il balancer con::

    sh.setBalancerState(true)
    sh.startBalancer()

Se il balancer non viene riavviato il sistema continuerà a funzionare, ma lo sharding
sarà di fatto disattivato.

Restore dei Dati
================

Restore di un singolo dato
--------------------------

Il dump del database salva lo stato di tutti i DB e le collections in una struttura
ad albero sul file system corrispondente alla struttura che hanno nel DBMS stesso::

    .
    ├── admin
    │   ├── system.indexes.bson
    │   ├── system.users.bson
    │   ├── system.users.metadata.json
    │   ├── system.version.bson
    │   └── system.version.metadata.json
    ├── DB_smartowear
    │   ├── archivedata.bson
    │   ├── archivedata.metadata.json
    │   ├── archivemeasures.bson
    │   ├── archivemeasures.metadata.json
    │   ├── data.bson
    │   ├── data.metadata.json
    │   ├── measures.bson
    │   ├── measures.metadata.json
    │   ├── media.bson
    │   ├── media.metadata.json
    │   ├── social.bson
    │   ├── social.metadata.json
    │   └── system.indexes.bson
    ├── DB_SUPPORT
    │   ├── allineamento.bson
    │   ├── allineamento.metadata.json
    │   ├── api.bson
    │   ├── api.metadata.json
    │   ├── metadata.bson
    │   ├── metadata.metadata.json
    │   ├── statistics.bson
    │   ├── statistics.metadata.json
    │   ├── stream.bson
    │   ├── stream.metadata.json
    │   ├── system.indexes.bson
    │   ├── tenant.bson
    │   └── tenant.metadata.json

Ad ogni directory corrisponde un ``db``, ad ogni file corrisponde una ``collection``.
Avendo accesso al dump del database in formato ``mongodump`` è possibile visualizzare
lo stato di un singolo documento all'interno del dump con il comando::

    $ bsondump tenant.bson --filter '{"idTenant": 20}'
    { "_id" : ObjectId( "547ee7cd84aed9afa0584cfc" ), "idTenant" : 20, "tenantName" : "ondeuwc", "tenantDescription" : "ondeuwc", "tenantCode" : "ondeuwc", "dataCollectionName" : "data", "dataCollectionDb" : "DB_ondeuwc", "measuresCollectionName" : "measures", "measuresCollectionDb" : "DB_ondeuwc", "socialCollectionName" : "social", "socialCollectionDb" : "DB_ondeuwc", "mediaCollectionName" : "media", "mediaCollectionDb" : "DB_ondeuwc", "archiveDataCollectionName" : "archivedata", "archiveDataCollectionDb" : "DB_ondeuwc", "archiveMeasuresCollectionName" : "archivemeasures", "archiveMeasuresCollectionDb" : "DB_ondeuwc" }
    17 objects found
    1 objects processed

All'interno del parametro ``filter`` è possibile specificare la query da eseguire
per filtrare i documenti all'interno del dump e recuperare solo il documento corretto.

Nel caso in cui si trattasse di un nodo di uno **shard** il documento potrebbe non essere
presente nel dump e bisogna procedere a cercarno anche nei backup degli altri nodi.

Questo procedimento permette il ripristino di uno o più documenti senza bisogno di procedere al
ripristino dell'intero database.

.. _recover-replicaset:

Restore del ReplicaSet
----------------------

Qualora si volesse procedere al restore dell'intero replicaSet è necessario procedere alla
configurazione da 0 del nodo primario in cui poi vanno importati i dati con il comando
``mongorestore``.

Una volta completata l'importazione dei dati, **avviare il nodo come replica singola**::

    $ mongod --replSet REPLICASET_NAME

E collegandosi al nodo, inizializzare quindi il replicaSet::

    > rs.initiate()

Questo inizializzerà un *oplog* e ci permetterà di usare il nodo come sorgente
da cui copiare i dati per gli sltri nodi del replicaset.

.. _recover-replica-secondaries:

Restore dei nodi Secondary
~~~~~~~~~~~~~~~~~~~~~~~~~~

Successivamente procedere al lock del nodo da cui si copiano i dati tramite il comando::

    > db.fsyncLock()

A questo punto si può procedere alla copia della directory in cui il nodo salva il dati
(di default ``/data/db``) su ognuno dei sistemi che saranno membri dello stesso replica set.

Una volta completata la copia sui nodi secondary procedere allo sblocco del nodo da
cui si sono copiati i dati con il comando::

    > db.fsyncUnlock()
    { "ok" : 1, "info" : "unlock completed" }

A questo punto si può procedere all'avvio del nuovo nodo::

    $ mongod --replSet REPLICASET_NAME

e collegandosi al primary, alla loro aggiunta al replica set::

    > rs.add("LPulsar:27019")
    { "ok" : 1 }

Restore di un Nodo del replicaSet
---------------------------------

Per il restore di un singolo nodo del replicaSet, il processo di ripristino dal backup
richiede la modifica manuale dell'OpLog.

Per questa ragione si consiglia di non procedere dal backup, ma di copiare i dati da uno
dei nodi **secondary** ancora funzionanti tramite gli step descritti nel
:ref:`recover-replica-secondaries`.

Restore dei ConfigServer
------------------------

Restore di un singolo config
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Nel caso di ripristino di un solo config server, come per il restore di un nodo del replicaSet è
consigliato procedere copiando i dati da un altro config server tramite il processo:

    * ``db.fsyncUnlock()``
    * copia di ``/data/db``
    * ``db.fsyncUnlock()``

come descritto in :ref:`recover-replica-secondaries`.

Prima di effettuare la procedura è consiglibile spegnere il bilanciatore con i comandi descritti
in :ref:`stop-balancer` e successivamente riaccenderlo con i comandi descritti in
:ref:`start-balancer`. Comunque il bilanciamento non sarebbe potuto procedere a causa del
comando ``db.fsyncLock()`` che viene lanciato sul config server da cui si vogliono copiare i dati.

Successivamente se è cambiato l'indirizza delle macchina andrà aggiornata anche l'opzione
``configDB`` nella configurazione dei *mongos* sostituendo il nuovo config.

Restore di tutti i config
~~~~~~~~~~~~~~~~~~~~~~~~~

Se invece devono essere ripristinati tutti e tre i config server si può procedere all'import
dei dati dal backup effettuato con ``mongodump`` per ognuno dei config server lanciando
suoi config server stessi::

    $ mongorestore --port 63000 --oplogReplay config_backup

Successivamente se è cambiato l'indirizzo delle macchine andrà aggiornata anche l'opzione
``configDB`` nella configurazione dei *mongos* sostituendo il nuovo config.

Restore del Cluster
-------------------

Al fine di ripristinare lo stato dell'intero Cluster è necessario disporre del backup di:

    * Un nodo per ognuno dei replicaSet membri dello shard
    * Un *Config* server

Supponendo che i backup dei tutti i nodi siano stati ottenuti con la procedura descritta
in questo documento e quindi con il comando ``mongodump --oplog`` è possibile ripristinare
lo stato di tutto il cluster procedendo nel seguente modo:

    1. Ripristinare dal backup del *replicaSet1* il primo replicaset seguendo le
       istruzioni indicate in :ref:`recover-replicaset`.
    2. Ripristinare dal backup del *replicaSet2* il secondo replicaset seguendo le
       istruzioni indicate in :ref:`recover-replicaset`.
    3. Se ci sono ulteriori shard per ognuno di essi ripete i punti 1/2
    4. Procedere al ripristino di *1 configserver* tramite ``mongorestore``::

        $ mongod --configsvr --port 63000
        $ mongorestore --port 63000 --oplogReplay config_backup

    5. Collegarsi al configserver e procedere all'aggiornamento degli ip dei replicaset
       qualora essi fossero cambiati::

        configsvr> use config
        configsvr> db.shards.find().pretty()
        {
            "_id" : "replshard1",
            "host" : "replshard1/10.0.1.10:10000,10.0.1.11:11000,10.0.1.12:12000"
        }
        {
            "_id" : "replshard2",
            "host" : "replshard2/10.0.1.20:20000,10.0.1.21:21000,10.0.1.22:22000"
        }

        configsvr> db.shards.update({"_id":"replshard1"},  {$set:{"host": 127.0.0.1:61000}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

        configsvr> db.shards.update({"_id":"replshard2"},  {$set:{"host": "127.0.0.1:62000"}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

       .. note::
            al posto di ``127.0.0.1:61000`` sarà necessario indicare gli ip dei membri del
            replicaSet nel formato: ``replicaSetName/IP1:PORT1,IP2:PORT2,IP3:PORT3``
    6. A questo punto è possibile copiare i dati del config server su altri 2 nodi
       così da tornare nello stato in cui sono presenti 3 config server seguendo le
       istruzioni indicate su :ref:`recover-replica-secondaries`.
    7. Una volta terminato il ripristino dei config server e delle repliche è possibile
       avviare i mongos affinché si aggancino ai nuovi config server e riprendere l'uso
       del cluster.


Restore del cluster su una singola istanza
------------------------------------------

Nal claso in cui si voglia ripristinare l'intero cluster su una singola istanza è necessario:

    1. avviare una nuova istanza ``mongod``::
    
        $ mongod --dbpath /data/tmp_bkp/ --port 30000
        
    2. ripristinare ogni dump (1 per shard)::
    
        $ mongorestore --port 30000 speed1bkp/
        $ mongorestore --port 30000 speed2bkp/
        $ mongorestore --port 30000 speed3bkp/
        
        