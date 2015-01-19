======================================
Code Review MongoDBOutEventAdaptorType
======================================

Abstract
========

Nel seguente documento si riportano i risultati della code review sull'Event
Adaper mongoDB del CEP. Per farlo oltre alla lettura del codice è stato svolto
un piccola prova di simil-benchmarking per valutare il comportamento
dell'Adapter dopo i tweaking ipotizzati.

Introduzione
============

Per simulare gli eventi sulla piattaforma WSO2 CEP è stato utilizzato un plugin
di terze parti, poiché l'Event simulator standard del CEP 3.1 permette di
scaturire un singolo evento alla volta.
La simulazione di inserimento quindi è stata effettuato utilizzando
(https://github.com/ujitha/CEP_EventSimulator).
Premesso che non abbiamo grandi conoscenze della piattaforma WSO2 CEP abbiamo
dedicato del tempo per lo studio dei componenti della piattaforma CEP. Si è
confermato che tutte le operazioni di review del codice che influiscono sulla
scrittura su mongo sono limitate all'interno dell'adapter per mongoDB in
particolare la class `MongoDBOutEventAdaptorType` del repo `yucca-realtime`.

Ogni prova di inserimento ha solo scopi indicativi e non un preciso benchmark,
ed è stato effetuato 4 volte su un dataset di 1M di entry utilizzando il
simulatore multiplo di eventi.


WriteConcern
============

http://docs.mongodb.org/manual/core/write-concern/

http://api.mongodb.org/java/2.12/com/mongodb/WriteConcern.html

Un aumento notevole delle prestazioni di scrittura si nota andando a
cambiare il WriteConcern (**w**) delle **insert**.

Il Write Concern serve per attestare che un'operazione di scrittura sia andata a
buon fine e definisce il livello di attestazione. Di default il driver compie le
operazione di scrittura in modalità `ACKNOWLEDGE`, ogni  volta che viene
effettuata una scrittura il driver aspetterà una conferma dal nodo principale.
Questo comporta un throughput peggiore in scrittura ma allo stesso tempo
assicura che i dati siano sempre aggiornati. Nel caso di un ambiente in
replication il WriteConcern può essere impostato ad un valore maggiore di 1 che
indica il numero di nodi che devono aver inserito i dati prima di compiere
un'altra operazione.

Per avere il miglior throughput in scrittura la soluzione migliore è utilizzare
la madalità `UNACKNOWLEDGE`, in questo modo il driver si occuperà di sollevare
un errore solo nel caso in cui il DB non sia raggiungibile e compirà tutte le
operazioni di scrittura senza aspettare la risposta dal server

Risultati
=========

.. image:: _static/reviewMongoDBOutEventAdaptorType/WriteConcern.png

|

.. list-table:: **No Review** (default ``WriteConcern.AKNOWLEDGE``):
    :widths: 15 15
    :header-rows: 0

    * - User timing: 67390000000 ns
      - System timing: 46120000000 ns

    * - User timing: 70410000000 ns
      - System timing: 47410000000 ns

    * - User timing: 67120000000 ns
      - System timing: 43260000000 ns

    * - User timing: 68000000000 ns
      - System timing: 46020000000 ns

|

.. list-table:: **writeConcern** ``WriteConcern.UNACKNOWLEDGE``
    :widths: 15 15
    :header-rows: 0

    * - User timing: 25580000000 ns
      - System timing: 30460000000 ns

    * - User timing: 27300000000 ns
      - System timing: 31530000000 ns


    * - User timing: 27720000000 ns
      - System timing: 30000000000 ns


    * - User timing: 26440000000 ns
      - System timing: 29780000000 ns

Conclusioni
===========

In seguito alla review del codice, in particolare r231:237, si conclude che la
procedura di scrittura è corretta, l'unica miglioria che potrebbe essere
implementata è cambiare il Write Concern, alla r:237 aggiungere il writeConcern
come parametro alla chiamata insert del documento, che incrementa di circa il
100% la velocità di scrittura.

Riportiamo l'esempio di seguito::

    coll.insert(dbo, WriteConcern.UNACKNOWLEDGE);

Utilizzando ``WriteConcern.UNACKNOWLEDGE``, invece che il valore di default
``WriteConcern.ACKNOWLEDGE``, si otteniene un throughput di inserimento
migliorato del 100%,
d'altra parte in lettura i dati non saranno disponibili in tempo reale ma ci
sarà qualche ms di ritardo, inoltre non si avrà la certezza assoluta della
corretta scrittura.
