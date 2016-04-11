Analisi risorse necessarie al cluster
=====================================

In seguito all'aumentare della mole di dati contenuti nel cluster, all' aumentare dei nodi o all'
aumento della quantità di inserimenti al secondo, per mantenere il throughput adeguato è necessario
adeguare le risorse per ogni singolo nodo.
Il seguente documento illustra come evolvere il cluster in seguito all' aumentare dei dati diviso
per tipologia di risorsa.

Memoria RAM
-----------

La RAM è la risorsa maggiormente utilizzata da MongoDB ed è quella che necessita di essere tenuta
sotto controllo maggiormente in quanto impattata da diversi fattori.

Connection Pool
...............

È la RAM richiesta per le varie connessioni ai mongod o mongos, ciascuna connessione occupa minimo 
1 MB di memoria, essendoci nodi in replica, tool di monitoraggio e svariati mongos che si collegano
al cluster è un valore di cui si deve tenere conto.
Nello stato attuale del cluster (3 shard composte ciascuna da 3 nodi in replica) il calcolo per
il connection pool dei singoli nodi è il seguente:

numSecondariesInReplica + numMonitoring + numShard + numMongos + numConfig

Per quanto riguarda invece i mongos il connection pool va calcolato nel seguente modo:

(((maxPrimaryConnectionsYouWant - numSecondariesInReplica*3 - monitoring*2) / numMongos) * 0.9

Woking Set
..........

Il working set è la somma dello spazio occupato in memoria dagli indici e dall'active set, non è un
valore calcolabile in quanto è variabile con l'utilizzo che si fa dei dati, l'active set non è
necessario che rimanga sempre nella RAM, gli indici invece sarebbe meglio se vi fossero tutti
contenuti (valore monitorato). In ogni caso mongoDB si occuperà di tenere sempre in memoria gli 
indici più utilizzati.
Per questo è necessario monitorare la dimensione occupata dagli indici e agire di conseguenza sulla
quantità di memoria RAM necessaria


Banda richiesta
---------------

Per calcolare la banda richiesta da mongodb, in particolare per permettere almeno ai nodi secondary
di rimanere sempre sincronizzati con i nodi primary, la formula è la seguente:

op/sec * docsize + 40% 

Stimando una dimensione media dei documenti di 256 byte e una quantità di inserimenti pari a 
10000/s risulta che:

(10000 * 256 byte) + 40% = 3584000 b/s 

