Best Practices di sicurezza del cluster MongoDB
===============================================

Sono riportate alcune pratiche che sarebbe meglio applicare per assicurare la sicurezza di un 
cluster mongodb.

Sicurezza di rete
-----------------

È buona norma non esporre il database mongodb direttamente su Internet, avere i server accessibili 
dall'esterno rende il cluster esposto ad attacchi DoS (Denial of Service) e combinata con una 
scarsa gestione degli accessi può portare ad una elevata vulnerabilità.

Per questo motivo è bene configurare i firewall e la rete con un' architettura multistrato in modo 
che i nodi siano accessibili solo nella rete locale o in una vps nella quale saranno presenti i
mongos e gli application server.

Scripting server side
---------------------

MongoDB permette l'esecuzione arbitraria di javascript server side per le operazioni di 
``mapReduce``, ``group``, ``$where`` ed ``eval``, per questo motivo se i suddetti comandi non 
vengono utilizzati è buona norma disabilitare l'esecuzione di javascript lato server tramite 
l'opzione ``noscripting=true`` nei file di configurazione.

Interfaccia Web di MongoDB
--------------------------

Le opzioni ``net.http.JSONPEnabled`` e ``net.http.RESTInterfaceEnabled`` abilitano l'interfaccia
web del server, di default sono entrambe disabilitate, si raccomanda caldamente di lasciarle 
disabilitate.

Abilitare SSL
-------------

Si consiglia di abilitare SSL per proteggere le comunicazioni di rete, SSL non va ad impattare 
negativamente le prestazioni del cluster, si avrà un miglioramento del throughput di rete grazie 
alla compressione dei dati e un leggero peggiormento computazionale dovuto alla cifratura.
Per poter abilitare SSL è necessario avere una distribuzione mongodb compatibile con SSL, la 
versione per red hat enterprise soddisfa questo requisito, inoltre è necessario un certificato 
rilasciato da una CA (Certificate Authority).

Per abilitare SSL:

	* Abilitare SSL sui nodi del cluster, sui config server e sui mongos andando ad editare il 
	  file di configurazione con i seguenti valori e riavviando ciascuna istanza::
		
		sslMode = preferSSL
		sslPEMKeyFile = /etc/ssl/mongodb.pem
		sslPEMKeyPassword = encryptionPassword
		sslCAFile = /etc/ssl/CA.pem
		
	* Una volta effettuata l'operazione su tutte le istanze configurare le applicazioni e i client
	  mongo in modo che utilizzino SSL, si rimanda alla documentazione del driver JAVA
	  

	* Editare i file di configurazione dei vari nodi, dei config e dei mongos in modo che vengano 
	  solo accettate connessioni via SSL, in seguito riavviare le istanze per applicare la 
	  modifica::
	
		sslMode = requireSSL

Role-Based Access Control
-------------------------

Si raccomanda di utilizzare e configurare le utenze di mongodb in modo che abbiano solo i permessi
di cui effettivamente necessitino, mongodb offre una serie di ruoli predefiniti che possono essere
comodamente utilizzati per evitare di dare permessi non necessari agli utenti che hanno accesso al
cluster (https://docs.mongodb.org/manual/core/security-built-in-roles/)
