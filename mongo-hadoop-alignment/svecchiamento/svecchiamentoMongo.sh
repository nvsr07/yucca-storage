#/bin/bash
#set -x
#
# Esegue lo svecchiamento di tutti i dataset di ciascun tenant 
# Riceve come parametro l ambiente di esecuzione (preprod / prod ) e carica il corrispondente file xxxx.conf
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure svecchiamentoMongo.sh - param = $1 "
echo ""

USAGE="USAGE: $0 prod|preprod"
if [ -z $1 ]; then
  echo "ERRORE: parametro ambiente non valorizzato"
  echo $USAGE
  exit 1
elif [ ! -e $1.conf ]; then
  echo "ERRORE: File $1.conf non trovato"
  exit 1
fi
#
# carica configurazione
. $1.conf

nomeDir=$EXP_DIR
myPid=$$
maxRecords=8035200 # 1 record/sec per 3 mesi da 31 giorni

# crea lista collection solr
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet elenco_coll_solr_osl.js > $nomeDir/lista_coll_solr.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during tenants list creation"
  exit 1
fi

# cicla sulle collection
for solrInfo in `cat $nomeDir/lista_coll_solr.$myPid.txt | sort | uniq` ;
do
  
  dataSolrCollectionName=`echo $solrInfo | awk -F\; '{print $1}'`
  measuresSolrCollectionName=`echo $solrInfo | awk -F\; '{print $2}'`
  mediaSolrCollectionName=`echo $solrInfo | awk -F\; '{print $3}'`
  socialSolrCollectionName=`echo $solrInfo | awk -F\; '{print $4}'`  

  # locka tenant
  #mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" lock_tenant.js
  #if [ $? -ne 0 ]
  #then
    #echo "FATAL ERROR during $tenantCode tenant locking"
    #exit 1
  #fi
  
  #svecchiamento 
  curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$measuresSolrCollectionName/select?q=*:*&facet=true&json.facet={dataset:{type:terms,field:iddataset_l,mincount:$((maxRecords+1))}}&rows=0&wt=json&indent=true" | grep -A1 "\"val\"" | grep -v "-" | cut -d":" -f2 | tr -d ",]}" | paste -d ";"  - - | sed -e 's/.*/&;measures/' > $nomeDir/lista_dataset.$myPid.json 
  curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$socialSolrCollectionName/select?q=*:*&facet=true&json.facet={dataset:{type:terms,field:iddataset_l,mincount:$((maxRecords+1))}}&rows=0&wt=json&indent=true"| grep -A1 "\"val\"" | grep -v "-" | cut -d":" -f2 | tr -d ",]}" | paste -d ";"  - - | sed -e 's/.*/&;social/' >> $nomeDir/lista_dataset.$myPid.json

  for riga in `cat $nomeDir/lista_dataset.$myPid.json` ;
  do
    idDataset=`echo $riga | awk -F\; '{print $1}'`
    datasetCount=`echo $riga | awk -F\; '{print $2}'`
	domain=`echo $riga | awk -F\; '{print $3}'`
	
	echo "recupero metadata dataset: "$idDataset
	
	mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1=$idDataset;" elenco_coll_mongo_osl.js > $nomeDir/lista_coll_mongo.$myPid.txt
    if [ $? -ne 0 ]
    then
      echo "ERRORE leggendo metadati da mongo, svecchiamento non effettuato"
      continue 
    fi
	
	if [ "$domain" = "data" ]
	then
	  
	  collectionName=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $1}'`
      collectionDb=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $2}'`
      solrCollectionName=$dataSolrCollectionName
  
	elif [ "$domain" = "measures" ]
	then 
	
      collectionName=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $3}'`
      collectionDb=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $4}'`
      solrCollectionName=$measuresSolrCollectionName
	  
	elif [ "$domain" = "media" ]
	then
	
	  collectionName=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $5}'`
      collectionDb=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $6}'`
	  solrCollectionName=$mediaSolrCollectionName
	  
	elif [ "$domain" = "social" ]
	then
	  
	  collectionName=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $7}'`
      collectionDb=`cat $nomeDir/lista_coll_mongo.$myPid.txt | awk -F\; '{print $8}'`
	  solrCollectionName=$socialSolrCollectionName
	
	fi
		
	#cerca l'id minimo	
	echo "curl -g -u $solrUsr:$solrPwd \"https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset&sort=id%20asc&wt=json&indent=true&rows=1&start=$((datasetCount-maxRecords))\""
	curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset&sort=id%20asc&wt=json&indent=true&rows=1&start=$((datasetCount-maxRecords))" > $nomeDir/minId.$myPid.txt
    minId=`cat $nomeDir/minId.$myPid.txt | grep "\"id\":" | cut -d":" -f2 | tr -d "\",]}"`
	echo "minId: "$minId

	if [ ! -z "$minId" ]
	then
		
		#cancella dati in eccesso da mongo
		echo mongo $MONGO_HOST:$MONGO_PORT/$collectionDb -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$collectionName'; var param2=$idDataset; var param3='$minId'" svecchia_dati_osl.js 
        mongo $MONGO_HOST:$MONGO_PORT/$collectionDb -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$collectionName'; var param2=$idDataset; var param3='$minId'" svecchia_dati_osl.js 
        if [ $? -ne 0 ]
        then
          echo "FATAL ERROR during dataset $idDataset cleaning up"
          exit 1
        fi
    else
    	
    	echo "ERRORE leggendo id minimo, svecchiamento non effettuato"
    fi
    
  done  

  # unlock tenant
  #mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" unlock_tenant.js
  #if [ $? -ne 0 ]
  #then
    #echo "WARNING : tenant ${tenantCode} unlocking FAILED"
    #echo ""
  #fi
done

# elimino lista tenant
rm $nomeDir/lista_coll_solr.$myPid.txt
rm $nomeDir/lista_coll_mongo.$myPid.txt
rm $nomeDir/lista_dataset.$myPid.json
rm $nomeDir/minId.$myPid.txt

echo " *** `date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ***  procedure svecchiamentoMongo.sh successfully executed *** "
