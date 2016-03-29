.. _add_shard:

================================
Aggiunta di una shard al cluster
================================

Una :ref:`shard` deve essere obbligatoriamente un :ref:`replica_set`, nel caso in cui non sia già 
stato configurato un nuovo :ref:`replica_set`, è necessario procedere alla sua inizializzazione
seguendo quanto spiegato nel capitolo :doc:`init_replica_set`.

L' operazione di aggiunta di una nuova :ref:`shard` può essere eseguita "a caldo" senza necessità 
di dare disservizio, considerare comunque che sarà poi necessario che il ``balancer`` migri i
``chunk`` di dati e potrebbe causare un degrado del servizio.

Per comodità la :ref:`shard` aggiunta verrà chiamata ``speed3``, il relativo host scelto per
fare l'aggiunta al :ref:`cluster` sarà ``speed3/sdnet-speed31.sdp.csi.it``.
Nel caso in cui si voglia aggiungere una :ref:`shard` con un nome diverso, sarà necessario 
utilizzare i nomi corretti.

Collegamento a mongos
=====================

Collegarsi tramite ssh ad un nodo avente un :ref:`mongos` in esecuzione::

    $ ssh sdnet-speed1.sdp.csi.it
    
Collegarsi al :ref:`mongos` utilizzando la shell ``mongo`` e autenticarsi::

    $ mongo --port 27019
    > use admin
    > db.auth( |db_user| , |db_password| )

Aggiunta della shard
====================

Aggiungere la :ref:`shard` al :ref:`cluster`, specificando prima il nome del :ref:`replica_set` 
seguito dall' host di uno dei membri::

    > sh.addShard( "speed3/sdnet-speed31.sdp.csi.it" )