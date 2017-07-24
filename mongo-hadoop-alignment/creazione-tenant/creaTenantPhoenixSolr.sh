#!/bin/bash
### crea le collection Phoenix per il tenant specificato
### crea le collection Solr per il tenant specificato
### attiva l'indexer per la replica automatica da Phoenix a Solr
#
echo "**** $0 start at "`date +%Y%m%d-%H%M%S`
#
if [ "$#" -ne "6" ] || [ "$2" != "prod" -a  "$2" != "preprod" ]; then
   echo "USAGE : creaTenantPhoenixSolr.sh nometenant prod|preprod collectionData collectionMeasures collectionMedia collectionSocial"
  echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
  exit 1
fi
#
cd /home/sdp/scripts
. ./prod.conf
collSolr=( $3 $4 $5 $6 )
if [ ${2^^} = "PROD" ]
then
    tenantCode=$1
else
	tenantCode="int_"$1
fi
# per Phoenix, converte tutto in maiuscolo - estensione bash
tenantCode=${tenantCode^^}
#rimozione SDP_ prefisso se presente
tenantCode=${tenantCode#SDP_}
 
# verifica presenza ticket Kerberos
kinit -kt /etc/security/keytabs/sdp.service.keytab sdp/sdnet-master4.sdp.csi.it@SDP.CSI.IT
klist
#
# crea tabelle e indici su phoenix

myPid=$$

phoenix-sqlline sdnet-master1.sdp.csi.it,sdnet-master2.sdp.csi.it,sdnet-master3.sdp.csi.it,sdnet-master4.sdp.csi.it,sdnet-master5.sdp.csi.it > elenco_tabelle_phoenix.$mypid.txt <<EOF
!outputformat csv
!tables
EOF

grep "'SDP_${tenantCode}','DATA'" elenco_tabelle_phoenix.$mypid.txt > /dev/null
if [ $? -eq 0 ]; then 
    echo "Tabella Phoenix SDP_${tenantCode}.DATA già esistente "
else
	phoenix-sqlline sdnet-master1.sdp.csi.it,sdnet-master2.sdp.csi.it,sdnet-master3.sdp.csi.it,sdnet-master4.sdp.csi.it,sdnet-master5.sdp.csi.it <<EOF
CREATE TABLE IF NOT EXISTS SDP_${tenantCode}.DATA (
  iddataset_l       INTEGER NOT NULL,
  datasetversion_l  INTEGER NOT NULL,
  id                VARCHAR NOT NULL,
  CONSTRAINT pk_${tenantCode}_DATA PRIMARY KEY (iddataset_l, datasetversion_l, id));
EOF
	
	# aggiunge replication su hbase per successiva copia automatica su Solr (tramite indexer)
	hbase shell <<EOF
disable "SDP_${tenantCode}.DATA"
alter "SDP_${tenantCode}.DATA", {NAME => '0', REPLICATION_SCOPE => '1'}
enable "SDP_${tenantCode}.DATA"
EOF
fi

grep "'SDP_${tenantCode}','MEASURES'" elenco_tabelle_phoenix.$mypid.txt > /dev/null
if [ $? -eq 0 ]; then 
    echo "Tabella Phoenix SDP_${tenantCode}.MEASURES già esistente "
else
	phoenix-sqlline sdnet-master1.sdp.csi.it,sdnet-master2.sdp.csi.it,sdnet-master3.sdp.csi.it,sdnet-master4.sdp.csi.it,sdnet-master5.sdp.csi.it <<EOF
CREATE TABLE IF NOT EXISTS SDP_${tenantCode}.MEASURES (
  iddataset_l       INTEGER NOT NULL,
  datasetversion_l  INTEGER NOT NULL,
  time_dt           TIMESTAMP NOT NULL,
  id                VARCHAR NOT NULL,
  sensor_s          VARCHAR,
  streamcode_s      VARCHAR,
  CONSTRAINT pk_${tenantCode}_MEASURES PRIMARY KEY (iddataset_l, datasetversion_l, time_dt, id));
EOF

	hbase shell <<EOF
disable "SDP_${tenantCode}.MEASURES"
alter "SDP_${tenantCode}.MEASURES", {NAME => '0', REPLICATION_SCOPE => '1'}
enable "SDP_${tenantCode}.MEASURES"
EOF
fi

grep "'SDP_${tenantCode}','MEDIA'" elenco_tabelle_phoenix.$mypid.txt > /dev/null
if [ $? -eq 0 ]; then 
    echo "Tabella Phoenix SDP_${tenantCode}.MEDIA già esistente "
else
	phoenix-sqlline sdnet-master1.sdp.csi.it,sdnet-master2.sdp.csi.it,sdnet-master3.sdp.csi.it,sdnet-master4.sdp.csi.it,sdnet-master5.sdp.csi.it <<EOF
CREATE TABLE IF NOT EXISTS SDP_${tenantCode}.MEDIA (
  iddataset_l       INTEGER NOT NULL,
  datasetversion_l  INTEGER NOT NULL,
  id                VARCHAR NOT NULL,
  CONSTRAINT pk_${tenantCode}_MEDIA PRIMARY KEY (iddataset_l, datasetversion_l, id));
EOF

	# aggiunge replication su hbase per successiva copia automatica su Solr (tramite indexer)
	hbase shell <<EOF
disable "SDP_${tenantCode}.MEDIA"
alter "SDP_${tenantCode}.MEDIA", {NAME => '0', REPLICATION_SCOPE => '1'}
enable "SDP_${tenantCode}.MEDIA"
EOF
fi

grep "'SDP_${tenantCode}','SOCIAL'" elenco_tabelle_phoenix.$mypid.txt > /dev/null
if [ $? -eq 0 ]; then 
    echo "Tabella Phoenix SDP_${tenantCode}.SOCIAL già esistente "
else
	phoenix-sqlline sdnet-master1.sdp.csi.it,sdnet-master2.sdp.csi.it,sdnet-master3.sdp.csi.it,sdnet-master4.sdp.csi.it,sdnet-master5.sdp.csi.it <<EOF
CREATE TABLE IF NOT EXISTS SDP_${tenantCode}.SOCIAL (
  iddataset_l       INTEGER NOT NULL,
  datasetversion_l  INTEGER NOT NULL,
  time_dt           TIMESTAMP NOT NULL,
  id                VARCHAR NOT NULL,
  sensor_s          VARCHAR,
  streamcode_s      VARCHAR,
  CONSTRAINT pk_${tenantCode}_SOCIAL PRIMARY KEY (iddataset_l, datasetversion_l, time_dt, id));

EOF

	# aggiunge replication su hbase per successiva copia automatica su Solr (tramite indexer)
	hbase shell <<EOF
								
																	   
							   
									
																		   
								   
								 
																		
								
disable "SDP_${tenantCode}.SOCIAL"
alter "SDP_${tenantCode}.SOCIAL", {NAME => '0', REPLICATION_SCOPE => '1'}
enable "SDP_${tenantCode}.SOCIAL"
EOF
fi

rm elenco_tabelle_phoenix.$mypid.txt


#
# per Solr converte tutto in minuscolo - estensione bash
tenantCode=${tenantCode,,}
collSolr=( ${collSolr[*],,} )
urlSolr="https://sdnet-knox.sdp.csi.it:8443/gateway/default/solr/admin/collections?action="
#
### crea le collection Solr per il tenant specificato:
### crea prima gli shard su nodi adiacenti da 1 a 5, senza ulteriori repliche; poi aggiunge le repliche sui nodi da 7 a 11
### cosi' dividiamo il nostro cluster in modo da facilitare la manutenzione
###
																		  
optSolr="&numShards=2&replicationFactor=1&maxShardsPerNode=1&createNodeSet="
zooKeeperClient="/opt/lucidworks-hdpsearch/solr/server/scripts/cloud-scripts/zkcli.sh"
zooKeeperHost="sdnet-master1.sdp.csi.it:2181/solr"
zooKeeperConfdir="/home/sdp/scripts/solr/configsets/sdp_default/conf"
if [ ! -d $zooKeeperConfdir ] ; then
  echo "ERRORE: manca la cartella $zooKeeperConfdir"
  echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
  exit 1
fi
i=0
while [ $i -lt 4 ] ; do
  myColl=${collSolr[i]}
  i=$((i+1))
  # verifica se la collection esiste gia
  curl -u $solrUsr:$solrPwd ${urlSolr}"LIST&wt=json" | grep -i '"'${myColl}'"' >/dev/null
  if [ $? -eq 0 ]; then
    echo "Collection Solr ${myColl} già esistente "
  else
    # se non esiste, la crea
    echo "Creazione collection Solr ${myColl} "
    optSolrCurr=${optSolr}"sdnet-slave"$i".sdp.csi.it:8983_solr,sdnet-slave"$((i+1))".sdp.csi.it:8983_solr"
    $zooKeeperClient -cmd upconfig -zkhost ${zooKeeperHost} -confname ${myColl} -confdir ${zooKeeperConfdir}
    if [ $? -ne 0 ]; then
      echo "ERRORE durante configurazione ZooKeeper"
      echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
      exit 1
    fi
    rispostaCurl=`curl -u $solrUsr:$solrPwd -v "${urlSolr}CREATE&name=${myColl}${optSolrCurr}&collection.configName=${myColl}"`
    if [ $? -ne 0 ] ; then
      echo "ERRORE curl durante creazione collection Solr ${myColl}"
        echo "curl response:"
        echo $rispostaCurl
      echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
      exit 1
    else
      echo $rispostaCurl | grep 'lst name="failure"' >/dev/null
      if [ $? -eq 0 ] ; then
        echo "ERRORE Solr durante creazione collection Solr ${myColl}"
        echo "curl response:"
        echo $rispostaCurl
        echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
        exit 1
      fi
    fi
    rispostaCurl=`curl -u $solrUsr:$solrPwd -v "${urlSolr}ADDREPLICA&collection=${myColl}&shard=shard1&node=sdnet-slave"$((i+6))".sdp.csi.it:8983_solr"`
    if [ $? -ne 0 ]; then
      echo "ERRORE curl durante creazione replica shard1 ${myColl}"
      echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
      exit 1
    else
      echo $rispostaCurl | grep 'lst name="failure"' >/dev/null
      if [ $? -eq 0 ] ; then
        echo "ERRORE Solr durante creazione replica shard1 ${myColl}"
        echo "curl response:"
        echo $rispostaCurl
        echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
        exit 1
      fi
    fi
    rispostaCurl=`curl -u $solrUsr:$solrPwd -v "${urlSolr}ADDREPLICA&collection=${myColl}&shard=shard2&node=sdnet-slave"$((i+7))".sdp.csi.it:8983_solr"`
    if [ $? -ne 0 ]; then
      echo "ERRORE curl durante creazione replica shard2 ${myColl}"
      echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
      exit 1
    else
      echo $rispostaCurl | grep 'lst name="failure"' >/dev/null
      if [ $? -eq 0 ] ; then
        echo "ERRORE Solr durante creazione replica shard2 ${myColl}"
        echo "curl response:"
        echo $rispostaCurl
        echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
        exit 1
      fi
    fi
  fi
done
#
### Infine crea indexer per la replica automatica dei dati da Phoenix a Solr
hbaseIndexerCommand="/opt/lucidworks-hdpsearch/hbase-indexer/bin/hbase-indexer"
zooKeeperList="sdnet-master1.sdp.csi.it:2181,sdnet-master2.sdp.csi.it:2181,sdnet-master3.sdp.csi.it:2181,sdnet-master4.sdp.csi.it:2181,sdnet-master5.sdp.csi.it:2181/solr"
myFolder="/home/sdp/scripts"
templateIndexer="$myFolder/indexer.xml"
morphlineFile="$myFolder/morphlines.conf"
if [ ! -f $templateIndexer ] || [ ! -f $morphlineFile ] ; then
  echo "ERRORE: mancano i file $templateIndexer e $morphlineFile"
  echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
  exit 1
fi
i=0
while [ $i -lt 4 ] ; do
  myColl=${collSolr[i]}
  if [ $i -eq 0 ] ; then
    indexerName=indexer_sdp_${tenantCode}_data
    tableName="sdp_${tenantCode}.data"
  elif [ $i -eq 1 ] ; then
    indexerName=indexer_sdp_${tenantCode}_measures
    tableName="sdp_${tenantCode}.measures"
  elif [ $i -eq 2 ] ; then
    indexerName=indexer_sdp_${tenantCode}_media
    tableName="sdp_${tenantCode}.media"
  else
    indexerName=indexer_sdp_${tenantCode}_social
    tableName="sdp_${tenantCode}.social"
  fi
  # converte nome e schema della tabella Phoenix in maiuscolo - estensione bash
  tableName=${tableName^^}
  $hbaseIndexerCommand list-indexers 2>&1 | egrep ^${indexerName} >/dev/null
  if [ $? -eq 0 ]; then
    echo "Indexer ${indexerName} già esistente "
  else
    sed "s%@TABLEPHOENIX@%$tableName%g" $templateIndexer > ${myFolder}/${indexerName}.xml
    sed -i "s%@FILEMORPH@%$morphlineFile%g" ${myFolder}/${indexerName}.xml
    $hbaseIndexerCommand add-indexer -n ${indexerName} -c ${myFolder}/${indexerName}.xml -cp solr.zk=${zooKeeperList} -cp solr.collection=${myColl}
    if [ $? -ne 0 ]; then
      echo "ERRORE durante creazione indexer ${indexerName}"
      echo "**** Esecuzione interrotta at "`date +%Y%m%d-%H%M%S`
      exit 1
    fi
  fi
  i=$((i+1))
done

echo "**** $0 ended at "`date +%Y%m%d-%H%M%S`