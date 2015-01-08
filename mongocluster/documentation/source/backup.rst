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

Start Balancer
--------------

Al termine del backup è necessario ricordarsi di riavviare il balancer con::

    sh.setBalancerState(true)
    sh.startBalancer()

Se il balancer non viene riavviato il sistema continuerà a funzionare, ma lo sharding
sarà di fatto disattivato.
