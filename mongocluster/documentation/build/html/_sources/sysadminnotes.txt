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

Monitoraggio Cluster
--------------------

Per quanto riguarda il monitoraggio del cluster è stato scelto di utilizzare nagios tramite 
l'apposito plugin (https://github.com/mzupan/nagios-plugin-mongodb) già presente con il supporto
alla versione 3.0 di mongodb


Di seguito sono riportati i valori rilevanti monitorati da Nagios, per ognuno di essi è indicato 
il significato, quale è il loro valore all'interno del monitoraggio e la soglia di attenzione:

    * **Load Average**
      Se il load average è superiore al numero di CPU disponibili
      nella macchina è indice che la CPU non è sufficiente a reggere il carico.
    * **Disk IO**
      Qualora il throuput di io su disco si avvicini a quello monitorato
      sul disco usando ``mongoperf`` è indice che le performance del disco non sono
      sufficienti. Verificare prima se l'elevato uso del disco non è in verità dovuto
      ad un insufficiente quantitativo di RAM.
    * **Check fs /data**
      La directory ``/data`` è dove MongoDB solitamente salva i file del database,
      è importante monitorarne l'occupazione in quanto in caso di necessità di riparare
      il DB sarà necessario che la sua occupazione non superi il 50%.
    * **check_connect_primary**
      Riporta se il nodo monitorato riesce a collegarsi al primary del replica set
      a cui appartiene in tempi rapidi. E` utile a monitorare la qualità di connessione
      verso il primary node del replicat, cosa che influenza la velocità di replicazione.
      Qualora il nodo sia il primary stesso non da informazioni utili.
    * **check_page_faults**
      Riporta il numero di page fault effettuati durante l'uso del database, qualora il
      numero sia significativo significa che il database non ha sufficiente memoria per
      gestire il carico di lavoro, con il passaggio a wiredTiger questo warning dovrebbe 
      presentarsi con molta meno frequenza.
    * **check_flushing**
      Riporta il tempo richiesto per la scrittura su disco degli inserimenti, se questo valore
      è troppo alto può rallentare la scrittura e replicazione dell'oplog a causa del lock che
      viene posto sulle operazione. Se la soglia di errore viene raggiunta può essere necessario
      operare per migliorare le prestazioni del disco. O aumentare la RAM per ridurre l'impatto
      sul disco.
    * **check_connections**
      Monitora il numero di connesisoni usate tra quelle disponibili per i client. Quando il
      massimo viene raggiunto non sarà più possibile connettersi per i client.
    * **check_lock**
      Monitora la % che il database spende bloccato sui lock delle operazioni. Questo valore
      dovrebbe essere sempre minimizzato in quanto impedisce a qualsiasi operazione di procedere
      inclusa la replicazione.
    * **check_memory**
      Di questo valore è importante tenere presente i valori ``mapped`` che sta ad indicare la
      dimensione massima utile perché il DB lavori senza uso del disco e ``resident`` che indica
      quanta memoria è in uso effettivamente da MongoDB.
    * **check_replica_primary**
      Verifica se è cambiato il primary node del replica set.
    * **check_rep_lag**
      Verifica la latenza di replicazione, quindi quanto i secondary node sono in ritardo rispetto
      al nodo primary. Eventuali backups andrebbero sempre effettuati quando i nodi sono in pari.
      Se la replicazione è molto in ritardo può essere dovuto alle seguenti cause:

        * Lentezza di rete tra i nodi
        * Troppo carico di operazioni sul primary node
        * Operazioni che detengono il "lock" a lungo (verificabile con ``db.currentOp()``)
        * Secondary troppo lenti a scrivere
    * **check_queries_per_second**
      Questo valore sta ad indicare le query al secondo effettuate dal sistema, se è alto
      può avere effetto sulla *check_rep_lag*.
    * **check_oplog**
	  Indica il tempo salvato nell'oplog, se diventa troppo basso richia di far andare i nodi
	  out of sync e rendere necessario resync iniziale.
    * **chunks_balance**
	  Verifica il corretto bilanciamento dei chunk nella shard, ad esempio se sono presenti jumbo 
	  chunk o se è presente quache problema al bilanciatore.
	  
	  
Comandi Monitoraggio Cluster
----------------------------

Analisi WorkingSet
~~~~~~~~~~~~~~~~~~

Il *WorkingSet* è il carico di dati che MongoDB deve abitualmente manipolare in base
al carico di lavoro richiesto (inserimenti e query che vengono fatte). La RAM fornita
al sistema dovrebbe essere sempre superiore al working set, per evitare swap su disco e quindi
IO bloccante all'interno di MongoDB.

Per verificare il working set è possibile tramite il comando::

    > db.runCommand( { serverStatus: 1, workingSet: 1 } )

L'output fornito conterrà una sezione::

	"workingSet" : {
		"note" : "thisIsAnEstimate",
		"pagesInMemory" : 654978,
		"computationTimeMicros" : 115617,
		"overSeconds" : 723
	},

In particolare il valore di interesse è ``pagesInMemory`` in cui è riportato il numero di
pagine di memoria usate. Di default ogni pagina di memoria ha una dimensione di ``4096 bytes``,
quindi l'occupzione del sistema in questo caso sarebbe di ``654978*4`` = ``2619912Kb`` ~ ``2.7Gb``.

Un altro valore molto importate è ``overSeconds``, questo indica il tempo passato tra la prima
e l'ultima pagina inserita in RAM. Se il valore è basso, vuole dire che il sistema sta
continuamente mettendo e togliendo pagine dalla RAM, quindi che la RAM non è sufficiente a contenere
il workingSet. Questo valore dovrebbe essere sempre di almeno qualche minuto.

Analisi Locking
~~~~~~~~~~~~~~~

Il *Locking* è il tempo che in MongoDB il sistema è fermo ad eseguire una operazione,
durante il locking nessun altra operazione può procedere, nemmeno la sincronzizazione
dei nodi nel replicaSet o il bilanciamento nello shard. Quindi è molto importante che sia
basso per permettere uno stato sano del cluster MongoDB.

Lo stato dei lock è sempre riportato dal comando
``db.runCommand( { serverStatus: 1, workingSet: 1 } )`` ed diviso in due sezioni.

``globalLock`` in cui è riportato lo stato generale del locking::

	"globalLock" : {
		"totalTime" : NumberLong(1435194000),
		"lockTime" : NumberLong(1319013),
		"currentQueue" : {
			"total" : 0,
			"readers" : 0,
			"writers" : 0
		},
		"activeClients" : {
			"total" : 0,
			"readers" : 0,
			"writers" : 0
		}
	},

e ``locks`` in cui è riportato lo stato dei lock per ogni database::

    	"locks" : {
            "." : {
                "timeLockedMicros" : {
                    "R" : NumberLong(0),
                    "W" : NumberLong(1319013)
                },
                "timeAcquiringMicros" : {
                    "R" : NumberLong(0),
                    "W" : NumberLong(6564)
                }
            },
            "admin" : {
                "timeLockedMicros" : {
                    "r" : NumberLong(19097),
                    "w" : NumberLong(0)
                },
                "timeAcquiringMicros" : {
                    "r" : NumberLong(4785),
                    "w" : NumberLong(0)
                }
            },
            "local" : {
                "timeLockedMicros" : {
                    "r" : NumberLong(72576),
                    "w" : NumberLong(970)
                },
                "timeAcquiringMicros" : {
                    "r" : NumberLong(12735),
                    "w" : NumberLong(18)
                }
            },
            "DB_elise" : {
                "timeLockedMicros" : {
                    "r" : NumberLong(34206),
                    "w" : NumberLong(320)
                },
                "timeAcquiringMicros" : {
                    "r" : NumberLong(10370),
                    "w" : NumberLong(17)
                }
            },
        }

Il valore ``r`` e ``R`` non sono problematici generalmente, essi sono lock condivisi e quindi
non bloccano in modo esclusivo il sistema. Mentre i lock di tipo ``w`` e ``W`` sono esclusivi
ed il loro tempo andrebbe minimizzato. In particolare confrontando i valori ``totalTime`` e
``lockTime`` è possibile fare il rapporto di quanta % del tempo è stato da MongoDB bloccato.

In ``currentQueue`` è poi possibile verificare se ci sono operazioni in attesa di acquisire
il lock sul database.

Si può poi verificare quali operazioni stanno causando problemi di locking tramite il comando
``db.currentOp()`` che va lanciato sul nodo che sta eseguendo l'operazione::

    > db.currentOp()
    {
        "inprog" : [
            {
                "opid" : 4882,
                "active" : true,
                "secs_running" : 1,
                "op" : "getmore",
                "ns" : "local.oplog.rs",
                "query" : {

                },
                "client" : "127.0.0.1:54403",
                "desc" : "conn134",
                "threadId" : "0x7f082add0700",
                "connectionId" : 134,
                "waitingForLock" : false,
                "numYields" : 0,
                "lockStats" : {
                    "timeLockedMicros" : {
                        "r" : NumberLong(35),
                        "w" : NumberLong(0)
                    },
                    "timeAcquiringMicros" : {
                        "r" : NumberLong(375),
                        "w" : NumberLong(0)
                    }
                }
            },
            {
                "opid" : 4881,
                "active" : true,
                "secs_running" : 1,
                "op" : "getmore",
                "ns" : "local.oplog.rs",
                "query" : {

                },
                "client" : "127.0.0.1:54402",
                "desc" : "conn133",
                "threadId" : "0x7f082aacd700",
                "connectionId" : 133,
                "waitingForLock" : false,
                "numYields" : 0,
                "lockStats" : {
                    "timeLockedMicros" : {
                        "r" : NumberLong(335),
                        "w" : NumberLong(0)
                    },
                    "timeAcquiringMicros" : {
                        "r" : NumberLong(7),
                        "w" : NumberLong(0)
                    }
                }
            }
        ]
    }

Se una delle operazioni nell'elenco sta tenendo un lock per troppo lungo tempo ed altre
sono in ``waitingForLock`` a causa sua è necessario intervenire uccidendo l'operazione con
``db.killOp(opid)``, specialmente se le operazioni in ``waitingForLock`` hanno come
campo ``ns`` il valore ``local.oplog`` in quanto vuole dire che è bloccata la replicazione
dell'oplog e quindi il replicaSet rischia di non avere dati consistenti.

Analisi ReplicaSet
~~~~~~~~~~~~~~~~~~

Lo stato del replica set può essere recuperato con il comando ``rs.status()`` eseguito
sui nodi del replica set stesso::

    > rs.status()
    {
        "set" : "rs0",
        "date" : ISODate("2015-03-04T11:45:04Z"),
        "myState" : 1,
        "members" : [
            {
                "_id" : 0,
                "name" : "LPulsar:27018",
                "health" : 1,
                "state" : 2,
                "stateStr" : "SECONDARY",
                "uptime" : 3631,
                "optime" : Timestamp(1425309401, 1),
                "optimeDate" : ISODate("2015-03-02T15:16:41Z"),
                "lastHeartbeat" : ISODate("2015-03-04T11:45:03Z"),
                "lastHeartbeatRecv" : ISODate("2015-03-04T11:45:03Z"),
                "pingMs" : 0,
                "syncingTo" : "LPulsar:27020"
            },
            {
                "_id" : 2,
                "name" : "LPulsar:27020",
                "health" : 1,
                "state" : 1,
                "stateStr" : "PRIMARY",
                "uptime" : 3633,
                "optime" : Timestamp(1425309401, 1),
                "optimeDate" : ISODate("2015-03-02T15:16:41Z"),
                "electionTime" : Timestamp(1425465873, 1),
                "electionDate" : ISODate("2015-03-04T10:44:33Z"),
                "self" : true
            },
            {
                "_id" : 3,
                "name" : "LPulsar:27019",
                "health" : 1,
                "state" : 2,
                "stateStr" : "SECONDARY",
                "uptime" : 3629,
                "optime" : Timestamp(1425309401, 1),
                "optimeDate" : ISODate("2015-03-02T15:16:41Z"),
                "lastHeartbeat" : ISODate("2015-03-04T11:45:03Z"),
                "lastHeartbeatRecv" : ISODate("2015-03-04T11:45:04Z"),
                "pingMs" : 0,
                "syncingTo" : "LPulsar:27020"
            }
        ],
        "ok" : 1
    }

Nel comando è riportato quali nodi sono in stato ``SECONDARY`` e quale è il ``PRIMARY``,
in particolare è indicato il campo ``optime`` in cui è indicato a quale punto dell'oplog
il nodo è arrivato. Questo dovrebbe coincidere tra tutti e tre i nodi affinché questi siano in
sincrono. Il campo ``optimeDate`` poi riporta a che data è aggiornato il nodo. Se un nodo è
troppo vecchio non potrà essere considerato contenere dei dati coerenti.

I campi ``lastHeartbeat`` e ``electionTime`` riportano invece rispettivamente l'ultima data
in cui i nodi sono riusciti a contattare gli altri nodi e la data in cui è stato eletto il
PRIMARY corrente.

Se ``optime`` figura troppo indietro può avere senso verificare in ordine:

    - Stato dei lock per essere certi che la replicazione non sia bloccata da altri processi
    - Performance della rete, per essere certi che i nodi riescano a trasferire quanto devono
      (La rete deve poter sostenere un throughput pari a *numero inserimenti al secondo* per
      *avgObjSize* riportato da ``db.stats()``).
    - Performance del disco, queste possono essere verificate con il tool ``mongoperf``.
      (Ugualmente dovrebbero sostenere gli inserimenti al secondo per l'avgObjSize riportato
      da ``db.stats()``)

Analisi Bilanciatore Sharding
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Lo stato dello sharding può esssere verificato con il comando ``sh.status()``, esso
riporta per ognuno delle collezioni se sono in sharding (``partitioned: true``) e
quale è lo stato dei chunks::

    > sh.status()
    --- Sharding Status ---
      sharding version: {
        "_id" : 1,
        "version" : 4,
        "minCompatibleVersion" : 4,
        "currentVersion" : 5,
        "clusterId" : ObjectId("5491e70b39a5e85efe178d6e")
      }
      shards:
        {  "_id" : "replshard1",  "host" : "REPLICASET1_HOSTS" }
        {  "_id" : "replshard2",  "host" : "REPLICASET2_HOSTS" }
      databases:
        {  "_id" : "DB_SUPPORT",  "partitioned" : false,  "primary" : "replshard1" }
        {  "_id" : "DB_csp",  "partitioned" : true,  "primary" : "replshard2" }
    DB_csp.measures
        shard key: { "idDataset" : 1, "datasetVersion" : 1, "time" : 1 }
        chunks:
            replshard1 3
            replshard2 3
            {
                "idDataset" : { "$minKey" : 1 },
                "datasetVersion" : { "$minKey" : 1 },
                "time" : { "$minKey" : 1 }
            } -->> {
                "idDataset" : 2,
                "datasetVersion" : 2,
                "time" : ISODate("2014-12-01T00:00:00Z")
            } on : replshard1 Timestamp(3, 0)
            {
                "idDataset" : 2,
                "datasetVersion" : 2,
                "time" : ISODate("2014-12-01T00:00:00Z")
            } -->> {
                "idDataset" : 2,
                "datasetVersion" : 2,
                "time" : ISODate("2014-12-02T12:52:00Z")
            } on : replshard2 Timestamp(2, 7)
            {
                "idDataset" : 43,
                "datasetVersion" : 1,
                "time" : ISODate("2014-12-10T17:00:00Z")
            } -->> {
                "idDataset" : { "$maxKey" : 1 },
                "datasetVersion" : { "$maxKey" : 1 },
                "time" : { "$maxKey" : 1 }
            } on : replshard1 Timestamp(2, 0)

I chunk sono i sottoinsiemi di dati che il bilanciatore ha deciso di dividere tra i vari
shard, sotto la voce ``chunks:`` sono indicati quanti chunk sono presenti su ognuno degli
shard. Questi valori dovrebbero essere quasi uguali tra gli shard, se così non fosse
vuole dire che il bilanciatore è spento (verificabile con ``sh.getBalancerState()``) o che
sono presenti dei chunk ``jumbo``.

I chunk jumbo sono insiemi di dati che a causa di una shard key non sufficientemente granulare
MongoDB non è stato in grado di dividere in sottoinsiemi. Questi sono identificabili dal fatto
che vicino alla descrizione del chunk riportata dal comando compare la parola ``jumbo``.

Un'altra comune causa di una distribuzione inequa dei chunk è nel caso in cui la shard key
non riesca a garantire sufficiente cardinalità dei dati, se ad esempio la shard key è
sequenziale, tutti i dati verranno scritti assieme sullo stesso nodo ed il bilanciatore potrebbe
poi non riuscire a spostarli tra i nodi abbastanza in fretta, oltre a sovraccaricare il sistema
in quanto il dato deve essere:

    1. Scritto sul nodo 1
    2. Letto dal nodo 1
    3. Scritto sul nodo 2
    4. Cancellato dal nodo 1

Quindi per ogni scrittura vengono fatte poi altre 3 operazioni per ribilanciare i dati.

Un'altra possibile causa di problemi di bilanciamento è se viene impostata una *Balancing Window*,
(finestra di orari in cui il bilanciatore può girare per non sovraccarirare il sistamte)
essa potrebbe non essere sufficientemente grande da permettete al bilanciatore di spostare i dati.


Procedura Migrazione Config Server
----------------------------------

Di seguito è descritta la procedura per migrare le instanze di configurazione
su nuove macchine. La procedura richiede il down del cluster qualora gli hostname
dei config server cambino:

    1) Disabilitare il bilanciatore
    2) Spegnere il config server da migrare
    3) Copiare i dati del config server da migrare sul nuovo server (anche con scp va bene)
    4) Avviare il nuovo config server
    5) Spegnere tutti i nodi del cluster: mongos, mongod, config
    6) Aggiornare l'opzione ``configdb`` nei mongos sostituendo il nuovo config
    7) Riavviare tutto il cluster
    8) Riaccendere il bilanciatore.

.. note::

    Lo spegnimento di un config server impedisce la migrazione dei chnuk sul database,
    ma permette al sistema di continuare a funzionare anche in scrittura.

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




