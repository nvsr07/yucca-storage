Note per la gestione del cluster MongoDB
========================================

Di seguito sono riportate alcune note e procedure di utilità durante la gestione di un cluster
mongodb.

OpLog Consumption Estimation
----------------------------

La **OpLog Window** è la definizione di quanto tempo ci va per fare un giro completo
dell'oplog. Quindi quanto tempo un nodo del sistema può restare sganciato dal replica set
essendo poi capace di riagganciarsi.

Qualora l'oplog non sia ancora stato consumato interamente si può calcolare
le informazioni riguardanti il consumo ed il tempo rimanente dello stesso tramite
il seguente script::

    #!/bin/bash
    MONGOPATH="/opt/mongodb/bin/mongo --port 27018"

    $MONGOPATH --eval '
        var oplog_speed = db.getReplicationInfo()["usedMB"] / db.getReplicationInfo()["timeDiff"];
        var oplog_size = db.getReplicationInfo()["logSizeMB"];
        var oplog_size_left = oplog_size - db.getReplicationInfo()["usedMB"];
        var oplog_time = db.getReplicationInfo()["timeDiffHours"];

        print("");
        print("OpLog Window: ", (oplog_size / oplog_speed / 3600).toFixed(2), "Hours");
        print("OpLog Time: ", oplog_time, "Hours");
        print("OpLog Size: ", oplog_size.toFixed(2), "MB");
        print("OpLog Size Left: ", oplog_size_left.toFixed(2), "MB");
        print("OpLog Throughput: ", oplog_speed.toFixed(3), "MB/sec");
        print("OpLog TimeLeft: ", (oplog_size_left / oplog_speed / 3600).toFixed(2), "Hours Left");
    '

I risultati riportati sono:

    * OpLog Window -> Quanto tempo è stimato per un giro completo di oplog.
    * OpLog Time -> Quanto tempo è registrato nell'oplog
      (Se l'oplog è già consumato questo valore corrisponde alla **OpLog Window**).
    * OpLog Size -> La dimensione allocata per l'oplog.
    * OpLog Size Left -> Quanto dell'oplog resta ancora disponibile, se l'oplog è già consumato
      questo valore sarà ~0.
    * OpLog Throughput -> Quale è la velocità con cui è consumato l'oplog.
    * OpLog TimeLeft -> Quanto tempo è rimanente nell'OpLog, se l'oplog è già consumato
      questo valore sarà ~0.

Scaling Orizzontale VS Scaling Verticale
----------------------------------------

Al fine di massimizzare l'efficacia dello scaling verticale è necessario
scaricare il lavoro di lettura sui nodi secondary del replica set.
Come indicato in :ref:`read-consistency` questo introduce una serie di
problematiche di consistenza che rendono difficile lo sviluppo dell'applicazione.

Per questa ragione si consiglia di mantenere le letture dei nodi in ``primary``
ed usare le repliche solo a fini di affidabilità.

Qualora la potenza di calcolo del ``primary`` non fosse più sufficiente a sottostare
al carico si suggerisce di scalare orizzontalmente con nuovi shard, che permetterebbe
di distribuire il carico tra i nodi senza dover rinunciare alla consistenza.

Indicatori di carico:

    * **Page Faults** -> Se il numero di page fault è elevato, è indice che la
      RAM del sistema non è sufficiente a reggere il carico.
    * **Load Average** -> Se il load average è superiore al numero di CPU disponibili
      nella macchina è indice che la CPU non è sufficiente a reggere il carico.
    * **Disk IO** -> Qualora il throuput di io su disco si avvicini a quello monitorato
      sul disco usando ``mongoperf`` è indice che le performance del disco non sono
      sufficienti. Verificare prima se l'elevato uso del disco non è in verità dovuto
      ad un insufficiente quantitativo di RAM.

Procedura Migrazione Config Server
----------------------------------

Di seguito è descritta la procedura per migrare le instanze di configurazione
su nuove macchine. La procedura richiede il down del cluster qualora gli hostname
dei config server cambino:

    1) Disabilitare il bilanciatore
    2) Spendere il config server da migrare
    3) Copiare i dati del config server da migrare sul nuovo server (anche con scp va bene)
    4) Avviare il nuovo config server
    5) Spegnere tutti i nodi del cluster: mongos, mongod, config
    6) Aggiornare l'opzione ``configDB`` nei mongos sostituendo il nuovo config
    7) Riavviare tutto il cluster
    8) Riaccendere il bilanciatore.

MongoDB Suggested Deploy
------------------------

.. image:: _static/mongodb-deploy.png

Il deploy suggerito include **3 Shard** ognuno costituito da un Replica Set di **5 nodi**,
di cui **4 operativi** ed **1 di backup** (il nodo di backup è descritto in :ref:`backup-replica`).

Questo consente di scalare sul carico tramite l'uso dello shard e fornisce l'High Availability
fino ad un massimo di *2 nodi non disponibili* grazie all'operato del nodo di backup
*anche come arbitro del replica set*.

Il numero di shard minimo consigliato è 3 in quanto generalmente si ha un beneficio in termini
di performance superati i 2 shard. Finché il numero di shard è 2 o inferiore i benifici dello
sharding sono solitamente superati dal carico di lavoro extra dovuto allo shard stesso.




