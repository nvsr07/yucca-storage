.. _architecture:

************
Architettura
************

Il seguente capitolo è suddiviso in 2 parti, nella prima parte viene spiegato e definito 
brevemente il significato e il funzionamento di un :ref:`cluster` MongoDB in :ref:`sharding`.
Sono presenti la descrizione e le definizioni dei vari elementi del cluster.

Nella seconda parte è illustrata l':ref:`deploy_architecture` del cluster specifica per la **Smart 
Data Platform**, con i nomi degli host, gli indirizzi e tutte le informazioni relative ai nodi 
che compongono il cluster.


.. _sharding:

Sharding
========

Lo sharding è un metodo di partizionamento dei dati all'interno diversi nodi detti :ref:`shard`, 
permette di operare con grosse moli di dati ed avere performance che tramite un singolo nodo 
non sarebbe possibile raggiungere.
Il partizionamento dei dati deve essere abilitato ed avviene per singola ``collection``, la 
politica di partizionamento è dettata dalla scelta della ``shard_key``.
In MongoDB lo sharding è molto flessibile e configurabile a seconda dell' esigenza, nel caso della
**Smart Data Platform**, essendo il :ref:`cluster` utilizzato per lo **Speed Layer** di inserimento 
dati, è stato configurato per favorire la velocità di inserimento dei dati, i dettagli della 
configurazione verranno forniti più avanti nella sezione relativa all' :ref:`deploy_architecture`.


.. _cluster:

Cluster
=======

Per cluster si intende l'insieme delle :ref:`shard` (ognuna delle quali a sua volta è un 
:ref:`replica_set`) dai 3 :ref:`config_server` e dalle istanze :ref:`mongos`.


.. _shard:

Shard
=====

Le ``Shard`` sono gli elementi del :ref:`cluster` dove sono contenuti i dati partizionati, nel caso 
di ``collection`` non shardate i dati saranno residenti nella :ref:`primary_shard`. 
Ciascuna ``shard`` è un :ref:`replica_set` a se stante e deve essere aggiunta al :ref:`cluster`
seguendo la procedura illustrata nel capitolo ":doc:`add_shard`".
La lettura dei dati dal :ref:`cluster` avviene tramite :ref:`mongos`, collegandosi direttamente
ad una shard tramite la shell ``mongo`` si leggeranno solo i dati partizionati presenti nella 
``shard`` stessa.

.. note:: In ambiente di sviluppo è possibile utilizzare delle singole istanze ``mongod`` al posto
	dei	:ref:`replica_set`.
	
.. _primary_shard:

Primary Shard
-------------

La ``Primary Shard`` si differenzia dalle altre :ref:`shard` per il semplice fatto che contiene
tutte le ``collection`` non shardate, non ha nulla a che fare con il nodo Primary del 
:ref:`replica_set`.


.. _config_server:

Config Servers
==============

Un ``Config Server`` è un' istanza ``mongod`` configurata appositamente per svolgere tale ruolo.
All'interno di un :ref:`cluster` devono essere sempre presenti 3 ``Config Server`` che contengono i 
**metadati** del cluster all'interno del **database** ``config``.
I **metadati** contengono le informazioni necessarie ai :ref:`mongos` per operare sui dati della
:ref:`shard` corretta.
 
Una ``collection`` shardata all'interno del :ref:`cluster` viene suddivisa in ``chunks`` tra le 
varie :ref:`shard` dal **bilanciatore**, le informazioni sulla posizione dei ``chunks`` 
all'interno del :ref:`cluster` risiedono nel **database** ``config``, ogni qualvolta avviene un 
**bilanciamento** dei ``chunks`` i **metadati** vengono aggiornati.

I 3 ``Config Server`` sono in **mirroring** (pur non essendo un :ref:`replica_set`) e devono
sempre essere disponibili, nel caso in cui anche solo 1 dei ``Config Server`` non sia disponibile 
il database contenente i **metadati** del cluster andrà in modalità **sola lettura** e non 
avverranno più operazioni di bilanciamento dei ``chunks``.

.. caution:: 
	A partire dalla versione 3.2 di MongoDB i ``Config Server`` dovranno necessariamente far parte
	di un :ref:`replica_set` configurato appositamente per agire da ``Config Server``, per questo 
	motivo non sarà più necessario avere 3 istanze ``mongod`` in mirroring ma basterà configurare 
	il :ref:`replica_set` dei ``Config Server``.


.. _mongos:

Mongos
======

Il ``mongos`` è il servizio che si occupa di eseguire le operazioni di lettura e scrittura sul 
:ref:`cluster`, si appoggia ai :ref:`config_server` per leggere i **metadati** del cluster e 
indirizzare alla :ref:`shard` le operazioni, è consigliato l'utilizzo di 1 ``mongos`` per
**application server** residente sull'application server stesso per assicurare che sia sempre 
attivo.
Lato applicativo o per la shell ``mongo`` è trattato esattamente come una istanza ``mongod``.


.. _replica_set:

Replica set
===========

Un ``Replica Set`` è un insieme di server ``mongod`` con i dati in replica, il numero di server 
può variare da 3 a 50 ma solo 7 saranno autorizzati al voto di elezione del :ref:`primary`, un 
nodo all'interno del ``Replica Set`` può agire da :ref:`primary`, :ref:`secondary` o 
:ref:`arbiter`


.. _primary:

Primary
=======

Solo un nodo all'interno del :ref:`replica_set` può essere ``primary``.
Come comportamento di default, tutte le operazioni di lettura e scrittura avvengono sul nodo 
``primary`` e in seguito vengono replicate sui :ref:`secondary` utilizzando l' ``oplog``.
All' interno del :ref:`replica_set` deve essere sempre presente un ``primary``, nel momento in cui
il ``primary`` non sarà più disponibile avverrà l'elezione del nuovo ``primary`` tra i 
:ref:`secondary` che possono essere eletti.


.. _secondary:

Secondary
=========

Tutti i nodi contenenti dati all'interno del :ref:`replica_set` sono considerati ``secondary``
(gli :ref:`arbiter` non contengono dati).
Sui nodi ``secondary`` non è possibile scrivere direttamente, i dati sono replicati dal 
:ref:`primary` ripetendo le operazioni scritte sull' ``oplog``.
È invece possibile effettuare le operazioni di lettura per alleggerire il carico di lavoro del 
:ref:`primary` ma si otterranno dei dati "vecchi" pari al tempo di replica.


.. _arbiter:

Arbiter
=======

Un nodo ``arbiter`` non contiene dati e agisce solo per la votazione del :ref:`primary`, deve 
essere presente solo nel caso in cui nel :ref:`replica_set` ci sia il rischio di avere voti pari
nel momento in cui si andrà a votare il nuovo :ref:`primary`.


.. _deploy_architecture:

Architettura di deploy
======================

Nello **Speed Layer** della **Smart Data Platform** è stato deciso di iniziare con un ambiente
di produzione composto dai seguenti nodi:

- 3 :ref:`shard`
- 3 :ref:`config_server`

Ciascuna :ref:`shard` è 1 :ref:`replica_set` composto da:

- 3 :ref:`primary`/:ref:`secondary` 
- 1 nodo non eleggibile a :ref:`primary` utilizzato per il backup


.. image:: /_static/production_cluster_schema.jpg

I 3 config server sono i seguenti:

      =============  ========================  ========  =======  =====================  ======================
      **Nodo**       **Host**                  **#CPU**  **RAM**  **Rete**               **Storage** 
      =============  ========================  ========  =======  =====================  ======================
      sdnet-config1  sdnet-config1.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 30 GB Dati
      sdnet-config2  sdnet-config2.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 30 GB Dati
      sdnet-config3  sdnet-config3.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 30 GB Dati
      =============  ========================  ========  =======  =====================  ======================

Le 3 shard sono così suddivise:

speed0
    =============  ========================  ========  =======  =====================  ======================
    **Nodo**       **Host**                  **#CPU**  **RAM**  **Rete**               **Storage** 
    =============  ========================  ========  =======  =====================  ======================
    sdnet-speed1   sdnet-speed1.sdp.csi.it   1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    sdnet-speed2   sdnet-speed2.sdp.csi.it   1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    sdnet-speed3   sdnet-speed3.sdp.csi.it   1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    =============  ========================  ========  =======  =====================  ======================

speed1
    =============  ========================  ========  =======  =====================  ======================
    **Nodo**       **Host**                  **#CPU**  **RAM**  **Rete**               **Storage** 
    =============  ========================  ========  =======  =====================  ======================
    sdnet-speed11  sdnet-speed11.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    sdnet-speed12  sdnet-speed12.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    sdnet-speed13  sdnet-speed13.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    =============  ========================  ========  =======  =====================  ======================

speed2
    =============  ========================  ========  =======  =====================  ======================
    **Nodo**       **Host**                  **#CPU**  **RAM**  **Rete**               **Storage** 
    =============  ========================  ========  =======  =====================  ======================
    sdnet-speed21  sdnet-speed21.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    sdnet-speed22  sdnet-speed22.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    sdnet-speed23  sdnet-speed23.sdp.csi.it  1         4 GB     1 GB dati/1 GB backup  20 GB SO + 110 GB Dati
    =============  ========================  ========  =======  =====================  ======================

I 3 nodi di **backup** sono tutti sullo stesso host ``sdnet-speed-restore`` in modo da avere
una singola macchina dalla quale gestire i backup delle 3 :ref:`shard`:

    ===================  ==============================  ========  =======  =====================  ======================
    **Nodo**             **Host**                        **#CPU**  **RAM**  **Rete**               **Storage** 
    ===================  ==============================  ========  =======  =====================  ======================
    sdnet-speed-restore  sdnet-speed-restore.sdp.csi.it  
    ===================  ==============================  ========  =======  =====================  ======================




















